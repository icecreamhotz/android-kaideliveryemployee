package app.icecreamhot.kaidelivery_employee.ui.order.Chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.model.Chat.ChatMessage
import app.icecreamhot.kaidelivery_employee.ui.order.Adapter.ChatAdapter
import app.icecreamhot.kaidelivery_employee.utils.MY_PREFS
import com.google.firebase.database.*
import kotlin.collections.HashMap

class
ChatFragment: Fragment() {

    private lateinit var orderName: String
    private lateinit var userImg: String
    private lateinit var empImg: String
    private var empId: Int? = null
    private var userId: Int? = null

    private var arrChat = arrayListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private var lastTimestampChat: Long? = null
    private lateinit var readListener: ValueEventListener

    private lateinit var listChat: RecyclerView
    private lateinit var edtMessage: EditText
    private lateinit var btnSendMessage: Button

    private lateinit var ref: DatabaseReference

    companion object {
        fun newInstance(order_name: String,
                        userImg: String,
                        empImg: String,
                        empId: Int,
                        userId: Int) = ChatFragment().apply {
            arguments = Bundle().apply {
                order_name?.let {
                    putString("order_name", order_name)
                }
                userImg?.let {
                    putString("user_avatar", userImg)
                }
                empImg?.let {
                    putString("emp_avatar", empImg)
                }
                empId?.let {
                    putInt("emp_id", empId)
                }
                userId?.let {
                    putInt("user_id", userId)
                }
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getString("order_name")?.let {
            orderName = it
        }
        arguments?.getString("user_avatar")?.let {
            userImg = it
        }
        arguments?.getString("emp_avatar")?.let {
            empImg = it
        }
        arguments?.getInt("emp_id")?.let {
            empId = it
        }
        arguments?.getInt("user_id")?.let {
            userId = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        btnSendMessage = view.findViewById(R.id.btnSendMessage)
        edtMessage = view.findViewById(R.id.edtMessage)
        listChat = view.findViewById(R.id.listChat)

        btnSendMessage.setOnClickListener(onClickSendMessage)

        loadChatMessage()
        checkReadMessage()
        return view
    }

    private val onClickSendMessage = View.OnClickListener {
        val text = edtMessage.text.toString()
        val message = ChatMessage(empId!!, text, System.currentTimeMillis() / 1000, userId!!, false)
        ref = FirebaseDatabase.getInstance().getReference("/Chats/$orderName").push()

        ref.setValue(message).addOnSuccessListener {
            if(edtMessage.text.isNotEmpty()) {
                Toast.makeText(activity, "sended", Toast.LENGTH_LONG).show()
                edtMessage.setText("")
            }
        }
    }

    private fun loadChatMessage() {
/*        val createChannelChat = FirebaseDatabase.getInstance().getReference("Chats")

        createChannelChat.setValue(orderName)*/

        ref = FirebaseDatabase.getInstance().getReference("Chats")

        ref.child(orderName).addValueEventListener(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                arrChat.clear()
                if(p0.exists()) {
                    for(message in p0.children) {
                        val data = message.getValue(ChatMessage::class.java)
                        arrChat.add(data!!)
                    }
                    chatAdapter = ChatAdapter(arrChat, userImg, empImg, empId!!)
                    listChat.apply {
                        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        adapter = chatAdapter
                        scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }
            }
        })
    }

    private fun checkReadMessage() {
        ref = FirebaseDatabase.getInstance().getReference("Chats")

        readListener = ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for(message in p0.child(orderName).children) {
                    val toId = message.child("toId").getValue(Int::class.java)
                    val fromId = message.child("fromId").getValue(Int::class.java)

                    Log.d("chatreadtoid", toId.toString())
                    Log.d("chatreadfromid", fromId.toString())
                    if(toId == empId && fromId == userId) {
                        val hashMap = HashMap<String, Any>()
                        hashMap.put("read", true)
                        message.ref.updateChildren(hashMap)
                        Log.d("chatread", message.ref.toString())
                    }
                }
            }

        })
    }

    override fun onPause() {
        super.onPause()
        ref.removeEventListener(readListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ref.removeEventListener(readListener)
    }

}