package app.icecreamhot.kaidelivery_employee.ui.order.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.OrderHistory
import kotlinx.android.synthetic.main.item_order_history.view.*

class OrderHistoryAdapter(val order: ArrayList<OrderHistory>): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    override fun getItemCount() = order.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false))
    }

    override fun onBindViewHolder(holder: OrderHistoryAdapter.ViewHolder, position: Int) {
        holder.bind(order[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(order: OrderHistory) {
            itemView.apply {
                txtCustomerName.text = "${order.user.name} ${order.user.lastname}"
                txtRestaurantName.text = "รับ ${order.restaurant.res_name}"
                txtEndpointName.text = "ถึง ${order.endpoint_name}"
            }
        }
    }
}