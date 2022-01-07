package name.herbers.android.highsenso

import android.app.Application
import android.content.SharedPreferences
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.*
import com.google.gson.Gson
import name.herbers.android.highsenso.connection.ServerCommunicationHandler
import name.herbers.android.highsenso.data.*
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.DatabaseHelper
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

/**
 * This [AndroidViewModel] belongs to the [MainActivity] and can therefore be accessed by
 * every Fragment of this App under this activity.
 * It is used to provide a [DatabaseHandler] to the Fragments and their
 * corresponding ViewModels.
 * The [databaseHandler] is used to handle every database access/manipulation.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class SharedViewModel(
    val databaseHandler: DatabaseHandler,
    var questionnaires: List<Questionnaire>?,
    var answerSheets: List<AnswerSheet>?,
    private val preferences: SharedPreferences,
    private val application: Application,
    private val communicationHandler: ServerCommunicationHandler
) : ViewModel() {
    var backFromResult: Boolean = false

    val currentAnswers: MutableMap<String, MutableMap<String, Answer>> = mutableMapOf()

    val sensorDataHSP: MutableList<SensorData> = mutableListOf()
    val sensorDataDWHS: MutableList<SensorData> = mutableListOf()
    var currentLocation = 9
    var currentQuestionnaireName = ""

    val registerDialogBackupMap = initRegisterDialogBackupMap()
    private val appRes = application.resources

    /* Error messages */
    private val badLoginCombinationToast =
        appRes.getString(R.string.login_dialog_bad_combination_toast_message)
    private val authFailureErrorMessage = appRes.getString(R.string.auth_failure_error_token)
    private val networkErrorMessage = appRes.getString(R.string.network_error)
    private val parseErrorMessage = appRes.getString(R.string.parse_error)
    private val serverErrorMessage = appRes.getString(R.string.server_error)
    private val timeoutErrorMessage = appRes.getString(R.string.timeout_error)
    private val anonymousErrorMessage = appRes.getString(R.string.anonymous_error)

    /* Response messages */
    private val resetPasswordLinkSendMessage = appRes.getString(R.string.reset_dialog_toast_message)

    private val _gatherSensorData = MutableLiveData(false)
    val gatherSensorData: LiveData<Boolean>
        get() = _gatherSensorData

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

    private val _loginResponse = MutableLiveData(false)
    val loginResponse: LiveData<Boolean>
        get() = _loginResponse

    private val _registerResponse = MutableLiveData(-1)
    val registerResponse: LiveData<Int>
        get() = _registerResponse

    private val _showResetPasswordSuccessionToast = MutableLiveData("")
    val showResetPasswordSuccessionToast: LiveData<String>
        get() = _showResetPasswordSuccessionToast

    init {
        Timber.i("SharedViewModel created!")
        if (Constants.OFFLINE_MODE) setUpQuestionnairesAndAnswerSheets()
    }

    private fun setUpQuestionnairesAndAnswerSheets() {
        questionnaires = DatabaseHelper().getQuestionnaires(application.applicationContext)
        questionnaires?.forEach { questionnaire ->
            databaseHandler.updateDatabaseQuestionnaire(questionnaire)
        }
        answerSheets = DatabaseHelper().getAnswerSheets()
        initCurrentAnswers()
    }

    fun getQuestionnaireByQuestionnaireName(name: String): Questionnaire? {
        questionnaires?.forEach { questionnaire ->
            if (questionnaire.name == name) return questionnaire
        }
        return null
    }

    private fun initCurrentAnswers() {
        questionnaires?.forEach { questionnaire ->
            if (currentAnswers[questionnaire.name] == null) currentAnswers[questionnaire.name] =
                mutableMapOf()
        }
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

    private fun callMailSentDialog() {
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
        communicationHandler.sendResetPasswordRequest(email, this)
    }

    fun registerResponseReceived() {
        _registerResponse.value = 1
        callMailSentDialog()
    }

    fun registerErrorReceived(error: VolleyError) {
        when (error) {
            is AuthFailureError -> {
                _errorSendingRegisterData.value = anonymousErrorMessage
            }
            is NetworkError -> {
                _errorSendingRegisterData.value = networkErrorMessage
            }
            is ParseError -> {
                _errorSendingRegisterData.value = anonymousErrorMessage
            }
            is ServerError -> {
                _errorSendingRegisterData.value = serverErrorMessage
            }
            is TimeoutError -> {
                _errorSendingRegisterData.value = timeoutErrorMessage
            }
            else -> {
                _errorSendingRegisterData.value = anonymousErrorMessage
            }
        }
        _errorSendingRegisterData.value = ""
    }

    fun loginResponseReceived(
        response: String,
        username: String,
        password: String,
        answerSheets: List<AnswerSheet>?
    ) {
        val token: String = "" //TODO extract token String

        /* Adjust preferences for token, token_expiration, username and password */
        preferences.edit().putString(
            appRes.getString(R.string.login_data_token),
            token
        ).apply()
        preferences.edit().putLong(
            appRes.getString(R.string.login_data_token_expiration_date),
            Date().time + Constants.TOKEN_DURABILITY_TIME
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

        //trigger LoginDialog dismiss
        _loginResponse.value = true
        _loginResponse.value = false

        if (answerSheets == null) {
            //get all questionnaires and past answerSheets
            communicationHandler.getAllQuestionnaires(token, this)
            communicationHandler.getAllAnswerSheets(token, this)
        } else {
            //send current answerSheets
            sendAnswerSheets(answerSheets)
        }

    }

    fun loginErrorReceived(
        error: VolleyError
    ) {
        when (error) {
            is AuthFailureError -> {
                _errorSendingLoginData.value = badLoginCombinationToast
            }
            is NetworkError -> {
                _errorSendingLoginData.value = networkErrorMessage
            }
            is ParseError -> {
                _errorSendingLoginData.value = anonymousErrorMessage
            }
            is ServerError -> {
                _errorSendingLoginData.value = serverErrorMessage
            }
            is TimeoutError -> {
                _errorSendingLoginData.value = timeoutErrorMessage
            }
        }
        _isLoggedIn.value = false
        _errorSendingLoginData.value = ""
    }

    fun resetPasswordResponseReceived(succession: Boolean) {
        when (succession) {
            true -> _showResetPasswordSuccessionToast.value = resetPasswordLinkSendMessage
            false -> _showResetPasswordSuccessionToast.value = anonymousErrorMessage
        }
    }

    fun createAndSendAnswerSheets() {
        val answerSheetList = mutableListOf<AnswerSheet>()

        val answerBaselineMap = currentAnswers[Constants.BASELINE_QUESTIONNAIRE]
        if (answerBaselineMap != null) {
            answerSheetList.add(
                createAnswerSheetFromCurrentAnswers(
                    answerBaselineMap,
                    Constants.BASELINE_QUESTIONNAIRE_ID,
                    null
                )
            )
        }

        val answerHSPMap = currentAnswers[Constants.HSP_SCALE_QUESTIONNAIRE]
        if (answerHSPMap != null) {
            answerSheetList.add(
                createAnswerSheetFromCurrentAnswers(
                    answerHSPMap,
                    Constants.HSP_SCALE_QUESTIONNAIRE_ID,
                    sensorDataHSP
                )
            )
        }
        val answerDWHMap = currentAnswers[Constants.DEAL_WITH_HS_QUESTIONNAIRE]
        if (answerDWHMap != null) {
            answerSheetList.add(
                createAnswerSheetFromCurrentAnswers(
                    answerDWHMap,
                    Constants.DEAL_WITH_HS_QUESTIONNAIRE_ID,
                    sensorDataDWHS
                )
            )
        }
        sendAnswerSheets(answerSheetList)
    }

    private fun createAnswerSheetFromCurrentAnswers(
        answersMap: Map<String, Answer>,
        questionnaireId: Int,
        sensorData: List<SensorData>?
    ): AnswerSheet {
        val client = Client(
            android.os.Build.MODEL,
            android.os.Build.DEVICE,
            android.os.Build.VERSION.RELEASE
        )
        val answers = mutableListOf<Answer>()
        answersMap.forEach { (_, answer) ->
            answers.add(answer)
        }
        return AnswerSheet(
            questionnaireId,
            Date().time,
            answers,
            sensorData,
            client
        )
    }

    fun sendAnswerSheets(answerSheets: List<AnswerSheet>) {
        val token = preferences.getString(appRes.getString(R.string.login_data_token), "")
        answerSheetsToJsonFile(answerSheets) //TODO delete
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
            communicationHandler.sendAnswerSheet(token, answerSheet, this)
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

    fun updateBackupMap(
        username: String?,
        email: String?,
        emailRepeat: String?,
        password: String?,
        passwordRepeat: String?
    ) {
        registerDialogBackupMap["username"] = username
        registerDialogBackupMap["email"] = email
        registerDialogBackupMap["emailRepeat"] = emailRepeat
        registerDialogBackupMap["password"] = password
        registerDialogBackupMap["passwordRepeat"] = passwordRepeat
    }

    fun updateAnswerSheets(loadedAnswerSheets: List<AnswerSheet>) {
        answerSheets = loadedAnswerSheets
        loadedAnswerSheets.forEach { answerSheet ->
            databaseHandler.updateDatabaseAnswerSheet(answerSheet)
        }
    }

    fun updateQuestionnaires(loadedQuestionnaires: List<Questionnaire>) {
        questionnaires = loadedQuestionnaires
        loadedQuestionnaires.forEach { questionnaire ->
            databaseHandler.updateDatabaseQuestionnaire(questionnaire)
        }
        initCurrentAnswers()
    }

    private fun answerSheetsToJsonFile(answerSheets: List<AnswerSheet>) {
        val gson = Gson()
        var count = 0
        answerSheets.forEach { answerSheet ->
            count++
            val bodyJSON = gson.toJson(answerSheet)
            Timber.i(bodyJSON)
            val path = Environment.getDataDirectory().path + "/data/name.herbers.android.highsenso/answersheets/anwersheet$count.json"

            try {
                File(path).printWriter().use { out ->
                    out.println(bodyJSON)
                }
            } catch (e: IOException) {
                Timber.e("IOException!" + e.printStackTrace())
            }
        }
    }

    fun locationQuestionAvailable(): Boolean {
        var available = false
        if (questionnaires.isNullOrEmpty()) return available
        questionnaires!!.forEach { questionnaire ->
            questionnaire.questions.forEach { question ->
                if (question is Question && question.label == Constants.LOCATION_QUESTION_LABEL)
                    available = true
            }
        }
        return available
    }

    fun loadQuestionnairesFromDeviceDatabase() {
        if (questionnaires.isNullOrEmpty()) questionnaires = databaseHandler.questionnaires
    }

    fun loadAnswerSheetsFromDeviceDatabase() {
        if (answerSheets.isNullOrEmpty()) answerSheets = databaseHandler.answerSheets
    }

    fun setLocationPreferences() {
        val locationKey = when(currentLocation){
            1 -> R.string.location_option_home_key
            2 -> R.string.location_option_work_key
            3 -> R.string.location_option_outside_key
            else -> return
        }
        preferences.edit().putBoolean(
            appRes.getString(locationKey),
            true
        ).apply()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("SharedViewModel destroyed!")
    }
}