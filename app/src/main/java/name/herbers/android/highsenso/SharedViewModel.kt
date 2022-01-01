package name.herbers.android.highsenso

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.connection.ServerCommunicationHandler
import name.herbers.android.highsenso.data.*
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.UserProfile
import timber.log.Timber
import java.util.*

/**
 * This [AndroidViewModel] belongs to the [MainActivity] and can therefore be accessed by
 * every Fragment of this App under this activity.
 * It is used to provide a [DatabaseHandler] and [UserProfile] to the Fragments and their
 * corresponding ViewModels.
 * The [databaseHandler] is used to handle every database access/manipulation.
 * The [userProfile] stores user data that is not permanently stored on the device while the App
 * is alive.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class SharedViewModel(
    val databaseHandler: DatabaseHandler,
    val userProfile: UserProfile,
    var questionnaires: List<Questionnaire>?,
    var answerSheets: List<AnswerSheet>?,
    private val preferences: SharedPreferences,
    application: Application,
    private val communicationHandler: ServerCommunicationHandler
) : ViewModel() {
    var backFromResult: Boolean = false

    val currentAnswersHSP: MutableList<Answer> = mutableListOf()
    val currentAnswersDWHS: MutableList<Answer> = mutableListOf()
    val sensorDataHSP: MutableList<SensorData> = mutableListOf()
    val sensorDataDWHS: MutableList<SensorData> = mutableListOf()

    val registerDialogBackupMap = initRegisterDialogBackupMap()

    private val appRes = application.resources

    private val _gatherSensorData = MutableLiveData(false)
    val gatherSensorData: LiveData<Boolean>
        get() = _gatherSensorData

    private val _questionnaireName = MutableLiveData("") //TODO LiveData needed?
    val questionnaireName: LiveData<String>
        get() = _questionnaireName

    private val _locationDialogDismiss = MutableLiveData(false)
    val locationDialogDismiss: LiveData<Boolean>
        get() = _locationDialogDismiss

    private val _startRegisterDialog = MutableLiveData(false)
    val startRegisterDialog: LiveData<Boolean>
        get() = _startRegisterDialog

    private val _startLoginDialog = MutableLiveData(false)
    val startLoginDialog: LiveData<Boolean>
        get() = _startLoginDialog

    private val _startPrivacyFragment = MutableLiveData(false)
    val startPrivacyFragment: LiveData<Boolean>
        get() = _startPrivacyFragment

    private val _startResetPasswordDialog = MutableLiveData(false)
    val startResetPasswordDialog: LiveData<Boolean>
        get() = _startResetPasswordDialog

    private val _startSentMailDialog = MutableLiveData(false)
    val startSentMailDialog: LiveData<Boolean>
        get() = _startSentMailDialog

    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    private val _resetPasswordSent = MutableLiveData(false)
    val resetPasswordSent: LiveData<Boolean>
        get() = _resetPasswordSent

    private val _errorSendingLoginData = MutableLiveData("")
    val errorSendingData: LiveData<String>
        get() = _errorSendingLoginData

    private val _errorSendingRegisterData = MutableLiveData("")
    val errorSendingRegisterData: LiveData<String>
        get() = _errorSendingRegisterData

    private val _loginResponse = MutableLiveData(-1)
    val loginResponse: LiveData<Int>
        get() = _loginResponse

    private val _registerResponse = MutableLiveData(-1)
    val registerResponse: LiveData<Int>
        get() = _registerResponse

    companion object {
        private const val TOKEN_DURABILITY_TIME =
            10000L //TODO adjust durability to actual token durability
        private const val LOCALE = "DEU"
    }

    init {
        Timber.i("SharedViewModel created!")
    }

    /**
     * Sets the observed [locationDialogDismiss] to true to initiate a change from
     * [name.herbers.android.highsenso.start.StartFragment] to
     * [name.herbers.android.highsenso.questioning.QuestioningFragment].
     * This is done after the [name.herbers.android.highsenso.dialogs.LocationDialogFragment] is
     * dismissed.
     * */
    fun locationDialogGetsDismissed() {
        _locationDialogDismiss.value = true
        _locationDialogDismiss.value = false
    }

    fun callMailSentDialog() {
        _startSentMailDialog.value = true
        _startSentMailDialog.value = false
    }

    fun tokenIsValid(): Boolean {
        val tokenExpirationDate =
            preferences.getLong(appRes.getString(R.string.login_data_token_expiration_date), 0)
        val isValid = tokenExpirationDate != 0L && Date().before(Date(tokenExpirationDate))
        Timber.i("Token is valid: $isValid")
        _isLoggedIn.value = isValid
        return isValid
    }

    fun sendLoginRequest(username: String, password: String) {
        communicationHandler.sendLoginRequest(username, password, this, null)
    }

    fun sendRegistrationRequest(registrationRequest: RegistrationRequest) {
        communicationHandler.sendRegistrationRequest(registrationRequest, this)
    }

    fun handleRegisterButtonClick() {
        _startRegisterDialog.value = true
        _startRegisterDialog.value = false
    }

    fun handleForgotPasswordClick() {
        _startResetPasswordDialog.value = true
        _startResetPasswordDialog.value = false
    }

    fun handleLoginButtonClick() {
        _startLoginDialog.value = true
        _startLoginDialog.value = false
    }

    fun handleRegisterDialogPrivacyClick() {
        _startPrivacyFragment.value = true
        _startPrivacyFragment.value = false
    }

    fun handleLogoutButtonClick() {
        /* If the token is valid, send a LogoutRequest, else there is no need to send it */
        if (tokenIsValid()) {
            val token = preferences.getString(appRes.getString(R.string.login_data_token), "")
            if (token != null && token != "") {
                communicationHandler.sendLogoutRequest(token)
            }
        }

        /* Delete information form preferences */
        preferences.edit().putString(
            appRes.getString(R.string.login_data_token),
            null
        ).apply()
        preferences.edit().putLong(
            appRes.getString(R.string.login_data_token_expiration_date),
            0
        ).apply()
        preferences.edit().putString(
            appRes.getString(R.string.login_data_username_key),
            null
        ).apply()
        preferences.edit().putString(
            appRes.getString(R.string.login_data_pw_key),
            null
        ).apply()

        _isLoggedIn.value = false
    }

    fun changeLoginStatus(status: Boolean) {
        _isLoggedIn.value = status
    }

    fun startGatherSensorData() {
        _gatherSensorData.value = true
    }

    fun stopGatherSensorData() {
        _gatherSensorData.value = false
    }

    fun handleResetPassword(email: String) {
        communicationHandler.sendResetPasswordRequest(email)
    }

    fun registerResponseReceived(response: String) {
//        _registerResponse.value = ... depending on response content
        callMailSentDialog()
        _registerResponse.value = -1
    }

    fun loginResponseReceived(
        response: String,
        username: String,
        password: String,
        answerSheets: List<AnswerSheet>?
    ) {
        /* Check if web server response was positive */
        if (positiveResponse(response)) {
            val token: String = "" //TODO extract token String

            /* Adjust preferences for token, token_expiration, username and password */
            preferences.edit().putString(
                appRes.getString(R.string.login_data_token),
                token
            ).apply()
            preferences.edit().putLong(
                appRes.getString(R.string.login_data_token_expiration_date),
                Date().time + TOKEN_DURABILITY_TIME
            ).apply()
            preferences.edit().putString(
                appRes.getString(R.string.login_data_username_key),
                username
            ).apply()
            preferences.edit().putString(
                appRes.getString(R.string.login_data_pw_key),
                password
            ).apply()

            /* Change app-status to 'logged-in', trigger observer in LoginFragment */
            _isLoggedIn.value = true
            _loginResponse.value = 1

            if (answerSheets == null) {
                //get all questionnaires
                communicationHandler.getAllQuestionnaires(token, this)
            } else {
                //send answerSheets
                sendAnswerSheets(answerSheets)
            }
        } else {
            /* Be sure app-status is not 'logged-in', trigger observer in LoginFragment */
            _isLoggedIn.value = false
            _loginResponse.value = 2
        }
        _loginResponse.value = -1
    }

    private fun positiveResponse(response: String): Boolean {
        return true
    }

    fun createAndSendAnswerSheets() {
        val client = Client(
            android.os.Build.MODEL,
            android.os.Build.DEVICE,
            android.os.Build.VERSION.RELEASE
        )
        val answerSheetHSP = AnswerSheet(
            2,
            Date().time,
            LOCALE,
            currentAnswersHSP,
            sensorDataHSP,
            client
        )
        val answerSheetDWHS = AnswerSheet(
            3,
            Date().time,
            LOCALE,
            currentAnswersDWHS,
            sensorDataDWHS,
            client
        )
        sendAnswerSheets(listOf(answerSheetHSP, answerSheetDWHS))
    }

    fun sendAnswerSheets(answerSheets: List<AnswerSheet>) {
        val token = preferences.getString(appRes.getString(R.string.login_data_token), "")
        if (token == null || token == "") {
            Timber.i("This should never be possible! Mon Dieu!")
            return
        }
        if (!tokenIsValid()) {
            val username =
                preferences.getString(appRes.getString(R.string.login_data_username_key), null)
            val password = preferences.getString(appRes.getString(R.string.login_data_pw_key), null)
            if (username != null && password != null)
                communicationHandler.sendLoginRequest(username, password, this, answerSheets)
        }
        answerSheets.forEach { answerSheet ->
            communicationHandler.sendAnswerSheet(token, answerSheet)
        }
    }

    private fun initRegisterDialogBackupMap(): MutableMap<String, String?> {
        val map = mutableMapOf<String, String?>()
        map["username"]
        map["email"]
        map["emailRepeat"]
        map["password"]
        map["passwordRepeat"]
        return map
    }

    fun updateBackupMap(username: String?, email: String?, emailRepeat: String?, password: String?, passwordRepeat: String?){
        registerDialogBackupMap["username"] = username
        registerDialogBackupMap["email"] = email
        registerDialogBackupMap["emailRepeat"] = emailRepeat
        registerDialogBackupMap["password"] = password
        registerDialogBackupMap["passwordRepeat"] = passwordRepeat
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("SharedViewModel destroyed!")
    }
}