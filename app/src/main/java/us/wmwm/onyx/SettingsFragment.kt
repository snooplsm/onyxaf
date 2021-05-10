package us.wmwm.onyx

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import us.wmwm.onyx.common.ControllerSetting
import us.wmwm.onyx.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    val vm: SettingsViewModule by viewModel()

    val reviewVm: ReviewSettingsBottomSheetViewModel by sharedViewModel()

    var _b: FragmentSettingsBinding? = null
    val b: FragmentSettingsBinding
        get() = _b!!

    val adapter = SettingsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _b = FragmentSettingsBinding.inflate(inflater, container, false)
        return b.root
    }

    var onValueChanged: (ControllerSetting,Int)->Unit = { controllerSetting: ControllerSetting, i: Int ->
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
                lastBottom = twenty*5,
                top = space,
                bottom= space,
                start = twenty,
                end = twenty
            )
        )
        adapter.onValueChanged = onValueChanged
        b.recyclerView.adapter = adapter

        vm.needToReadFlashData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            b.recyclerView.gone()
            b.readData.gone()
        })
        vm.flashData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            b.recyclerView.visible()
            b.readData.gone()
        })
//        b.readFromController.setOnClickListener {
//            b.readFromController.isEnabled = false
//            vm.readFromController()
//        }
        vm.controllerSettings.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.submitList(it)
        })

        vm.onReview.observe(viewLifecycleOwner, Observer {
            it.consume()?:return@Observer
            reviewVm.init(it.peek)
            ReviewSettingsBottomSheetDialogFragment().show(childFragmentManager,"rsbs")
        })

        vm.reviewChanges.observe(viewLifecycleOwner, Observer {
            it.consume()?:return@Observer
            TransitionManager.beginDelayedTransition(b.review.parent as ViewGroup)
            val vis = it.peek
            if(vis) {
                b.review.visible()
            } else {
                b.review.hide()
            }
        })

        b.review.setOnClickListener {
            vm.onReviewClicked()
        }

        b.readFromController.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

data class ControllerSettingChange(
    val from:ControllerSetting,
    val to:ControllerSetting
)