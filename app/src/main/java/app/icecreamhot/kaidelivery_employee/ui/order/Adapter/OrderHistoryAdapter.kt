package app.icecreamhot.kaidelivery_employee.ui.order.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.OrderHistory
import app.icecreamhot.kaidelivery_employee.utils.FormatDateISO8601
import kotlinx.android.synthetic.main.item_order_history.view.*
import javax.inject.Inject

class OrderHistoryAdapter @Inject constructor(val order: ArrayList<OrderHistory>): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    var onItemClick: ((OrderHistory) -> Unit)? = null

    override fun getItemCount() = order.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false))
    }

    override fun onBindViewHolder(holder: OrderHistoryAdapter.ViewHolder, position: Int) {
        holder.bind(order[position])
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(order[adapterPosition])
            }
        }

        fun bind(order: OrderHistory) {
            val orderStatus = if(order.order_status == "5") "ยกเลิก" else "สำเร็จ"
            val orderDate = FormatDateISO8601().getDateTime(order.created_at)
            val totalPrice = order.order_deliveryprice + order.orderDetailsPrice
            itemView.apply {
                txtOrderStatus.text = "สถานะ : ${orderStatus}"
                txtOrderStatusDetail.text = order.order_statusdetails
                if(order.order_status == "5") {
                    txtOrderStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
                    txtOrderStatusDetail.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
                    txtOrderStatusDetail.visibility = View.VISIBLE
                } else {
                    txtOrderStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorSuccess))
                    txtOrderStatusDetail.visibility = View.GONE
                }
                if(order.user == null) {
                    txtCustomerName.text = "Guest Guest"
                } else {
                    txtCustomerName.text = "${order.user.name} ${order.user.lastname}"
                }
                txtRestaurantName.text = "รับ ${order.restaurant.res_name}"
                txtEndpointName.text = "ถึง ${order.endpoint_name}"
                txtOrderDate.text = orderDate
                txtPriceTotal.text = "${totalPrice} $"
            }
        }
    }
}