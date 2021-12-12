package uz.techie.uzendictionaryadmin.data

import dagger.hilt.android.AndroidEntryPoint
import uz.techie.uzendictionary.models.Favorite
import uz.techie.uzendictionary.models.User
import uz.techie.uzendictionary.models.Word
import uz.techie.uzendictionaryadmin.db.DictionaryDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryRepository @Inject constructor(private val db:DictionaryDatabase) {

    private val dao = db.dao()

    suspend fun insertAndDeleteWords(words:List<Word>) = dao.deleteAndInsertWords(words)
    fun searchUzWords(word: String) = dao.searchUzWords(word)
    fun searchEnWords(word: String) = dao.searchEnWords(word)

    //favorite

    suspend fun insertFavorite(favorite: Favorite) = dao.insertFavorite(favorite)
    suspend fun deleteFavorite(favorite: Favorite) = dao.deleteFavorite(favorite)
    fun getFavorite(id:Int) = dao.getSingleFavorite(id)

    fun getAllFavoritesEn() = dao.getAllFavoritesEn()
    fun getAllFavoritesUz() = dao.getAllFavoritesUz()

    //user
    suspend fun insertUser(user: User) = dao.deleteInsertUser(user)
    fun getUser() = dao.getUser()

}