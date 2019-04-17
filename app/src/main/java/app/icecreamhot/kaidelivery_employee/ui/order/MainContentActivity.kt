package app.icecreamhot.kaidelivery_employee.ui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.ui.order.Map.MapsFragment
import com.google.android.gms.maps.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_content.*

class MainContentActivity : AppCompatActivity() {

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
                replaceFragment(HistoryOrderFragment())
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
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contentContainer, fragment)
        fragmentTransaction.commit()
    }
}
