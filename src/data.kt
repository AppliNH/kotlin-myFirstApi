package com.ocwebserver

data class Course(val id: Int, var title: String, val level: Int, var isActive: Boolean)

val courses = listOf(
    Course(1, "Premier cours", 2, true),
    Course(2, "Second cours", 5, true),
    Course(3, "Troisième cours", 3, true)
)
