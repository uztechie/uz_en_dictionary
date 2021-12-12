package uz.techie.uzendictionary.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import uz.techie.uzendictionary.R

class CustomProgressbar(context: Context):Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(0))
        setContentView(R.layout.custom_progressbar)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

    }
}