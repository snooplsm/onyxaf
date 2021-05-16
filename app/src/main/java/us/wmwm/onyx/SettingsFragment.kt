package us.wmwm.onyx

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ListAdapter
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.common.ControllerSetting
import us.wmwm.onyx.databinding.DisconnectViewBinding
import us.wmwm.onyx.databinding.FragmentSettingsBinding
import us.wmwm.onyx.db.BluetoothDvc

class SettingsFragment : Fragment() {

    val vm: SettingsViewModule by sharedViewModel()

    val reviewVm: ReviewSettingsBottomSheetViewModel by sharedViewModel()

    var _b: FragmentSettingsBinding? = null
    val b: FragmentSettingsBinding
        get() = _b!!

    val adapter = SettingsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentSettingsBinding.inflate(inflater, container, false)
        return b.root
    }

    var onValueChanged: (ControllerSetting, Int) -> Unit =
        { controllerSetting: ControllerSetting, i: Int ->
            vm.onValueChanged(controllerSetting, i)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val space = resources.getDimension(R.dimen.five)
        val twenty = resources.getDimension(R.dimen.twenty)
        b.recyclerView.clipChildren = false
        b.recyclerView.clipToPadding = false
        b.recyclerView.addItemDecoration(
            SpaceItemDecoration(
                firstTop = space,
                lastBottom = twenty * 5,
                top = space,
                bottom = space,
                start = twenty,
                end = twenty
            )
        )
        adapter.onValueChanged = onValueChanged
        b.recyclerView.adapter = adapter

        vm.needToReadFlashData.observe(viewLifecycleOwner, {
            b.recyclerView.gone()
            b.tryAPreset.gone()
            b.readData.visible()
        })
        vm.flashData.observe(viewLifecycleOwner, {
            b.recyclerView.visible()
            b.tryAPreset.visible()
            b.readData.gone()
        })
        b.readFromController.setOnClickListener {
            b.readFromController.isEnabled = false
            vm.readFromController()
        }
        vm.controllerSettings.observe(viewLifecycleOwner, {
            //adapter.submitList(emptyList())
            adapter.submitList(it)
        })

        vm.onReview.observe(viewLifecycleOwner, Observer {
            it.consume() ?: return@Observer
            reviewVm.init(it.peek)
            ReviewSettingsBottomSheetDialogFragment().show(childFragmentManager, "rsbs")
        })

        vm.reviewChanges.observe(viewLifecycleOwner, Observer {
            it.consume() ?: return@Observer
            TransitionManager.beginDelayedTransition(b.review.parent as ViewGroup)
            val vis = it.peek
            if (vis) {
                b.review.visible()
            } else {
                b.review.hide()
            }
        })

        b.review.setOnClickListener {
            vm.onReviewClicked()
        }

        val str =
            resources.getString(R.string.have_no_clue_what_to_change_try_a_preset_to_get_the_ball_rolling)
        str.replaceWithSpans {
            when (it) {
                resources.getString(R.string.preset) -> {
                    SpannableStringBuilder(it).apply {
//                        setSpan(UnderlineSpan(), 0,it.length,SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                PresetBottomSheetDialogFragment().show(
                                    childFragmentManager,
                                    "preset"
                                )
                            }
                        }, 0, it.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                else -> SpannableStringBuilder()
            }
        }.run {
            b.tryAPreset.text = this
            b.tryAPreset.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

