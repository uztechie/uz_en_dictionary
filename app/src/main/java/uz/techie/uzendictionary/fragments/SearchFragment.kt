package uz.techie.uzendictionary.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.techie.uzendictionary.MainActivity
import uz.techie.uzendictionary.R
import uz.techie.uzendictionary.adapter.WordAdapter
import uz.techie.uzendictionary.dialog.CustomProgressbar
import uz.techie.uzendictionary.models.Word
import uz.techie.uzendictionary.utils.Constants
import uz.techie.uzendictionary.utils.Utils
import uz.techie.uzendictionaryadmin.data.DictionaryViewModel

@AndroidEntryPoint
class SearchFragment:Fragment(R.layout.fragment_search) {
    private val viewModel:DictionaryViewModel by viewModels()
    private val db = Firebase.firestore
    private lateinit var customProgressbar: CustomProgressbar
    private lateinit var wordAdapter: WordAdapter

    private var primaryLang = Constants.LANG_UZBEK

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customProgressbar = CustomProgressbar(requireContext())
        wordAdapter = WordAdapter(object : WordAdapter.WordListener {
            override fun onItemClick(word: Word) {
                if (findNavController().currentDestination?.id == R.id.searchFragment){
                    findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToWordDetailsFragment(word))
                }
            }

        })

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.searchWordUz("%%").collect {
                if (it.isNotEmpty()){
                    customProgressbar.dismiss()
                }
            }
        }

        search_recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = wordAdapter
        }



        search_et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()){
                    search_clear_iv.visibility = View.VISIBLE
                    searchWords(p0.toString())
                }
                else{
                    wordAdapter.differ.submitList(emptyList())
                    search_clear_iv.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

                if (p0.toString().isEmpty()){
                    wordAdapter.differ.submitList(emptyList())
                }
            }

        })

        search_clear_iv.setOnClickListener {
            search_et.text = null
        }

        search_changer_iv.setOnClickListener {
            FavoriteFragment().animateView(it)

            if (primaryLang == Constants.LANG_ENGLISH){
                search_lang1.text = getString(R.string.uzbek)
                search_lang2.text = getString(R.string.english)
                primaryLang = Constants.LANG_UZBEK
                wordAdapter.changeLang(primaryLang)
            }
            else{
                search_lang1.text = getString(R.string.english)
                search_lang2.text = getString(R.string.uzbek)
                primaryLang = Constants.LANG_ENGLISH
                wordAdapter.changeLang(primaryLang)
            }
        }

    }

    private fun searchWords(query: String) {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            val word = "%$query%"
            if (primaryLang == Constants.LANG_UZBEK){
                viewModel.searchWordUz(word).collect {
                    wordAdapter.differ.submitList(it)
                    if (it.isEmpty()){
                        showInfoText()
                    }
                    else{
                        hideInfoText()
                    }
                }
            }
            else{
                viewModel.searchWordEn(word).collect {
                    wordAdapter.differ.submitList(it)
                    if (it.isEmpty()){
                        showInfoText()
                    }
                    else{
                        hideInfoText()
                    }
                }
            }




        }
    }

    private fun showInfoText() {
        search_info.text = getString(R.string.not_found)
        search_info.visibility = View.VISIBLE
    }

    private fun hideInfoText() {
        search_info.visibility = View.GONE
    }


    override fun onStart() {
        super.onStart()
//        (activity as MainActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
//        (activity as MainActivity).supportActionBar?.show()

    }


}