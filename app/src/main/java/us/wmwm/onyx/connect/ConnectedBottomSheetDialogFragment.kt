package us.wmwm.onyx.connect

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.common.BaseCallback
import us.wmwm.onyx.common.Consumable
import us.wmwm.onyx.R
import us.wmwm.onyx.bluetooth.BluetoothConnection
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.common.SpaceItemDecoration
import us.wmwm.onyx.databinding.DisconnectViewBinding
import us.wmwm.onyx.databinding.FragmentConnectedBinding
import us.wmwm.onyx.db.BluetoothDvc

class ConnectedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    val vm: ConnectedFragmentViewModel by sharedViewModel()

    val adapter = ConnectedAdapter()

    var _b: FragmentConnectedBinding? = null
    val b: FragmentConnectedBinding
        get() {
            return _b!!
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentConnectedBinding.inflate(inflater)
        return b.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.onDisconnectClicked = {
            vm.onDisconnectClicked()
        }



        b.recyclerView.adapter = adapter

        val twenty = resources.getDimension(R.dimen.twenty)

        b.recyclerView.addItemDecoration(
            SpaceItemDecoration(
                firstTop = twenty,
                top = twenty/4,
                bottom = twenty/4,
                lastBottom = twenty
            )
        )

        vm.pres.observe(this, Observer {
            adapter.submitList(it)
        })

        vm.dismiss.observe(this, Observer {
            it.consume()?:return@Observer
            dismiss()
        })
    }

}

class ConnectedFragmentViewModel(
    val bt: BluetoothManager
) : ViewModel() {

    val device = MutableLiveData<BluetoothDvc>()
    val pres = MutableLiveData<List<ConnectedPres>>()

    val dismiss = MutableLiveData<Consumable<Boolean>>()

    init {
        bt.connected
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                if(it.second==BluetoothConnection.DISCONNECTED || it.second==BluetoothConnection.CONNECTION_FAILED) {
                    dismiss.postValue(Consumable(true))
                }
            }
    }

    fun init(dvc: BluetoothDvc) {
        device.postValue(dvc)

        pres.postValue(listOf(
            ConnectedType.DISCONNECT
        ).map {
            ConnectedPres(
                data = dvc,
                type = it
            )
        })
    }

    fun onDisconnectClicked() {
        bt.disconnect()
    }

}

data class ConnectedPres(
    val data: BluetoothDvc,
    val type: ConnectedType
)

enum class ConnectedType {
    DISCONNECT
}

class ConnectedAdapter : ListAdapter<ConnectedPres, BaseViewHolder>(BaseCallback<ConnectedPres>()) {

    var onDisconnectClicked: () -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = when (viewType) {
            ConnectedType.DISCONNECT.ordinal -> DisconnectView(parent.context)
            else -> View(parent.context)
        }
        return BaseViewHolder(view.apply {
            layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)
        })
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].type.ordinal
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.view
        when (view) {
            is DisconnectView -> {
                view.onDisconnect = onDisconnectClicked
            }
        }
    }
}

class DisconnectView(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    var onDisconnect: () -> Unit = {}

    val b = DisconnectViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        b.disconnect.setOnClickListener {
            onDisconnect()
        }
    }

}