package uz.techie.uzendictionary.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.techie.uzendictionary.R
import uz.techie.uzendictionary.dialog.CustomProgressbar
import uz.techie.uzendictionary.models.User
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
        customProgressbar.show()
        initToolbar()

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getUser().collect {
                loadUser()
                if (it.isNotEmpty()) {
                    customProgressbar.dismiss()
                    val user = it[0]
                    author_email.text = user.email
                    author_name.text = user.full_name
                    author_phone.text = user.phone
                }
            }
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

    private fun loadUser() {
        db.collection("users").document("user2").get()
            .addOnFailureListener {
                customProgressbar.dismiss()
                Utils.showMessage(requireContext(), it.toString())
            }.addOnSuccessListener { snapshot ->
                customProgressbar.dismiss()
                val user: User? = snapshot.toObject(User::class.java)
                user?.let {
                    viewModel.insertUser(it)
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

}