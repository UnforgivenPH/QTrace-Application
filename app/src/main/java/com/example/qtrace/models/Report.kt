package com.example.qtrace.models
import java.io.Serializable

data class Report(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val category: String = "",
    val status: String = "Pending",

    // Critical for linking
    val projectId: String = "",
    val projectName: String = ""
) : Serializable