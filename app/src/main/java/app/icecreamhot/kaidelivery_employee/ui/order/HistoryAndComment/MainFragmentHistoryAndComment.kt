package app.icecreamhot.kaidelivery_employee.ui.order.HistoryAndComment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.ui.order.Adapter.HistoryAndCommentAdapter
import com.google.android.material.tabs.TabLayout

class MainFragmentHistoryAndComment:Fragment() {

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_comment, container, false)
        initView(view)

        return view
    }

    private fun initView(view: View?) {
        tabLayout = view?.findViewById(R.id.tabLayout)
        viewPager = view?.findViewById(R.id.viewPager)

        val fragmentHistoryAndComment = HistoryAndCommentAdapter(childFragmentManager!!)
        viewPager!!.offscreenPageLimit = 2
        viewPager!!.adapter = fragmentHistoryAndComment

        tabLayout!!.setupWithViewPager(viewPager)
    }

}