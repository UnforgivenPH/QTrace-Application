package com.example.qtrace.models

import java.io.Serializable
import java.util.Date

// Main Project Data Class matches Firestore structure
data class Project(
    var id: String = "", // Document ID for reference
    val title: String = "",
    val category: String = "",
    val contractorId: String = "",
    val budget: Double = 0.0,
    val status: String = "",
    val description: String = "",
    val location: GeoLocation = GeoLocation(),
    val address: Address = Address(),
    val dates: ProjectDates = ProjectDates(),
    val milestones: List<Milestone> = emptyList()
) : Serializable

data class GeoLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0
) : Serializable

data class Address(
    val street: String = "",
    val barangay: String = "",
    val zipCode: String = "",
    val city: String = "Quezon City"
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