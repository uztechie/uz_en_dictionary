package uz.techie.uzendictionary.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class SharedData(context: Context): AppCompatActivity() {
    private val sharedPref = context.getSharedPreferences("shared_data", Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()


    private val appTheme = "appTheme"

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }


    fun setAppMode(isDark:Boolean){
        editor.putBoolean(appTheme, isDark)
        editor.commit()
    }

    fun isDarkMode():Boolean{
        return sharedPref.getBoolean(appTheme, false)
    }




}