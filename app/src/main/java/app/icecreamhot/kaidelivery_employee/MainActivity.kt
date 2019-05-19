
package app.icecreamhot.kaidelivery_employee

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.icecreamhot.kaidelivery_employee.model.Auth.Employee
import app.icecreamhot.kaidelivery_employee.network.EmployeeAPI
import app.icecreamhot.kaidelivery_employee.ui.order.MainContentActivity
import app.icecreamhot.kaidelivery_employee.utils.MY_PREFS
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val employeeAPI by lazy {
        EmployeeAPI.create()
    }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        checkLogin()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onClickLogin = View.OnClickListener {
            commonLogin()
        }

        btn_Login.setOnClickListener(onClickLogin)
    }

    private fun loginSuccess(employee: Employee) {
        val shared = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putInt("emp_id", employee.empId)
        editor.putString("token", employee.token)
        editor.apply()

        Log.d("jwttoken", shared.getString("token", ""))

        val intent = Intent(this, DrawerLayout::class.java)
        startActivity(intent)
    }

    private fun commonLogin() {
        val username = edtUsername.text.toString()
        val password = editPassword.text.toString()

        disposable = employeeAPI.loginCommon(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveStart() }
            .doOnTerminate { onRetrieveFinish() }
            .subscribe(
                {
                        result -> loginSuccess(result.arrUserList!!)
                },
                {
                        e -> Log.d("err", e.message)
                        Toast.makeText(applicationContext, "Something has wrong", Toast.LENGTH_LONG).show()
                }
            )
    }

    private fun onRetrieveStart() {
        loading.visibility = View.VISIBLE
    }

    private fun onRetrieveFinish() {
        loading.visibility  = View.GONE
    }

    private fun checkLogin() {
        val shared = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        val token = shared.getString("token", null)
        if(token != null) {
            val intent = Intent(this, DrawerLayout::class.java)
            startActivity(intent)
        }
    }
}
