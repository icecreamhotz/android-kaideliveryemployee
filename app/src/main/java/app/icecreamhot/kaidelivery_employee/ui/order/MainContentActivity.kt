package app.icecreamhot.kaidelivery_employee.ui.order

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.ui.order.HistoryAndComment.MainFragmentHistoryAndComment
import app.icecreamhot.kaidelivery_employee.ui.order.Map.MapsFragment
import app.icecreamhot.kaidelivery_employee.ui.order.Report.ReportFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main_content.*
import java.io.IOException

class MainContentActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_content)



        initView()
    }



    private fun initView() {

    }

}
