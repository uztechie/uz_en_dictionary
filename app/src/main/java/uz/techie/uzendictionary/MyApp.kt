package uz.techie.uzendictionary

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import uz.techie.uzendictionary.utils.SharedData

@HiltAndroidApp
class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()

        changeMode(SharedData(this).isDarkMode())
    }

    fun changeMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}