package uz.techie.uzendictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.techie.uzendictionary.adapter.FavoriteAdapter
import uz.techie.uzendictionary.models.Favorite
import uz.techie.uzendictionary.utils.Constants
import uz.techie.uzendictionaryadmin.data.DictionaryViewModel


import android.view.animation.LinearInterpolator

import android.view.animation.Animation

import android.view.animation.RotateAnimation
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import uz.techie.uzendictionary.R
import uz.techie.uzendictionary.databinding.FragmentFavoriteBinding
import uz.techie.uzendictionary.models.Word


@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private lateinit var binding:FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val viewModel: DictionaryViewModel by viewModels()
    private var primaryLang = Constants.LANG_UZBEK

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
//        initBannerAd()

        favoriteAdapter = FavoriteAdapter(object : FavoriteAdapter.WordListener {
            override fun onItemClick(favorite: Favorite) {
                val word = Word()
                word.id = favorite.id
                word.example = favorite.example
                word.word_uz = favorite.word_uz
                word.word_en = favorite.word_en

                findNavController().navigate(FavoriteFragmentDirections.actionFavoriteFragmentToWordDetailsFragment(word))
            }

            override fun onDeleteClick(favorite: Favorite) {
                deleteFavorite(favorite)
            }
        })

        binding.favoriteRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = favoriteAdapter
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getAllFavoritesUz().collect {
                favoriteAdapter.differ.submitList(it)
            }
        }



        binding.favoriteLangChangerBtn.setOnClickListener {
            animateView(it)
            if (primaryLang == Constants.LANG_UZBEK) {
                primaryLang = Constants.LANG_ENGLISH
                favoriteAdapter.changeLang(primaryLang)

                viewLifecycleOwner.lifecycle.coroutineScope.launch {
                    viewModel.getAllFavoritesEn().collect {
                        favoriteAdapter.differ.submitList(it)
                    }
                }

            } else {
                primaryLang = Constants.LANG_UZBEK
                favoriteAdapter.changeLang(primaryLang)
                viewLifecycleOwner.lifecycle.coroutineScope.launch {
                    viewModel.getAllFavoritesUz().collect {
                        favoriteAdapter.differ.submitList(it)
                    }
                }
            }
        }


    }

    private fun deleteFavorite(favorite: Favorite) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Deleting favorite word")
        builder.setMessage(getString(R.string.do_you_want_to_delete_favorite))
        builder.setNegativeButton("No"){dialogInterface, which->
            dialogInterface.dismiss()
        }
        builder.setPositiveButton("Yes"){dialogInterface, which->
            dialogInterface.dismiss()
            viewModel.deleteFavorite(favorite)
        }
        builder.show()
    }


    fun animateView(view: View) {
        val rotate = RotateAnimation(
            0f,
            -180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 200
        rotate.interpolator = LinearInterpolator()
        view.startAnimation(rotate)
    }


    fun initToolbar(){
        binding.header.toolbarTitle.text = getString(R.string.favorite)
        binding.header.toolbarBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun initBannerAd(){
        MobileAds.initialize(requireContext())
        val adRequest = AdRequest.Builder().build()
        binding.searchAdView.loadAd(adRequest)
    }


}