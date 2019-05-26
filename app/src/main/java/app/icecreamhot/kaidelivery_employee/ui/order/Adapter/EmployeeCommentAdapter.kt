package app.icecreamhot.kaidelivery_employee.ui.order.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.model.RateAndComment.EmployeeScore
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.utils.BASE_URL_USER_IMG
import app.icecreamhot.kaidelivery_employee.utils.FormatDateISO8601
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_employee_comment.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class EmployeeCommentAdapter(val scoreCommentList: ArrayList<EmployeeScore>): RecyclerView.Adapter<EmployeeCommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_employee_comment, parent, false))
    }

    override fun getItemCount() = scoreCommentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scoreCommentList[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(scoreComment: EmployeeScore) {
            val fullName = if(scoreComment.user == null) "Guest Guest" else "${scoreComment.user.name} ${scoreComment.user.lastname}"
            var avatar = if(scoreComment.user == null) "noimg.png" else scoreComment.user.avatar
            val imgUser = BASE_URL_USER_IMG + avatar
            val dateComment = FormatDateISO8601().getDateTime(scoreComment.empscore_date)
            val detailComment = scoreComment.empscore_comment
            val detailRate = scoreComment.empscore_rating.toString()

            itemView.apply {
                txtUserNameComment.text = fullName
                if(detailComment.isNullOrEmpty()) {
                    txtUserDetailComment.visibility = View.GONE
                } else {
                    txtUserDetailComment.visibility = View.VISIBLE
                    txtUserDetailComment.text = detailComment
                }
                txtUserRateComment.text = detailRate
                txtUserDateComment.text = dateComment
                Glide.with(itemView.context).load(imgUser).into(imgUserComment)
            }
        }

    }
}