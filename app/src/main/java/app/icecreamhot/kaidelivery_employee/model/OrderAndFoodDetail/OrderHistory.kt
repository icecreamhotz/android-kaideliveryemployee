package app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail

import com.google.gson.annotations.SerializedName

data class OrderHistory (
    val order_id: Int,
    val order_name: String,
    val order_details: String?,
    val user_id: Int,
    val res_id: Int,
    val emp_id: Int?,
    val resscore_id: Int,
    val empscore_id: Int,
    val rate_id: Int,
    val pro_id: Int?,
    val endpoint_name: String,
    val endpoint_details: String?,
    val endpoint_lat: Double,
    val endpoint_lng: Double,
    val order_score: Int?,
    val order_comment: String?,
    val order_status: String,
    val order_statusdetails: String?,
    val order_timeout: String?,
    val order_deliveryprice: Double,
    val order_price: Double,
    val order_discount: Double?,
    val order_date: String,
    val order_start: String,
    val created_at: String,
    val updated_at: String,
    @SerializedName("restaurant")
    val restaurant: Restaurant,
    @SerializedName("user")
    val user: User,
    @SerializedName("totalPrice")
    val orderDetailsPrice: Double,
    @SerializedName("orderdetails")
    val orderDetail: List<OrderDetail>
)