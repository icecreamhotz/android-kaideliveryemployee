package app.icecreamhot.kaidelivery_employee.ui.order.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.model.Chat.ChatMessage
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.chat_to_layout.view.*

class ChatAdapter(val arrChat: ArrayList<ChatMessage>, val userImg: String, val empImg: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mChat: MutableList<ChatMessage> = arrChat

    companion object {
        const val TYPE_CHAT_USER = 0
        const val TYPE_CHAT_EMPLOYEE = 1
    }

    fun addMessage(message: ChatMessage) {
        mChat.add(message)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val type = when(mChat[position].fromId) {
            17 -> TYPE_CHAT_EMPLOYEE
            else -> TYPE_CHAT_USER
        }

        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == TYPE_CHAT_EMPLOYEE) {
            ViewHolderEmployee(LayoutInflater.from(parent.context).inflate(R.layout.chat_to_layout, parent, false))
        } else {
            ViewHolderUser(LayoutInflater.from(parent.context).inflate(R.layout.chat_from_layout, parent, false))
        }
    }

    override fun getItemCount() = mChat.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            TYPE_CHAT_USER -> (holder as ViewHolderUser).bind(mChat[position])
            else -> (holder as ViewHolderEmployee).bind(mChat[position])
        }
    }

    inner class ViewHolderUser(itemsView: View): RecyclerView.ViewHolder(itemsView) {

        fun bind(chat: ChatMessage) {
            itemView.apply {
                messageUser.text = chat.message
                Glide.with(itemView.context).load(userImg).into(imgUser)
            }

        }
    }

    inner class ViewHolderEmployee(itemsView: View): RecyclerView.ViewHolder(itemsView) {

        fun bind(chat: ChatMessage) {
            itemView.apply {
                messageUser.text = chat.message
                Glide.with(itemView.context).load(empImg).into(imgUser)

                if(adapterPosition == mChat.size - 1) {
                    if(chat.read == true) {
                        txtRead.text = "Read"
                    } else {
                        txtRead.text = "Delivered"
                    }
                    txtRead.visibility = View.VISIBLE
                } else {
                    txtRead.visibility = View.GONE
                }
            }
        }

    }
}