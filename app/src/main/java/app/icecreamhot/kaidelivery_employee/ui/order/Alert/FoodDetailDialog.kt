package app.icecreamhot.kaidelivery_employee.ui.order.Alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.Order
import app.icecreamhot.kaidelivery_employee.ui.order.Adapter.FoodDetailAdapter

class FoodDetailDialog: DialogFragment() {

    lateinit var mOrder: ArrayList<Order>

    companion object {
        fun newInstance(order: ArrayList<Order>): FoodDetailDialog {
            val fragment = FoodDetailDialog()
            fragment.mOrder = order
            return fragment
        }
    }

    private lateinit var txtFullname: TextView
    private lateinit var txtOrderDetail: TextView
    private lateinit var txtAddressName: TextView
    private lateinit var txtAddressDetail: TextView
    private lateinit var txtOrderCreate: TextView
    private lateinit var txtOrderStart: TextView
    private lateinit var txtEmployee: TextView
    private lateinit var txtFoodPrice: TextView
    private lateinit var txtDeliveryPrice: TextView
    private lateinit var txtAllPrice: TextView
    private lateinit var foodListRv: RecyclerView
    private lateinit var btnOk: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setTitle(mOrder.get(0).order_name)
        val view = inflater.inflate(R.layout.alert_fragment_detail_food, container, false)
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

        txtFullname = view.findViewById(R.id.txtCusFullname)
        txtOrderDetail = view.findViewById(R.id.txtCusFoodDetails)
        txtAddressName = view.findViewById(R.id.txtCusAddressName)
        txtAddressDetail = view.findViewById(R.id.txtCusAddressDetails)
        txtOrderCreate = view.findViewById(R.id.txtCusOrderSend)
        txtOrderStart = view.findViewById(R.id.txtCusOrderStart)
        txtEmployee = view.findViewById(R.id.txtCusEmployee)
        txtFoodPrice = view.findViewById(R.id.txtFoodPrice)
        txtDeliveryPrice = view.findViewById(R.id.txtDeliveryPrice)
        txtAllPrice = view.findViewById(R.id.txtAllPrice)
        foodListRv = view.findViewById(R.id.foodListDetail)
        btnOk = view.findViewById(R.id.btnOkOrderDetail)

        txtFullname.text = "${mOrder.get(0).user.name} ${mOrder.get(0).user.lastname}"
        txtOrderDetail.text = if (mOrder.get(0).endpoint_details == null) "ไม่มี" else mOrder.get(0).endpoint_details
        txtAddressName.text = mOrder.get(0).endpoint_name
        txtAddressDetail.text = if (mOrder.get(0).order_details == null) "ไม่มี" else mOrder.get(0).order_details
        txtOrderCreate.text = mOrder.get(0).created_at
        txtOrderStart.text = mOrder.get(0).order_start
        txtEmployee.text = "${mOrder.get(0).employee.emp_name} ${mOrder.get(0).employee.emp_lastname}"

        txtFoodPrice.text = String.format("%.2f", foodPrice)
        txtDeliveryPrice.text = String.format("%.2f", deliveryPrice)
        txtAllPrice.text = String.format("%.2f", allPrice)

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        foodListRv.apply {
            layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = foodAdapter
        }
    }

}