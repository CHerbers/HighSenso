package name.herbers.android.highsenso.database

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class PersonalData(
    private val genderList: List<String>,
    private val martialStatusList: List<String>,
    private val educationList: List<String>,
    var gender: Int = 0,
    var age: Int = 99,
    var martialStatus: Int = 0,
    var children: Int = 0,
    var education: Int = 0,
    var profession: String = ""
) {
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
}
