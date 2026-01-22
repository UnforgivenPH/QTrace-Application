package com.example.qtrace.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Contractor(
    @DocumentId
    var id: String = "",

    val name: String = "",

    // 🛠️ FIX: Changed 'val' to 'var' so @set: works
    @get:PropertyName("contact_person")
    @set:PropertyName("contact_person")
    var contactPerson: String = "",

    val phone: String = "",
    val address: String = "",
    val email: String = "",
    val experience: String = "",
    val expertise: List<String> = emptyList(),
    val logo: LogoData = LogoData(),
    val documents: List<String> = emptyList(),

    // These are already 'var', so they are fine
    @get:PropertyName("active_projects")
    @set:PropertyName("active_projects")
    var activeProjects: Int = 0,

    @get:PropertyName("completed_projects")
    @set:PropertyName("completed_projects")
    var completedProjects: Int = 0
) : Serializable

data class LogoData(
    val path: String = "",
    val name: String = "",
    val phone: String = ""
) : Serializable