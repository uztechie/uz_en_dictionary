package uz.techie.uzendictionary.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id:Int,
    var full_name:String? = null,
    var phone:String? = null,
    var email:String? = null
)
