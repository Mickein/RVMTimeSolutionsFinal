package za.co.varsitycollege.st10215473.rvmtimesolutions.Data

data class CalendarEvents(
    val id: String? = null,
    val eventDate: String? = null,
    val eventName: String? = null,
    val eventTime: String? = null,
    val projectName: String? = null
){
    constructor() : this("", "", "", "")
}
