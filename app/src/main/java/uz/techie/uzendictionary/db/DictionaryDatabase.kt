package uz.techie.uzendictionaryadmin.db

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.techie.uzendictionary.db.DictionaryDao
import uz.techie.uzendictionary.models.Favorite
import uz.techie.uzendictionary.models.User
import uz.techie.uzendictionary.models.Word

@Database(entities = [Word::class, Favorite::class, User::class], version = 10)
abstract class DictionaryDatabase:RoomDatabase() {
    abstract fun dao(): DictionaryDao
}