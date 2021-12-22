package uz.techie.uzendictionary.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "version")
data class Version(
    @PrimaryKey
    val id:Int,
    val version:Long
)
