package uz.techie.uzendictionary.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.techie.uzendictionary.MainActivity
import uz.techie.uzendictionary.R
import uz.techie.uzendictionary.adapter.WordAdapter
import uz.techie.uzendictionary.databinding.FragmentSearchBinding
import uz.techie.uzendictionary.dialog.CustomProgressbar
import uz.techie.uzendictionary.models.Word
import uz.techie.uzendictionary.utils.Constants
import uz.techie.uzendictionary.utils.Utils
import uz.techie.uzendictionaryadmin.data.DictionaryViewModel

@AndroidEntryPoint
class SearchFragment:Fragment() {
    private lateinit var binding:FragmentSearchBinding
    private val viewModel:DictionaryViewModel by viewModels()
    private val db = Firebase.firestore
    private lateinit var customProgressbar: CustomProgressbar
    private lateinit var wordAdapter: WordAdapter

    private var primaryLang = Constants.LANG_UZBEK

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

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

//        initBannerAd()

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.searchWordUz("%%").collect {
                if (it.isNotEmpty()){
                    customProgressbar.dismiss()
                }
            }
        }

        binding.searchRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = wordAdapter
        }



        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()){
                    binding.searchClearIv.visibility = View.VISIBLE
                    searchWords(p0.toString())
                }
                else{
                    wordAdapter.differ.submitList(emptyList())
                    binding.searchClearIv.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

                if (p0.toString().isEmpty()){
                    wordAdapter.differ.submitList(emptyList())
                }
            }

        })

        binding.searchClearIv.setOnClickListener {
            binding.searchEt.text = null
        }

        binding.searchChangerIv.setOnClickListener {
            FavoriteFragment().animateView(it)

            if (primaryLang == Constants.LANG_ENGLISH){
                binding.searchLang1.text = getString(R.string.uzbek)
                binding.searchLang2.text = getString(R.string.english)
                primaryLang = Constants.LANG_UZBEK
                wordAdapter.changeLang(primaryLang)
            }
            else{
                binding.searchLang1.text = getString(R.string.english)
                binding.searchLang2.text = getString(R.string.uzbek)
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
        binding.searchInfo.text = getString(R.string.not_found)
        binding.searchInfo.visibility = View.VISIBLE
    }

    private fun hideInfoText() {
        binding.searchInfo.visibility = View.GONE
    }


    override fun onStart() {
        super.onStart()
//        (activity as MainActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
//        (activity as MainActivity).supportActionBar?.show()

    }

    private fun initBannerAd(){
        MobileAds.initialize(requireContext())
        val adRequest = AdRequest.Builder().build()
        binding.searchAdView.loadAd(adRequest)
    }

}