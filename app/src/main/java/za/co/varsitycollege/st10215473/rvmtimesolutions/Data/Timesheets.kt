package za.co.varsitycollege.st10215473.rvmtimesolutions.Data

data class Timesheets(
    val id: String? = null,
    val name: String? = null,
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val category: String? = null,
    val description: String? = null,
    val minHourGoal: Int? = null,
    val maxHourGoal: Int? = null,
    val image: String? = null,
    val clientName: String? = null,
    val userId: String? = null
){
    constructor() : this("", "", "", "", "", "", "", 0, 0, "", "", "")
}
