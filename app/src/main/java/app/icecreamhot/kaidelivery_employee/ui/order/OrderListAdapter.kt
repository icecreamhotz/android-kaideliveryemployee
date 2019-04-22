package app.icecreamhot.kaidelivery_employee.ui.order

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.data.gAliasDistance
import app.icecreamhot.kaidelivery_employee.data.gDistance
import app.icecreamhot.kaidelivery_employee.data.mLatitude
import app.icecreamhot.kaidelivery_employee.data.mLongitude
import app.icecreamhot.kaidelivery_employee.model.Order
import kotlinx.android.synthetic.main.item_order.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class OrderListAdapter @Inject constructor(val items: ArrayList<Order>): RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    private lateinit var context:Context
    var onItemClick: ((Order) -> Unit)? = null
    var onDeclineClick: ((Order) -> Unit)? = null
    var onPreviousQueueClick: ((Int) -> Unit)? = null
    var onNextQueueClick: ((Int, Int?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: OrderListAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], context)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        init {
            view.acceptOrder.setOnClickListener {
                onItemClick?.invoke(items[adapterPosition])
            }
            view.declineOrder.setOnClickListener {
                onDeclineClick?.invoke(items[adapterPosition])
            }
            view.txtOptionQueue.setOnClickListener {
                val orderId = items[adapterPosition].order_id
                val orderQueue = items[adapterPosition].order_queue

                val popupMenu = PopupMenu(context, view.txtOptionQueue)
                if(adapterPosition == 0) {
                    popupMenu.inflate(R.menu.options_calcel_order_menu_noprevious)
                } else {
                    popupMenu.inflate(R.menu.options_cancel_order_menu)
                }
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.menu_previous_queue ->
                        {
                            onPreviousQueueClick?.invoke(orderId)
                        }
                        R.id.menu_next_queue ->
                        {
                            onNextQueueClick?.invoke(orderId, orderQueue)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }

        fun bind(order: Order, context: Context) {
            val customerName = "${order.user?.name} ${order.user?.lastname}"
            val restaurantDistance = getDistance(order.restaurant?.res_lat, order.restaurant?.res_lng)
            val customerDistance = getDistance(order.endpoint_lat, order.endpoint_lng)
            val foodPrice = order.orderDetail[0].totalPrice
            val deliveryPrice = order.order_deliveryprice
            val allPrice = foodPrice + deliveryPrice
            val aliasBath = context.resources.getString(R.string.thbath)
            itemView.apply {
                if(order.min_minute != null) {
                    txtMinMinute.text = "เวลาขั้นต่ำ ${order.min_minute} นาที"
                    containerMinMinute.visibility = View.VISIBLE
                }
                txtOrderName.text = order.order_name
                txtTimeStart.text = order.order_start
                txtOrderQueue.text = "(คิวที่ ${order.order_queue.toString()})"
                txtCustomerName.text = customerName
                txtDistanceCustomer.text = customerDistance
                txtRestaurantName.text = order.restaurant?.res_name
                txtRestaurantAddress.text = order.endpoint_name
                txtDistanceRestaurant.text = restaurantDistance
                txtFoodPrice.text = "${foodPrice} ${aliasBath}"
                txtDeveliryprice.text = "${deliveryPrice} ${aliasBath}"
                txtAllPrice.text = "${allPrice} ${aliasBath}"
            }
        }

        fun getDistance(lat: Double?, lng: Double?): String {
            val distance: Int

            if(lat != null && lng != null) {
                val startPoint = Location("locationOne")
                startPoint.setLatitude(mLatitude!!)
                startPoint.setLongitude(mLongitude!!)

                val endPoint = Location("locationTwo")
                endPoint.setLatitude(lat)
                endPoint.setLongitude(lng)

                distance = startPoint.distanceTo(endPoint).toInt()
            } else {
                distance = 0
            }

            val df = DecimalFormat("#.#")
            df.roundingMode = RoundingMode.FLOOR
            if(distance >= 1000) {
                gDistance = distance / 1000.0
                gAliasDistance = " กม."
            } else {
                gDistance = distance.toDouble()
                gAliasDistance = " ม."
            }

           return if(lat != null && lng != null) df.format(gDistance) + gAliasDistance else "ไม่มีข้อมูล"
        }
    }
}