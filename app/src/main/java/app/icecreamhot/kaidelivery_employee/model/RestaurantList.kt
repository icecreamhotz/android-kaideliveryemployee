package app.icecreamhot.kaidelivery_employee.model

import com.google.gson.annotations.SerializedName

data class RestaurantList(
    @SerializedName("data")
    var employeeArrayList: List<Restaurant>? = null
)