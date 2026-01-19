package com.example.qtrace.models

import java.util.Date

data class Report(
    var id: String = "",
    val title: String = "",
    val location: String = "",
    val category: String = "",
    val description: String = "",
    val status: String = "Pending",
    // Use Date? = null to handle cases where timestamp might be missing
    val dateSubmitted: Date? = null
)