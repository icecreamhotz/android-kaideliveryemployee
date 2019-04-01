package app.icecreamhot.kaidelivery_employee.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Restaurant(
    @SerializedName("res_id")
    @field:PrimaryKey
    val res_id: Int,
    @SerializedName("res_name")
    val res_name: String,
    @SerializedName("res_telephone")
    val res_telephone: String?,
    @SerializedName("res_email")
    val res_email: String?,
    @SerializedName("res_address")
    val res_address: String?,
    @SerializedName("res_details")
    val res_details: String?,
    @SerializedName("res_open")
    val res_open: String?,
    @SerializedName("res_close")
    val res_close: String?,
    @SerializedName("res_holiday")
    val res_holiday: String?,
    @SerializedName("res_status")
    val res_status: String = "0",
    @SerializedName("res_logo")
    val res_logo: String?,
    @SerializedName("open_status")
    val open_status: String?,
    @SerializedName("resscore_id")
    val resscore_id: Int = 0,
    @SerializedName("res_lat")
    val res_lat: Double,
    @SerializedName("res_lng")
    val res_lng: Double,
    @SerializedName("restype_id")
    val restype_id: String?,
    var restype_name: String?
)