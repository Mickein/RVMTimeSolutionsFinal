package za.co.varsitycollege.st10215473.rvmtimesolutions.Data

class Profile (
    val id: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val imageUrl: String? = null,
    val timesheetId: String? = null,
    val profilePic: String? = null,
){
    constructor():this("", "", "", "", "", "", "", "")
}