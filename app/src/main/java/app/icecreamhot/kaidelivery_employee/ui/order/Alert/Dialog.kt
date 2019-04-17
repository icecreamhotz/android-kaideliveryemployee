package app.icecreamhot.kaidelivery_employee.ui.order.Alert

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.fragment.app.FragmentActivity

class Dialog {
    var ans_true: Runnable? = null
    var ans_false: Runnable? = null

    fun Confirm(ac:FragmentActivity?, title:String, confirmText: String,
                okBtn: String, cancelBtn: String, aProcedure: Runnable?, bProcedure: Runnable?): Boolean {
        aProcedure?.let {
            ans_true = aProcedure
        }
        bProcedure?.let {
            ans_false = bProcedure
        }
        val dialog: AlertDialog = AlertDialog.Builder(ac).create()
        dialog.setTitle(title)
        dialog.setMessage(confirmText)
        dialog.setCancelable(false)
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, okBtn, object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, buttonId: Int) {
                ans_true?.run()
            }
        })
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, cancelBtn, object: DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                ans_false?.run()
            }
        })
        dialog.setIcon(android.R.drawable.ic_dialog_alert)
        dialog.show()
        return true
    }
}