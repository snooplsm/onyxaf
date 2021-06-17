package us.wmwm.onyx.preset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.R
import us.wmwm.onyx.settings.SettingsViewModule
import us.wmwm.onyx.common.SpaceItemDecoration
import us.wmwm.onyx.databinding.FragmentPresetBottomSheetBinding

class PresetBottomSheetDialogFragment : BottomSheetDialogFragment() {
    var _b: FragmentPresetBottomSheetBinding? = null
    val b: FragmentPresetBottomSheetBinding
        get() = _b!!

    val adapter = PresetAdapter()

    val settingVM: SettingsViewModule by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _b = FragmentPresetBottomSheetBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.recyclerView.adapter = adapter
        val twenty = resources.getDimension(R.dimen.twenty)
        b.recyclerView.addItemDecoration(
            SpaceItemDecoration(
                firstTop = twenty,
                lastBottom = twenty
            )
        )

        adapter.submitList(Preset.values().toList())

        adapter.onClicked = {

            settingVM.onPreset(it)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

}