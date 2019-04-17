package app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<Order>
)