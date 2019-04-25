package app.icecreamhot.kaidelivery.model.RateAndComment

import com.google.gson.annotations.SerializedName

data class EmployeeList(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<EmployeeScore>
)