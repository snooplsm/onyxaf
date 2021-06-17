package us.wmwm.onyx.previous

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.R
import us.wmwm.onyx.settings.SettingsViewModule
import us.wmwm.onyx.common.SpaceItemDecoration
import us.wmwm.onyx.databinding.FragmentPresetBottomSheetBinding
import us.wmwm.onyx.db.OnyxDb

class PreviousBottomSheetDialogFragment : BottomSheetDialogFragment() {
    var _b: FragmentPresetBottomSheetBinding? = null
    val b: FragmentPresetBottomSheetBinding
        get() = _b!!

    val adapter = PreviousAdapter()

    val settingVM: SettingsViewModule by sharedViewModel()

    val db:OnyxDb by inject()

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

        db.flashData().listAsLiveData().observe(viewLifecycleOwner, Observer {
//            adapter.submitList(it.map {
//                Previous(
//                    data = it,
//                    presetData = it.data
//                )
//            })
        })


        adapter.onClicked = {

            //settingVM.onPreset(it)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

}