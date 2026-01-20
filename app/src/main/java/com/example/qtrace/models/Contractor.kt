package com.example.qtrace.models

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Contractor(
    var id: String = "",
    val name: String = "",
    val contactPerson: String = "",
    val phone: String = "",
    val address: String = "",
    val email: String = "",
    val experience: String = "",
    val expertise: List<String> = emptyList(),
    val logo: LogoData = LogoData(),
    val documents: List<String> = emptyList(),

    // 🛠️ FIX: Map DB 'active_projects' to Variable 'activeProjects'
    @get:PropertyName("active_projects") @set:PropertyName("active_projects")
    var activeProjects: Int = 0,

    // 🛠️ FIX: Map DB 'completed_projects' to Variable 'completedProjects'
    @get:PropertyName("completed_projects") @set:PropertyName("completed_projects")
    var completedProjects: Int = 0
) : Serializable

data class LogoData(
    val path: String = "",
    val name: String = "",
    val phone: String = ""
) : Serializable