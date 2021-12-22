package uz.techie.uzendictionary.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.techie.uzendictionary.models.Favorite
import uz.techie.uzendictionary.models.User
import uz.techie.uzendictionary.models.Version
import uz.techie.uzendictionary.models.Word

@Dao
interface DictionaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words:List<Word>)

    @Query("delete from words")
    suspend fun deleteWords()


    @Transaction
    suspend fun deleteAndInsertWords(words: List<Word>){
        deleteWords()
        insertWords(words)
    }

    @Query("select * from words where word_uz like :word order by word_uz limit 50")
    fun searchUzWords(word:String):Flow<List<Word>>

    @Query("select * from words where word_en like :word order by word_en limit 50")
    fun searchEnWords(word:String):Flow<List<Word>>

    @Query("select * from words")
    fun getAllWords():LiveData<List<Word>>

    //favorite
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("select * from favorites where id=:id limit 1")
    fun getSingleFavorite(id:Int):Flow<List<Favorite>>

    @Query("select * from favorites order by word_en")
    fun getAllFavoritesEn():Flow<List<Favorite>>

    @Query("select * from favorites order by word_uz")
    fun getAllFavoritesUz():Flow<List<Favorite>>


    //user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user:User)

    @Query("delete from user")
    suspend fun deleteUser()

    @Transaction
    suspend fun deleteInsertUser(user: User){
        deleteUser()
        insertUser(user)
    }

    @Query("select * from user")
    fun getUser():Flow<List<User>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: Version)

    @Query("select * from version order by version desc limit 1")
    fun getVersion():List<Version>


}