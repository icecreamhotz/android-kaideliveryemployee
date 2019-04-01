package app.icecreamhot.kaidelivery_employee.model

import com.google.gson.annotations.SerializedName

data class OrderTotalPrice(
    @SerializedName("totalPrice")
    val totalPrice: Double
)