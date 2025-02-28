package com.example.worknest.models

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val priority: String,
    val completed: Boolean
)
