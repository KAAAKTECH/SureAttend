package com.kaak.sureattend.dataclass

data class User(
    val name: String = "", // User Name
    val id: String = "", // User Institution ID
    val email: String = "", // User Email
    val hostedCLasses: List<String> = emptyList(),
    val joinedCLasses: List<String> = emptyList()
)
