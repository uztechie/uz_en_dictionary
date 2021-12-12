package uz.techie.uzendictionary.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.techie.uzendictionaryadmin.db.DictionaryDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app:Application):DictionaryDatabase =
        Room.databaseBuilder(app, DictionaryDatabase::class.java, "dictionary2.db")
            .fallbackToDestructiveMigration()
            .build()
}