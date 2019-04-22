package app.icecreamhot.kaidelivery_employee.ui.order

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.model.Order
import app.icecreamhot.kaidelivery_employee.network.OrderAPI
import app.icecreamhot.kaidelivery_employee.ui.order.Adapter.CancelOrderAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CancelOrderFragment: Fragment() {

    private val orderAPI by lazy {
        OrderAPI.create()
    }

    private var order_id: Int? = null
    private var order_name: String? = null
    private lateinit var disposable: Disposable
    private var orderStatusDetails: String? = null

    private lateinit var ref: DatabaseReference

    private lateinit var btnAccept: Button
    private lateinit var btnDecline: Button
    private lateinit var listCancelOrder: RecyclerView
    private lateinit var loadingOrder: ContentLoadingProgressBar

    var cancelOrderList = arrayOf(
        "1. ร้านปิด",
        "2. คนขับหาร้านไม่เจอ",
        "3. ลูกค้าโทรไปไม่รับ",
        "4. เกินเวลาที่ตั้งไว้",
        "5. ข้อผิดพลาดทางระบบ",
        "6. คนขับประสบอุบัติเหตุ"
    )

    companion object {
        fun newInstance(orderId: Int, orderName: String) = CancelOrderFragment().apply {
            arguments = Bundle().apply {
                putInt("order_id", orderId)
                putString("order_name", orderName)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getInt("order_id")?.let {
            order_id = it
        }
        arguments?.getString("order_name")?.let {
            order_name = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_cancel_choice, container, false)
        btnAccept = view.findViewById(R.id.btnOk)
        btnDecline = view.findViewById(R.id.btnCancel)
        listCancelOrder = view.findViewById(R.id.cancelOrderList)
        loadingOrder = view.findViewById(R.id.loading)

        setEventToView()

        return view
    }

    private fun setEventToView() {
        val cancelAdapter = CancelOrderAdapter(cancelOrderList)

        listCancelOrder.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = cancelAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        cancelAdapter.onRowClick = { cancelOrder ->
            orderStatusDetails = cancelOrder
        }

        btnAccept.setOnClickListener(setOnClickOk)
        btnDecline.setOnClickListener(setOnClickCancel)
    }

    private fun deleteOrderFromFirebase(order_name: String) {
        ref = FirebaseDatabase.getInstance().getReference("Orders").child(order_name)
        ref.removeValue().addOnSuccessListener {
            Toast.makeText(activity!!.applicationContext, "Cancel Success", Toast.LENGTH_LONG).show()
            popStacktoOrderListFragment()
        }
    }

    private val setOnClickOk = View.OnClickListener {
        orderStatusDetails?.let {
            disposable = orderAPI.updateStatusOrder(order_id!!,
                5,
                orderStatusDetails,
                null,
                null)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadingOrder.show() }
                .doOnTerminate { loadingOrder.hide() }
                .subscribe(
                    {
                        deleteOrderFromFirebase(order_name!!)
                    },
                    {
                            err -> Log.d("err", err.message)
                    }
                )
        }
    }

    private val setOnClickCancel = View.OnClickListener {
        popStacktoOrderListFragment()
    }

    fun popStacktoOrderListFragment() {
        val transaction = fragmentManager
        transaction?.popBackStack()
    }
}