package app.icecreamhot.kaidelivery_employee.ui.order.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.Food
import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.OrderDetail
import kotlinx.android.synthetic.main.item_food_detail.view.*

class FoodDetailAdapter(val food: ArrayList<OrderDetail>): RecyclerView.Adapter<FoodDetailAdapter.ViewHolder>() {

    override fun getItemCount() = food.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodDetailAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_food_detail, parent, false))
    }

    override fun onBindViewHolder(holder: FoodDetailAdapter.ViewHolder, position: Int) {
        holder.bind(food[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(food: OrderDetail) {
            itemView.apply {
                foodName.text = food.food.food_name
                foodTotalAndPrice.text = "x${food.orderdetail_total} (${food.orderdetail_price}บาท)"
            }
        }
    }
}