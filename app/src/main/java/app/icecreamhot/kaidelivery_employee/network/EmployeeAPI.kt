package app.icecreamhot.kaidelivery_employee.network

import android.content.Context
import android.content.Intent
import android.util.Log
import app.icecreamhot.kaidelivery.model.RateAndComment.EmployeeList
import app.icecreamhot.kaidelivery_employee.MainActivity
import app.icecreamhot.kaidelivery_employee.model.Profile.EmployeeResponse
import app.icecreamhot.kaidelivery_employee.model.Report.ReportResponse
import app.icecreamhot.kaidelivery_employee.model.ResponseMAS
import app.icecreamhot.kaidelivery_employee.utils.BASE_URL
import app.icecreamhot.kaidelivery_employee.utils.MY_PREFS
import io.reactivex.Observable
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface EmployeeAPI {

    @FormUrlEncoded
    @POST("employees/working")
    fun updateEmployeeStatus(@Field("work_status") status: Int, @Header("authorization") token: String): Observable<ResponseMAS>

    @GET("employees/score/comment/{empId}")
    fun getEmployeeScoreAndComment(@Path("empId") empId: Int): Observable<EmployeeList>

    @FormUrlEncoded
    @POST("employees/login")
    fun loginCommon(@Field("emp_username") username: String?,
                    @Field("emp_password") password: String?): Observable<app.icecreamhot.kaidelivery_employee.model.Auth.EmployeeList>

    @Multipart
    @POST("employees/update/info")
    fun updateProfile(@Part("emp_name") username: RequestBody,
                    @Part("emp_lastname") password: RequestBody,
                      @Part("emp_idcard") idcard: RequestBody,
                      @Part("emp_telephone") telephone: RequestBody,
                      @Part("emp_address") address: RequestBody,
                      @Part image: MultipartBody.Part?,
                      @HeaderMap token: MutableMap<String, String>): Observable<ResponseMAS>

    @GET("employees/info")
    fun getEmployeeInfo( @Header("authorization") token: String): Observable<EmployeeResponse>

    @GET("employees/income/month/{date}")
    fun getEmployeeReportMonth(@Path("date") date: String): Observable<ReportResponse>

    @GET("employees/income/day/{date}")
    fun getEmployeeReportDay(@Path("date") date: String): Observable<ReportResponse>

    @GET("employees/income/year/{date}")
    fun getEmployeeReportYear(@Path("date") date: String): Observable<ReportResponse>

    @GET("employees/income/range/{startdate}/{enddate}")
    fun getEmployeeReportRange(@Path("startdate") startDate: String,
                               @Path("enddate") endDate: String): Observable<ReportResponse>

    @FormUrlEncoded
    @POST("employees/update/password")
    fun updatePassword(@Field("emp_password") emp_password: String, @Header("authorization") token: String): Observable<ResponseMAS>

    companion object {
        fun create(context: Context): EmployeeAPI {
            val clientBuilder = OkHttpClient.Builder()
            clientBuilder.addInterceptor(object: Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                    val response = chain.proceed(request)
                    if(response.code() == 401) {
                        val shared = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
                        shared.edit().remove("token").apply()
                        Log.d("logout", shared.getString("token", "not"))
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    return response
                }
            })
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(EmployeeAPI::class.java)
        }
    }
}