package app.icecreamhot.kaidelivery_employee.ui.order

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.data.gAliasDistance
import app.icecreamhot.kaidelivery_employee.data.gDistance
import app.icecreamhot.kaidelivery_employee.data.mLatitude
import app.icecreamhot.kaidelivery_employee.data.mLongitude
import app.icecreamhot.kaidelivery_employee.model.Order
import kotlinx.android.synthetic.main.item_order.view.*
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class OrderListAdapter @Inject constructor(val items: ArrayList<Order>): RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    private lateinit var context:Context
    var onItemClick: ((Order) -> Unit)? = null
    var onDeclineClick: ((Order) -> Unit)? = null

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
                    txtOrderName.text = order.order_name
                    txtTimeStart.text = order.order_start
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
            var distance: Int

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