package app.icecreamhot.kaidelivery_employee.firebasemodel

data class OrderFB(
    val latitude: Double?,
    val longitude: Double?,
    val emp_id: String,
    val status: Int
)