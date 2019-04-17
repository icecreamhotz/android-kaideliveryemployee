package app.icecreamhot.kaidelivery_employee.ui.order

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.ui.order.Adapter.CancelOrderAdapter
import kotlinx.android.synthetic.main.fragment_cancel_choice.view.*

class CancelOrderFragment: Fragment() {

    private var res_id: Int? = null

    var cancelOrderList = arrayOf(
        "1. ร้านปิด",
        "2. คนขับหาร้านไม่เจอ",
        "3. ลูกค้าโทรไปไม่รับ",
        "4. เกินเวลาที่ตั้งไว้",
        "5. ข้อผิดพลาดทางระบบ",
        "6. คนขับประสบอุบัติเหตุ"
    )

    companion object {
        fun newInstance(resId: Int) = CancelOrderFragment().apply {
            arguments = Bundle().apply {
                putInt("res_id", resId)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getInt("order_id")?.let {
            res_id = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_cancel_choice, container, false)

        val cancelAdapter = CancelOrderAdapter(cancelOrderList)

        view.cancelOrderList.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = cancelAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        cancelAdapter.onRowClick = { cancelOrder ->
            Log.d("dataja", cancelOrder)
        }

        return view
    }
}