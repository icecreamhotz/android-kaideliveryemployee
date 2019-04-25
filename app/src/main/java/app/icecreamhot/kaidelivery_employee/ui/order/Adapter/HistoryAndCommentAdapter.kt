package app.icecreamhot.kaidelivery_employee.ui.order.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import app.icecreamhot.kaidelivery_employee.ui.order.HistoryAndComment.CommentFragment
import app.icecreamhot.kaidelivery_employee.ui.order.HistoryAndComment.HistoryOrderFragment

class HistoryAndCommentAdapter(fm: FragmentManager):
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return when(position) {
            0 -> CommentFragment()
            else -> return HistoryOrderFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "คำติชม"
            else -> return "ประวัติ"
        }
    }

}