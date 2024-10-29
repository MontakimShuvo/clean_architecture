package com.example.demo_clean_code.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity (
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val name:String,
    val email: String,
    val imageUrl:String?
)