package com.northstar.app.presentation.search

fun matchesQuery(query: String, fields: List<String>): Boolean {
    val needle = query.trim()
    if (needle.isEmpty()) return true
    val lower = needle.lowercase()
    return fields.any { field -> field.lowercase().contains(lower) }
}
