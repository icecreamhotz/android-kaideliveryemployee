package app.icecreamhot.kaidelivery.model.RateAndComment

data class EmployeeScore(
    val empscore_id: Int,
    val empscore_rating: Int,
    val empscore_comment: String?,
    val empscore_date: String,
    val user_id: Int,
    val emp_id: Int,
    val user: User
)