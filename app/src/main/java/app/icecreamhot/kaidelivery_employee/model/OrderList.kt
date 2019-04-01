package app.icecreamhot.kaidelivery_employee.model

import com.google.gson.annotations.SerializedName

data class OrderList(
    @SerializedName("data")
    val orderList: ArrayList<Order>? = null
)