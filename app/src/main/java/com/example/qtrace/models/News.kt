package com.example.qtrace.models

import com.google.firebase.firestore.PropertyName
import java.io.Serializable
import java.util.Date

data class News(
    var id: String = "",

    // 1. Map 'article_type' -> title
    @get:PropertyName("article_type") @set:PropertyName("article_type")
    var title: String = "",

    // 2. Map 'article_description' -> content
    @get:PropertyName("article_description") @set:PropertyName("article_description")
    var content: String = "",

    // 3. Map 'article_photo_url' -> imageUrl
    @get:PropertyName("article_photo_url") @set:PropertyName("article_photo_url")
    var imageUrl: String = "",

    // 4. Map 'project_id' -> projectId
    @get:PropertyName("project_id") @set:PropertyName("project_id")
    var projectId: String = "",

    // 5. Map 'article_created_at' -> datePosted
    @get:PropertyName("article_created_at") @set:PropertyName("article_created_at")
    var datePosted: Date? = null,

    // 6. Map 'article_status' -> status
    @get:PropertyName("article_status") @set:PropertyName("article_status")
    var status: String = "Published",

    // 7. Map 'user_id' (Fixes log warning)
    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    // 8. Map 'article_updated_at' (Fixes log warning)
    @get:PropertyName("article_updated_at") @set:PropertyName("article_updated_at")
    var updatedAt: Date? = null,

    // Default field (not in DB, so no annotation needed)
    var author: String = "QC Public Works"

) : Serializable