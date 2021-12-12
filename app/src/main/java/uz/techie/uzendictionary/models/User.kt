package uz.techie.uzendictionary.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id:Int? = null,
    val full_name:String? = null,
    val phone:String? = null,
    val email:String? = null
)
