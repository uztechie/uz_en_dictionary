package uz.techie.uzendictionary.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_word_details.*
import kotlinx.coroutines.flow.collect
import uz.techie.uzendictionary.MainActivity
import uz.techie.uzendictionary.R
import uz.techie.uzendictionary.models.Favorite
import uz.techie.uzendictionary.models.Word
import uz.techie.uzendictionary.utils.Utils
import uz.techie.uzendictionaryadmin.data.DictionaryViewModel
import java.util.*

@AndroidEntryPoint
class WordDetailsFragment : Fragment(R.layout.fragment_word_details) {
    private val viewModel: DictionaryViewModel by viewModels()
    private var word: Word? = null
    private var tts: TextToSpeech? = null
    private var isFavorite = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.let {
            word = WordDetailsFragmentArgs.fromBundle(it).word

            details_word_uz.text = word?.word_uz
            details_word_en.text = word?.word_en
            details_word_example.text = word?.example

        }

        checkFavorite()

        tts = TextToSpeech(requireContext(), object : TextToSpeech.OnInitListener {
            override fun onInit(p0: Int) {
                val result = tts!!.setLanguage(Locale.UK)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Utils.showMessage(requireContext(), "TTS does not support this language")
                }

            }

        })

        details_word_en_tts.setOnClickListener {
            word!!.word_en?.let {
                val text = it.replace("/", ",")
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            }


        }

        details_example_tts.setOnClickListener {
            word!!.example?.let {
                val text = it.replace("/", ",")
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            }


        }

        details_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }

        details_btn_favorite.setOnClickListener {
            word?.let { nonNullWord->

                val favorite = Favorite(
                    id = nonNullWord.id!!,
                    docId = nonNullWord.docId,
                    word_uz = nonNullWord.word_uz,
                    word_en = nonNullWord.word_en,
                    example = nonNullWord.example
                )
                if (isFavorite){
                    viewModel.deleteFavorite(favorite)
                }
                else{
                    viewModel.insertFavorite(favorite)
                }
            }
        }


    }

    private fun checkFavorite() {
        word?.let {
            viewLifecycleOwner.lifecycle.coroutineScope.launchWhenCreated {
                viewModel.getFavorite(it.id!!).collect {
                    if (it.isEmpty()){
                        isFavorite = false
                        details_btn_favorite
                            .setImageDrawable(ContextCompat
                                .getDrawable(requireContext(), R.drawable.ic_baseline_favorite_border_24))
                    }
                    else{
                        isFavorite = true
                        details_btn_favorite
                            .setImageDrawable(ContextCompat
                                .getDrawable(requireContext(), R.drawable.ic_baseline_favorite_24))
                    }
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
//        (activity as MainActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        tts?.stop()
        tts?.shutdown()
//        (activity as MainActivity).supportActionBar?.show()
    }


}