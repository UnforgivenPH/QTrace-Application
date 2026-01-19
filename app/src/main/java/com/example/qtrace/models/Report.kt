package com.example.qtrace.models

import java.io.Serializable
import java.util.Date

data class Report(
    var id: String = "",
    val title: String = "",
    val location: String = "",
    val category: String = "",
    val description: String = "",
    val status: String = "Pending",
    val dateSubmitted: Date? = null,

    // ✅ ADD THESE TWO LINES TO FIX THE MODEL ERRORS
    val projectId: String = "",
    val projectName: String = ""
) : Serializable