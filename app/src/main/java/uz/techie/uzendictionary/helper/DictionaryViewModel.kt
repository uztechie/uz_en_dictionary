package uz.techie.uzendictionaryadmin.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import uz.techie.uzendictionary.models.Favorite
import uz.techie.uzendictionary.models.User
import uz.techie.uzendictionary.models.Word
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel
@Inject constructor(private val repository: DictionaryRepository
) :ViewModel()
{
    private val TAG = "DictionaryViewModel"


    fun insertAndDeleteWords(words:List<Word>) = viewModelScope.launch {
        repository.insertAndDeleteWords(words)
    }

    fun searchWordUz(word:String) = repository.searchUzWords(word)
    fun searchWordEn(word:String) = repository.searchEnWords(word)


    //favorite

    fun insertFavorite(favorite: Favorite) = viewModelScope.launch {
        repository.insertFavorite(favorite)
    }
    fun deleteFavorite(favorite: Favorite) = viewModelScope.launch {
        repository.deleteFavorite(favorite)
    }
    fun getFavorite(id:Int) = repository.getFavorite(id)

    fun getAllFavoritesEn() = repository.getAllFavoritesEn()
    fun getAllFavoritesUz() = repository.getAllFavoritesUz()

    //user
    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }
    fun getUser() = repository.getUser()

}