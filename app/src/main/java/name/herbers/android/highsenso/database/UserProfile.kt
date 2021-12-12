package name.herbers.android.highsenso.database

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class UserProfile(
    private val locationList: List<String>,
    private val genderList: List<String>,
    private val martialStatusList: List<String>,
    private val educationList: List<String>,
    private val professionTypeList: List<String>,
    val name: String = "",
    val email: String = "",
    var currentLocation: Int = 3,
    var gender: Int = 0,
    var dateOfBirth: Int = 0,
    var martialStatus: Int = 0,
    var children: Int = 0,
    var education: Int = 0,
    var professionType: Int = 0,
    var profession: String = ""
) {
    val currentLocationString: String
        get() {
            return if (currentLocation >= 0 && locationList.size > currentLocation)
                locationList[currentLocation]
            else "missing location"
        }

    val genderString: String
        get() {
            return if (gender >= 0 && genderList.size > gender)
                genderList[gender]
            else ""
        }

    val martialStatusString: String
        get() {
            return if (martialStatus >= 0 && martialStatusList.size > martialStatus)
                martialStatusList[martialStatus]
            else ""
        }

    val educationString: String
        get() {
            return if (education >= 0 && educationList.size > education)
                educationList[education]
            else ""
        }

    val professionTypeString: String
        get() {
            return if(professionType >= 0 && professionTypeList.size > professionType)
                professionTypeList[professionType]
            else ""
        }
}
