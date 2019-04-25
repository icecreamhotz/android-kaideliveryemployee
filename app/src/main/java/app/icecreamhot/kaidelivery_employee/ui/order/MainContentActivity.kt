package app.icecreamhot.kaidelivery_employee.ui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.ui.order.HistoryAndComment.MainFragmentHistoryAndComment
import app.icecreamhot.kaidelivery_employee.ui.order.Map.MapsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main_content.*
import java.io.IOException

class MainContentActivity : AppCompatActivity() {

    private val TAG = "MyFirebaseToken"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.restaurant -> {
                replaceFragment(OrderListFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.order -> {
                replaceFragment(MapsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.history -> {
                replaceFragment(MainFragmentHistoryAndComment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_content)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.contentContainer, OrderListFragment())
                .commit()
        }

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initView()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contentContainer, fragment)
        fragmentTransaction.commit()
    }

    private fun initView() {
        Thread(Runnable {
            try {
                Log.i(TAG, FirebaseInstanceId.getInstance().getToken("99509912056", "FCM"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }
}
