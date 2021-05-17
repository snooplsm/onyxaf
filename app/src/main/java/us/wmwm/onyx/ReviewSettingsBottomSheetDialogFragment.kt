package us.wmwm.onyx

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncorti.slidetoact.SlideToActView
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.databinding.FragmentReviewSettingsBottomSheetBinding
import us.wmwm.onyx.databinding.ReviewSettingBinding
import us.wmwm.onyx.databinding.ReviewSettingHeaderBinding
import java.lang.RuntimeException

class ReviewSettingsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    val vm: ReviewSettingsBottomSheetViewModel by sharedViewModel()

    var _b: FragmentReviewSettingsBottomSheetBinding? = null
    val b: FragmentReviewSettingsBottomSheetBinding
        get() = _b!!

    val adapter = ReviewSettingsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentReviewSettingsBottomSheetBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val behavior = dialog.behavior

        behavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        return dialog

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.recyclerView.adapter = adapter

        b.recyclerView.addItemDecoration(SpaceItemDecoration(
            lastBottom = resources.getDimension(R.dimen.five)*20
        ))

        vm.write.observe(viewLifecycleOwner, Observer {
            it.consume() ?: return@Observer
            TransitionManager.beginDelayedTransition(b.write.parent as ViewGroup)
            b.write.visible()
        })
        vm.data.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        vm.dismiss.observe(viewLifecycleOwner, Observer {
            it.consume()?:return@Observer
            dismiss()
        })

        b.write.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                view.isEnabled = false
                isCancelable = false
                vm.onWriteClicked()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        vm.onViewCreated()
    }
}

class ReviewSettingsAdapter : ListAdapter<ReviewPres, BaseViewHolder>(object :
    ItemCallback<ReviewPres>() {
    override fun areItemsTheSame(
        oldItem: ReviewPres,
        newItem: ReviewPres
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ReviewPres,
        newItem: ReviewPres
    ): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

}) {

    override fun getItemViewType(position: Int): Int {
        return currentList[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val view = when(viewType) {
            ReviewType.REVIEW.ordinal-> ReviewSettingView(parent.context)
            ReviewType.HEADER.ordinal-> ReviewSettingHeaderView(parent.context)
            ReviewType.REVIEW_SPOOF.ordinal-> TextView(parent.context).apply{
                minHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50f, resources.displayMetrics).toInt()

            }
            else-> throw RuntimeException("no")
        }
        return BaseViewHolder(view.apply {
            layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)
        })
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val v = holder.view
        val data = currentList[position].data
        when(v) {
            is ReviewSettingView-> {
                v.bind(data as ControllerSettingChange)
            }
        }

    }

}

data class ReviewPres(
    val type:ReviewType,
    val data:Any?=null
)

enum class ReviewType {
    HEADER, REVIEW, REVIEW_SPOOF
}

class ReviewSettingView(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {
    val b: ReviewSettingBinding =
        ReviewSettingBinding.inflate(LayoutInflater.from(context), this, true)

    fun bind(change: ControllerSettingChange) {
        b.name.setText(resources.getIdentifier("controller_setting_${change.from.setting.code}","string",context.packageName))
        b.old.text = change.from.value.toString()
        b.newV.text = change.to.value.toString()
    }
}

class ReviewSettingHeaderView(context: Context, attrs: AttributeSet?=null) :
    ConstraintLayout(context, attrs) {

        val b:ReviewSettingHeaderBinding = ReviewSettingHeaderBinding.inflate(LayoutInflater.from(context),this,true)

}