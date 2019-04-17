package app.icecreamhot.kaidelivery_employee.ui.order.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import kotlinx.android.synthetic.main.item_cancel_order.view.*
import javax.inject.Inject

class CancelOrderAdapter @Inject constructor(val cancelOrder: Array<String>) : RecyclerView.Adapter<CancelOrderAdapter.ViewHolder>() {

    var onRowClick: ((String) -> Unit)? = null
    lateinit var context: Context
    var viewList: MutableList<View> = ArrayList()

    override fun getItemCount() = cancelOrder.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelOrderAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cancel_order, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cancelOrder[position])
        if(!viewList.contains(holder.itemView)) {
            viewList.add(holder.itemView)
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                for(tempItemView in viewList) {
                    tempItemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGray))
                onRowClick?.invoke(cancelOrder[adapterPosition])
            }
        }

        fun bind(cancelOrder: String) {
            itemView.apply {
                txtTitleCancelOrder.text = cancelOrder
            }
        }
    }

}