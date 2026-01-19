package com.example.qtrace.models

import java.io.Serializable

data class Contractor(
    var id: String = "",
    val name: String = "",          // Matches "name" in Firestore
    val address: String = "",       // Matches "address"
    val phone: String = "",         // Matches "phone"
    val contactPerson: String = "", // Matches "contactPerson"
    val expertise: List<String> = emptyList() // Matches the "expertise" array
) : Serializable