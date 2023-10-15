package uz.techie.uzendictionary.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.techie.uzendictionary.BuildConfig
import uz.techie.uzendictionary.R
import uz.techie.uzendictionary.databinding.FragmentInfoBinding
import uz.techie.uzendictionary.dialog.CustomProgressbar
import uz.techie.uzendictionary.models.User
import uz.techie.uzendictionary.utils.SharedData
import uz.techie.uzendictionary.utils.Utils
import uz.techie.uzendictionaryadmin.data.DictionaryViewModel

@AndroidEntryPoint
class InfoFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var customProgressbar: CustomProgressbar
    private val viewModel: DictionaryViewModel by viewModels()
    private lateinit var binding:FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customProgressbar = CustomProgressbar(requireContext())

        initToolbar()

//        initBannerAd()

        binding.infoVersion.text = "Version: ${BuildConfig.VERSION_NAME}"


        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getUser().collect {
                if (it.isNotEmpty()) {
                    it.forEach { user->
                        if (user.id == 1){
                            binding.authorEmail.text = user.email
                            binding.authorName.text = user.full_name
                            binding.authorPhone.text = user.phone
                        }
                        else if (user.id == 2){
                            binding.developerEmail.text = user.email
                            binding.developerName.text = user.full_name
                            binding.developerPhone.text = user.phone
                        }
                    }


                }
            }
        }

        binding.infoSwitch.setIsNight(SharedData(requireContext()).isDarkMode())
        changeMode(SharedData(requireContext()).isDarkMode())

        binding.infoSwitch.setListener {
            if (binding.infoSwitch.isNight) {
                changeMode(true)
                SharedData(requireContext()).setAppMode(true)
            } else {
                SharedData(requireContext()).setAppMode(false)
                changeMode(false)
            }

            Log.d("d", "onViewCreated: switch " + it)
            Log.d("TAG", "onViewCreated: isNight " + binding.infoSwitch.isNight)
        }







        binding.authorEmail.setOnClickListener {
            val email = binding.authorEmail.text.toString()
            if (email.isNotEmpty()) {
                copyEmail(email)
            }
        }


        binding.developerEmail.setOnClickListener {
            val email = binding.developerEmail.text.toString()
            if (email.isNotEmpty()) {
                copyEmail(email)
            }
        }

        binding.authorPhone.setOnClickListener {
            val phone = binding.authorPhone.text.toString()
            if (phone.isNotEmpty()) {
                call(phone)
            }
        }

        binding.developerPhone.setOnClickListener {
            val phone = binding.developerPhone.text.toString()
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
        binding.header.toolbarTitle.text = getString(R.string.info)
        binding.header.toolbarBtnBack.setOnClickListener {
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
        binding.searchAdView.loadAd(adRequest)
    }


}