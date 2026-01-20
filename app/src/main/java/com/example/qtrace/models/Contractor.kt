package com.example.qtrace.models

import java.io.Serializable

data class Contractor(
    var id: String = "",
    val name: String = "",
    val contactPerson: String = "",
    val phone: String = "",
    val address: String = "",
    val email: String = "",

    // 1. Matches DB field "experience" (String "5")
    val experience: String = "",

    // 2. Matches DB field "expertise" (List ["Plumbing", "Carpenter"])
    // Removed @PropertyName because the variable name matches the DB field exactly now
    val expertise: List<String> = emptyList(),

    // 3. Nested Logo Object
    val logo: LogoData = LogoData(),

    val documents: List<String> = emptyList()
) : Serializable

data class LogoData(
    val path: String = "",
    val name: String = "",
    val phone: String = ""
) : Serializable