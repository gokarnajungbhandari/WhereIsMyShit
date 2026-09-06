package com.example.whereismyshit.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shit")
data class Shit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val isContainer: Boolean,
    val parentId: Int? = null //? makes the value nullable
)