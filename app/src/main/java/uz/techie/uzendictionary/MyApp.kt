package uz.techie.uzendictionary

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import uz.techie.uzendictionary.utils.SharedData

@HiltAndroidApp
class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()







        changeMode(SharedData(this).isDarkMode())
        Log.d("TAG", "onCreate: mode is dark "+SharedData(this).isDarkMode())
    }

    fun changeMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}