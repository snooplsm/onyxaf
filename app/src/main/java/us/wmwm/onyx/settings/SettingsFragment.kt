package us.wmwm.onyx.settings

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.Slide
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.*
import us.wmwm.onyx.common.ControllerSetting
import us.wmwm.onyx.common.SpaceItemDecoration
import us.wmwm.onyx.databinding.FragmentSettingsBinding
import us.wmwm.onyx.preset.PresetBottomSheetDialogFragment

class SettingsFragment : Fragment() {

    val vm: SettingsViewModule by sharedViewModel()

    val reviewVm: ReviewSettingsBottomSheetViewModel by sharedViewModel()

    val t: OnyxTracker by inject()

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
            b.readFromController.isEnabled = true
            b.review.gone()
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
            adapter.submitList(it)
            val str =
                resources.getString(R.string.have_no_clue_what_to_change_try_a_preset_to_get_the_ball_rolling)
            str.replaceWithSpans {
                when (it) {
                    resources.getString(R.string.preset) -> {
                        SpannableStringBuilder(it).apply {
//                        setSpan(UnderlineSpan(), 0,it.length,SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                            setSpan(object : ClickableSpan() {
                                override fun onClick(p0: View) {
                                    PresetBottomSheetDialogFragment().apply {
                                        t.screen(this.javaClass)
                                    }.show(
                                        childFragmentManager,
                                        "preset"
                                    )
                                }
                            }, 0, it.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                    resources.getString(R.string.previous_configuration)-> {
                        SpannableStringBuilder(it).apply {
//                        setSpan(UnderlineSpan(), 0,it.length,SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                            setSpan(object : ClickableSpan() {
                                override fun onClick(p0: View) {
                                    PresetBottomSheetDialogFragment().apply {
                                        t.screen(this.javaClass)
                                    }.show(
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
        })

        vm.onReview.observe(viewLifecycleOwner, Observer {
            it.consume() ?: return@Observer
            reviewVm.init(it.peek.first, it.peek.second)
            ReviewSettingsBottomSheetDialogFragment().apply {
                t.screen(this.javaClass)
            }.show(childFragmentManager, "rsbs")
        })

        vm.reviewChanges.observe(viewLifecycleOwner, Observer {
            it.consume() ?: return@Observer
            val slide = Slide(Gravity.TOP)
            slide.addTarget(b.review)
            TransitionManager.beginDelayedTransition(b.review.parent as ViewGroup)

            val vis = it.peek
            if (vis) {
                b.review.visible()
            } else {
                b.review.hide()
            }
        })

        vm.showWrittenMessage.observe(viewLifecycleOwner, Observer {
            it.consume()?:return@Observer
            BikeProgrammedBottomSheetDialogFragment().show(childFragmentManager,"bikeProgrammed")
        })

        b.review.setOnClickListener {
            vm.onReviewClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
