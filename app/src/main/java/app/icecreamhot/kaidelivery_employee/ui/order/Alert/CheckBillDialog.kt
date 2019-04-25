package app.icecreamhot.kaidelivery_employee.ui.order.Alert

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.Order
import app.icecreamhot.kaidelivery_employee.network.OrderAPI
import app.icecreamhot.kaidelivery_employee.ui.order.Adapter.FoodDetailAdapter
import app.icecreamhot.kaidelivery_employee.ui.order.HistoryAndComment.HistoryOrderFragment
import app.icecreamhot.kaidelivery_employee.ui.order.HistoryAndComment.MainFragmentHistoryAndComment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CheckBillDialog: DialogFragment() {

    lateinit var mOrder: ArrayList<Order>

    companion object {
        fun newInstance(order: ArrayList<Order>): CheckBillDialog {
            val fragment = CheckBillDialog()
            fragment.mOrder = order
            return fragment
        }
    }

    private lateinit var txtFoodPrice: TextView
    private lateinit var txtDeliveryPrice: TextView
    private lateinit var txtAllPrice: TextView
    private lateinit var txtCalChange: TextView
    private lateinit var edtCalChange: EditText
    private lateinit var foodListRv: RecyclerView
    private lateinit var btnOk: Button
    private lateinit var btnCancel: Button

    private val orderAPI by lazy {
        OrderAPI.create()
    }
    private var disposable: Disposable? = null
    private lateinit var ref: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setTitle(mOrder.get(0).order_name)
        val view = inflater.inflate(R.layout.alert_fragment_check_bill, container, false)
        initView(view)

        return view
    }

    private fun initView(view: View) {
        var foodPrice = 0.0
        val deliveryPrice = mOrder.get(0).order_deliveryprice

        val foodAdapter = FoodDetailAdapter(mOrder.get(0).order_detail)

        for(calprice in mOrder.get(0).order_detail) {
            foodPrice += calprice.orderdetail_price.toDouble() * calprice.orderdetail_total
        }

        val allPrice = foodPrice + deliveryPrice

        txtFoodPrice = view.findViewById(R.id.txtFoodPrice)
        txtDeliveryPrice = view.findViewById(R.id.txtDeliveryPrice)
        txtAllPrice = view.findViewById(R.id.txtAllPrice)
        txtCalChange = view.findViewById(R.id.txtCalChange)
        edtCalChange = view.findViewById(R.id.edtCalChange)
        foodListRv = view.findViewById(R.id.foodListDetail)
        btnOk = view.findViewById(R.id.btnCheckBillOK)
        btnCancel = view.findViewById(R.id.btnCheckBillCancel)

        txtFoodPrice.text = String.format("%.2f", foodPrice)
        txtDeliveryPrice.text = String.format("%.2f", deliveryPrice)
        txtAllPrice.text = String.format("%.2f", allPrice)

        edtCalChange.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(s.isNotEmpty()) {
                    val calChange = s.toString().toInt() - allPrice
                    txtCalChange.text = "ทอน ${String.format("%.2f", calChange)} บาท"
                } else {
                    txtCalChange.text = "ทอน 0 บาท"
                    return
                }
            }
        })

        btnOk.setOnClickListener {
            disposable = orderAPI.updateStatusOrder(mOrder.get(0).order_id,
                4,
                null,
                "delivered",
                "dT65fviC3JU:APA91bEWwPgBE-prszQZIprelXfPH_fNxP1kkaD7_grVEHVCqtUK8YvjVMuVlT_FInQltk6p_ultdXLUq-AlznODa6mHH7PQ2b7xpc1uILLeLf7qM7Edik1oKkU3elXLkvcHvECe94Jb")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//                .doOnTerminate { loadingOrder.visibility = View.GONE }
                .subscribe(
                    {
                            deleteDelivery()
                    },
                    {
                            err -> Log.d("errcheckbill", err.message)
                    }
                )
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        foodListRv.apply {
            layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = foodAdapter
        }
    }

    private fun deleteDelivery() {
        Log.d("deletedelivery", mOrder.get(0).order_name)
        ref = FirebaseDatabase.getInstance().getReference("Delivery").child(mOrder.get(0).order_name)
        ref.removeValue().addOnSuccessListener {
            val goFragement = MainFragmentHistoryAndComment()
            val fm = fragmentManager
            fm?.beginTransaction()
                ?.replace(R.id.contentContainer, goFragement)
                ?.commitAllowingStateLoss()
            dialog.dismiss()
        }
    }

}