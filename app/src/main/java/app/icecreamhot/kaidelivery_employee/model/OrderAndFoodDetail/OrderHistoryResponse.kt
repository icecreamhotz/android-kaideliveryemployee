package app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail

import com.google.gson.annotations.SerializedName


data class OrderHistoryResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<OrderHistory>
)