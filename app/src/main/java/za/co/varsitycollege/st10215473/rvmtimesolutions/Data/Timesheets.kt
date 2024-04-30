package za.co.varsitycollege.st10215473.rvmtimesolutions.Data

data class Timesheets(
    val category: String? = null,
    val clientName: String? = null,
    val date: String? = null,
    val description: String? = null,
    val endTime: String? = null,
    val id: String? = null,
    val image: String? = null,
    val maxHourGoal: Int? = null,
    val minHourGoal: Int? = null,
    val name: String? = null,
    val startTime: String? = null,
    val userId: String? = null
){
    constructor() : this("", "", "", "", "", "", "", 0, 0, "", "", "")
}
