package app.icecreamhot.kaidelivery_employee.model

data class OrderDetail(
    val orderdetail_id: Int,
    val order_id: Int,
    val food_id: Int,
    val orderdetail_total: Int,
    val orderdetail_price: Double
)