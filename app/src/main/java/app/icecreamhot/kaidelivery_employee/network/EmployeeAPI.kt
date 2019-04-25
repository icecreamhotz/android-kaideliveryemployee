package app.icecreamhot.kaidelivery_employee.network

import app.icecreamhot.kaidelivery.model.RateAndComment.EmployeeList
import app.icecreamhot.kaidelivery_employee.model.ResponseMAS
import app.icecreamhot.kaidelivery_employee.utils.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface EmployeeAPI {

    @FormUrlEncoded
    @POST("orders/delivery")
    fun updateEmployeeStatus(@Field("work_status") status: Int): Observable<ResponseMAS>

    @GET("employees/score/comment/{empId}")
    fun getEmployeeScoreAndComment(@Path("empId") empId: Int): Observable<EmployeeList>

    companion object {
        fun create(): EmployeeAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(EmployeeAPI::class.java)
        }
    }
}