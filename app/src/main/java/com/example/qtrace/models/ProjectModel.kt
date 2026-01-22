package com.example.qtrace.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.io.Serializable
import java.util.Date

data class Project(
    // 1. Ensure ID is captured so DetailActivity can listen to it

    var id: String = "",

    val title: String = "",
    val description: String = "",
    val category: String = "",
    val status: String = "",
    val budget: Double = 0.0,

    // 🛠️ Add these new fields to match your database
    @get:PropertyName("contractor") @set:PropertyName("contractor")
    var contractor: String = "",

    // 2. CRITICAL FIX: Map the DB field "contractor" to this variable
    // This fixes the "No Contractor Assigned" issue
    @get:PropertyName("contractorId") @set:PropertyName("contractorId")
    var contractorId: String = "",

    val location: GeoLocation = GeoLocation(),
    val address: Address = Address(),
    val dates: ProjectDates = ProjectDates(),
    val milestones: List<Milestone> = emptyList()
) : Serializable

// ... (Address, ProjectDates, Milestone, GeoLocation classes remain the same)
data class Address(
    val street: String = "",
    val barangay: String = "",
    val city: String = "",
    val zipCode: String = ""
) : Serializable

data class ProjectDates(
    val started: Date? = null,
    val end: Date? = null
) : Serializable

data class Milestone(
    val type: String = "",
    val imageUrl: String = "",
    val dateUploaded: String = ""
) : Serializable

data class GeoLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0
) : Serializable