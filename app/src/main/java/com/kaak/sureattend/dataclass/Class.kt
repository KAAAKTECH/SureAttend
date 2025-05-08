package com.kaak.sureattend.dataclass

data class Class(
    val className: String = "",
    val host: String = "HOST123",
    val clients: List<String> = emptyList()
)
