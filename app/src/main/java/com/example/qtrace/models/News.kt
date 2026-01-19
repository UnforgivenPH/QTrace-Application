package com.example.qtrace.models

import java.io.Serializable
import java.util.Date

data class News(
    var id: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val author: String = "QC Gov",
    val datePosted: Date? = null,

    // ✅ OPTIONAL: Paste a Project ID here (from your projects collection)
    // to creates a link button in the app.
    val projectId: String = ""
) : Serializable