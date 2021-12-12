package uz.techie.uzendictionary.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null,
    var docId:String? = null,
    var word_uz: String? = null,
    var word_en: String? = null,
    var example: String? = null,
    var word_uz_lowercase: String? = null,
    var word_en_lowercase: String? = null,
    var example_lowercase: String? = null,

    var date:Long? = null
):Serializable
