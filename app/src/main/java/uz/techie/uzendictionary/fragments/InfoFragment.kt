package uz.techie.uzendictionary.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.android.synthetic.main.fragment_info.search_adView
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.techie.uzendictionary.R
import uz.techie.uzendictionary.dialog.CustomProgressbar
import uz.techie.uzendictionary.models.User
import uz.techie.uzendictionary.utils.SharedData
import uz.techie.uzendictionary.utils.Utils
import uz.techie.uzendictionaryadmin.data.DictionaryViewModel

@AndroidEntryPoint
class InfoFragment : Fragment(R.layout.fragment_info) {
    private val db = Firebase.firestore
    private lateinit var customProgressbar: CustomProgressbar
    private val viewModel: DictionaryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customProgressbar = CustomProgressbar(requireContext())

        initToolbar()

        initBannerAd()



        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getUser().collect {
                if (it.isNotEmpty()) {
                    it.forEach { user->
                        if (user.id == 1){
                            author_email.text = user.email
                            author_name.text = user.full_name
                            author_phone.text = user.phone
                        }
                        else if (user.id == 2){
                            developer_email.text = user.email
                            developer_name.text = user.full_name
                            developer_phone.text = user.phone
                        }
                    }


                }
            }
        }

        info_switch.setIsNight(SharedData(requireContext()).isDarkMode())
        changeMode(SharedData(requireContext()).isDarkMode())

        info_switch.setListener {
            if (info_switch.isNight) {
                changeMode(true)
                SharedData(requireContext()).setAppMode(true)
            } else {
                SharedData(requireContext()).setAppMode(false)
                changeMode(false)
            }

            Log.d("d", "onViewCreated: switch " + it)
            Log.d("TAG", "onViewCreated: isNight " + info_switch.isNight)
        }







        author_email.setOnClickListener {
            val email = author_email.text.toString()
            if (email.isNotEmpty()) {
                copyEmail(email)
            }
        }


        developer_email.setOnClickListener {
            val email = developer_email.text.toString()
            if (email.isNotEmpty()) {
                copyEmail(email)
            }
        }

        author_phone.setOnClickListener {
            val phone = author_phone.text.toString()
            if (phone.isNotEmpty()) {
                call(phone)
            }
        }

        developer_phone.setOnClickListener {
            val phone = developer_phone.text.toString()
            if (phone.isNotEmpty()) {
                call(phone)
            }
        }

    }




    fun call(phone: String) {
        val uri = Uri.parse("tel:$phone")
        val intent = Intent(Intent.ACTION_CALL, uri)
        startActivity(intent)
    }


    fun copyEmail(email: String) {
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("email", email)
        clipboardManager.setPrimaryClip(clipData)

        Utils.showMessage(requireContext(), getString(R.string.email_is_copied))
    }


    fun initToolbar() {
        toolbar_title.text = getString(R.string.info)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun changeMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun initBannerAd(){
        MobileAds.initialize(requireContext())
        val adRequest = AdRequest.Builder().build()
        search_adView.loadAd(adRequest)
    }


}