package com.example.qtrace.models

import java.io.Serializable
import java.util.Date

data class Project(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val status: String = "",
    val budget: Double = 0.0,
    val contractorId: String = "",

    // Nested Objects matching your Web Admin
    val location: GeoLocation = GeoLocation(),
    val address: Address = Address(),
    val dates: ProjectDates = ProjectDates(),
    val milestones: List<Milestone> = emptyList()
) : Serializable

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