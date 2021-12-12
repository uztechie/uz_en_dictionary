package uz.techie.uzendictionary.utils

import android.content.Context
import android.widget.Toast

object Utils {

    fun showMessage(context: Context, message:String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}