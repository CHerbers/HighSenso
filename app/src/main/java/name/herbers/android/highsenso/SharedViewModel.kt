package name.herbers.android.highsenso

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.*
import com.google.gson.Gson
import name.herbers.android.highsenso.connection.ServerCommunicationHandler
import name.herbers.android.highsenso.data.*
import name.herbers.android.highsenso.database.*
import name.herbers.android.highsenso.dialogs.ConfirmationMailSentDialog
import name.herbers.android.highsenso.dialogs.LocationDialogFragment
import name.herbers.android.highsenso.dialogs.PrivacyDialogFragment
import name.herbers.android.highsenso.dialogs.ResetPasswordDialogFragment
import name.herbers.android.highsenso.login.LoginDialogFragment
import name.herbers.android.highsenso.login.RegisterDialogFragment
import name.herbers.android.highsenso.questioning.QuestioningFragment
import name.herbers.android.highsenso.start.StartFragment
import timber.log.Timber
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
    private val gson = Gson()
    var backFromResult: Boolean = false

    val currentAnswers: MutableMap<String, MutableMap<String, Answer>> = mutableMapOf()

    val sensorDataHSP: MutableList<SensorData> = mutableListOf()
    val sensorDataDWHS: MutableList<SensorData> = mutableListOf()
    var currentLocation = Constants.QUESTION_NO_ANSWER_DEFAULT
    var currentQuestionnaireName = ""

    val registerDialogBackupMap = initRegisterDialogBackupMap()
    private val appRes = application.resources

    /* Error messages */
    private val badLoginCombinationToast =
        appRes.getString(R.string.login_dialog_bad_combination_toast_message)
    private val networkErrorMessage = appRes.getString(R.string.network_error)
    private val serverErrorMessage = appRes.getString(R.string.server_error)
    private val timeoutErrorMessage = appRes.getString(R.string.timeout_error)
    private val anonymousErrorMessage = appRes.getString(R.string.anonymous_error)

    /* Response messages */
    private val resetPasswordLinkSendMessage = appRes.getString(R.string.reset_dialog_toast_message)

    /* Observed LiveData */
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
//        if (Constants.OFFLINE_MODE && Constants.FIRST_START) setUpQuestionnairesAndAnswerSheetsFromJsonFiles()      //this is to enable offline usage/testing
    }

    /**
     * This function returns a [Questionnaire] for a given questionnaire name if it exists.
     *
     * @param name the [Questionnaire.name] of the searched [Questionnaire]
     *
     * @return a [Questionnaire] with the searched name if it exists, null otherwise
     * */
    fun getQuestionnaireByQuestionnaireName(name: String): Questionnaire? {
        questionnaires?.forEach { questionnaire ->
            if (questionnaire.name == name) return questionnaire
        }
        return null
    }

    /**
     * Fills [currentAnswers] with empty [MutableMap]s for every [Questionnaire] in [questionnaires].
     * */
    private fun initCurrentAnswers() {
        questionnaires?.forEach { questionnaire ->
            if (currentAnswers[questionnaire.name] == null) currentAnswers[questionnaire.name] =
                mutableMapOf()
        }
    }

    /**
     * Sets the observed [locationDialogDismiss] to true to initiate a change from [StartFragment] to
     * [QuestioningFragment]. This is done after the [LocationDialogFragment] is dismissed.
     * */
    fun locationDialogGetsDismissed() {
        _locationDialogDismiss.value = true
        _locationDialogDismiss.value = false
    }

    /**
     * Sets the observed [startSentMailDialog] to true.
     * If true, the [ConfirmationMailSentDialog] is shown.
     * */
    private fun callMailSentDialog() {
        _startSentMailDialog.value = true
        _startSentMailDialog.value = false
    }

    /**
     * This function calculates if the token saved in the [SharedPreferences] is still valid by
     * checking its expiration date.
     *
     * @return true if token is valid, false otherwise
     * */
    fun tokenIsValid(): Boolean {
        val tokenExpirationDate =
            preferences.getLong(appRes.getString(R.string.login_data_token_expiration_date), 0)
        val isValid = tokenExpirationDate != 0L && Date().before(Date(tokenExpirationDate))
        Timber.i("Token is valid: $isValid")
        _isLoggedIn.value = isValid
        return isValid
    }

    /**
     * Trigger the sending of a [LoginRequest] in the [ServerCommunicationHandler].
     *
     * @param username the username used in the [LoginRequest]
     * @param password the password used in the [LoginRequest]
     * */
    fun sendLoginRequest(username: String, password: String) {
        communicationHandler.sendLoginRequest(username, password, this, null)
    }

    /**
     * Trigger the sending of a [RegistrationRequest] in the [ServerCommunicationHandler].
     *
     * @param registrationRequest the [RegistrationRequest] that should be send
     * */
    fun sendRegistrationRequest(registrationRequest: RegistrationRequest) {
        communicationHandler.sendRegistrationRequest(registrationRequest, this)
    }

    /**
     * Handles the click of the register button in the [LoginDialogFragment].
     * Sets observed [startRegisterDialog] to true to show the [RegisterDialogFragment].
     * */
    fun handleRegisterButtonClick() {
        _startRegisterDialog.value = true
        _startRegisterDialog.value = false
    }

    /**
     * Handles the click of the forgot password button in the [LoginDialogFragment].
     * Sets observed [startResetPasswordDialog] to true to show the [ResetPasswordDialogFragment].
     * */
    fun handleForgotPasswordClick() {
        _startResetPasswordDialog.value = true
        _startResetPasswordDialog.value = false
    }

    /**
     * Handles the click of the login button in the [RegisterDialogFragment].
     * Sets observed [startLoginDialog] to true to show the [LoginDialogFragment].
     * */
    fun handleLoginButtonClick() {
        _startLoginDialog.value = true
        _startLoginDialog.value = false
    }

    /**
     * Handles the click of the privacy TextView in the [RegisterDialogFragment].
     * Sets observed [startPrivacyFragment] to true to show the [PrivacyDialogFragment].
     * */
    fun handleRegisterDialogPrivacyClick() {
        _startPrivacyFragment.value = true
        _startPrivacyFragment.value = false
    }

    /**
     * Handles the click of logout button of the [StartFragment].
     * Triggers the sending of a logout request in the [ServerCommunicationHandler] if logged in.
     * Deletes the token from the [SharedPreferences].
     * */
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

    /**
     * Changes the observed [isLoggedIn] to the given value.
     *
     * @param status the value [isLoggedIn] is changed to
     * */
    fun changeLoginStatus(status: Boolean) {
        _isLoggedIn.value = status
    }

    /**
     * Changes the observed [gatherSensorData] to true to initiate gathering of sensor data.
     * */
    fun startGatherSensorData() {
        _gatherSensorData.value = true
    }

    /**
     * Changes the observed [gatherSensorData] to false to stop the gathering of sensor data.
     * */
    fun stopGatherSensorData() {
        _gatherSensorData.value = false
    }

    /**
     * Triggers the sending of a [ResetPasswordRequest] in the [ServerCommunicationHandler].
     *
     * @param email the email the reset link is send to
     * */
    fun handleResetPassword(email: String) {
        communicationHandler.sendResetPasswordRequest(email, this)
    }

    /**
     * This function is called after a response to a [RegistrationRequest] was received.
     * [registerResponse] is set to 1 which means success. This triggers an observer in the
     * [RegisterDialogFragment].
     * A function is called to show the [ConfirmationMailSentDialog].
     * */
    fun registerResponseReceived() {
        _registerResponse.value = 1
        callMailSentDialog()
    }

    /**
     * This function is called after an [VolleyError] to a [RegistrationRequest] was received.
     * The value of the by the [RegisterDialogFragment] observed [errorSendingRegisterData] is
     * changed to a specific error message that will be shown as a [Toast].
     *
     * @param error the received [VolleyError]
     * */
    fun registerErrorReceived(error: VolleyError) {
        when (error) {
            is AuthFailureError -> _errorSendingRegisterData.value = anonymousErrorMessage
            is NetworkError -> _errorSendingRegisterData.value = networkErrorMessage
            is ParseError -> _errorSendingRegisterData.value = anonymousErrorMessage
            is ServerError -> _errorSendingRegisterData.value = serverErrorMessage
            is TimeoutError -> _errorSendingRegisterData.value = timeoutErrorMessage
            else -> {
                _errorSendingRegisterData.value = anonymousErrorMessage
            }
        }
        _errorSendingRegisterData.value = ""
    }

    /**
     * This function handles a received response to a [LoginRequest].
     * The token gets saved in the SharedPreferences, logged in flag is set to true and
     * [ServerCommunicationHandler] functions are called to get all available [Questionnaire]s and
     * [AnswerSheet]s.
     *
     * @param token the received token
     * @param username the username that was part of the [LoginRequest]
     * @param password the password that was part of the [LoginRequest]
     * @param answerSheets the [AnswerSheet]s that need to be sent after the successful login,
     * null if no AnswerSheets needed to be sent before the [LoginRequest]
     * */
    fun loginResponseReceived(
        token: String,
        username: String,
        password: String,
        answerSheets: List<AnswerSheet>?
    ) {
        if (!Constants.OFFLINE_MODE) {  //this is needed to not edit preferences in offline mode
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
        }

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

    /**
     * This function handles a received [VolleyError] to a [LoginRequest].
     * Depending on the given error a specific [LiveData] is changed to trigger a [Toast] message.
     *
     * @param error the received [VolleyError]
     * */
    fun loginErrorReceived(error: VolleyError) {
        when (error) {
            is AuthFailureError -> _errorSendingLoginData.value = badLoginCombinationToast
            is NetworkError -> _errorSendingLoginData.value = networkErrorMessage
            is ParseError -> _errorSendingLoginData.value = anonymousErrorMessage
            is ServerError -> _errorSendingLoginData.value = serverErrorMessage
            is TimeoutError -> _errorSendingLoginData.value = timeoutErrorMessage
        }
        _isLoggedIn.value = false
        _errorSendingLoginData.value = ""
    }

    /**
     * This function handles errors and responses to a [ResetPasswordRequest].
     * If the request was successful a [ConfirmationMailSentDialog] is shown,
     * else a [Toast] with an error message is shown.
     *
     * @param succession tells if the [ResetPasswordRequest] was successful
     * */
    fun resetPasswordResponseReceived(succession: Boolean) {
        when (succession) {
            true -> _showResetPasswordSuccessionToast.value = resetPasswordLinkSendMessage
            false -> _showResetPasswordSuccessionToast.value = anonymousErrorMessage
        }
    }

    /**
     * This function calls a function to create an [AnswerSheet] for every needed AnswerSheet.
     * A function to initiate sending this AnswerSheets is called after.
     * */
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

    /**
     * Creates an [AnswerSheet] from given parameters.
     *
     * @param answersMap a [Map] of [Answer]s and question names
     * @param questionnaireId the id of the [Questionnaire] this are the [Answer]s for
     * @param sensorData the [SensorData] send within [AnswerSheet]
     * */
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

    /**
     * This functions checks if the token is still valid. If not a [LoginRequest] is send and the
     * given [AnswerSheet]s are saved to be send later on.
     * Otherwise the sending of the given [AnswerSheet]s in the [ServerCommunicationHandler] is
     * triggered.
     *
     * @param answerSheets a [List] of the [AnswerSheet]s that shall be send to the webserver
     * */
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
            communicationHandler.sendAnswerSheet(token, answerSheet, this)
        }
    }

    /**
     * This functions returns a [MutableMap] of pairs of defined keys needed in the
     * [RegisterDialogFragment] and value of 'null'.
     *
     * @return a [MutableMap] if pre defined keys and 'null' as value
     * */
    private fun initRegisterDialogBackupMap(): MutableMap<String, String?> {
        val map = mutableMapOf<String, String?>()
        map["username"]
        map["email"]
        map["emailRepeat"]
        map["password"]
        map["passwordRepeat"]
        return map
    }

    /**
     * This function saves given [String]s in the [registerDialogBackupMap].
     *
     * @param username the username that is to save
     * @param email the email that is to save
     * @param emailRepeat the repetition of the email that is to save
     * @param password the password that is to save
     * @param passwordRepeat the repetition og the password that is to save
     * */
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

    /**
     * This function updates the [answerSheets] variable from a given [List] of [AnswerSheet]s that
     * were loaded form the webserver.
     * Every AnswerSheet is saved to the [HighSensoDatabase]
     *
     * @param loadedAnswerSheets the [List] of [AnswerSheet]s loaded from the server that is to save
     * */
    fun updateAnswerSheets(loadedAnswerSheets: List<AnswerSheet>) {
        answerSheets = loadedAnswerSheets
        loadedAnswerSheets.forEach { answerSheet ->
            databaseHandler.insertAnswerSheet(
                DatabaseAnswerSheet(
                    answerSheet.id,
                    answerSheet.collected_at,
                    gson.toJson(answerSheet.answers),
                    gson.toJson(answerSheet.sensor_data),
                    answerSheet.client,
                    answerSheet.locale
                )
            )
        }
    }

    /**
     * This function updates the [questionnaires] variable form a given [List] of [Questionnaire]s
     * that were loaded from the webserver.
     * Every Questionnaire is saved to the [HighSensoDatabase].
     *
     * @param loadedQuestionnaires the [List] of [Questionnaire]s loaded form the server that is to save
     * */
    fun updateQuestionnaires(loadedQuestionnaires: List<Questionnaire>) {
        questionnaires = loadedQuestionnaires
        loadedQuestionnaires.forEach { questionnaire ->
            databaseHandler.insertQuestionnaire(
                DatabaseQuestionnaire(
                    questionnaire.id,
                    questionnaire.name,
                    gson.toJson(questionnaire.questions)
                )
            )
        }
        initCurrentAnswers()
    }

    /**
     * This function tells weather a [Question] after the users location is available in one of the
     * [Questionnaire]s or not.
     *
     * @return true if the [Question] for users location is available, false otherwise
     * */
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

    /**
     * This function loads all [Questionnaire]s from the [HighSensoDatabase] and stores them in
     * [questionnaires].
     * This function is called if a request to get all Questionnaires from the server returns with an
     * error.
     * */
    fun loadQuestionnairesFromDeviceDatabase() {
        if (questionnaires.isNullOrEmpty()) {
            Timber.i("Using Questionnaires from database!")
            questionnaires = databaseHandler.questionnaires
        }
        initCurrentAnswers()
    }

    /**
     * This function loads all [AnswerSheet]s from the [HighSensoDatabase] and stores them in
     * [answerSheets].
     * This function is called if a request to get all AnswerSheets from the server returns with an
     * error.
     * */
    fun loadAnswerSheetsFromDeviceDatabase() {
        if (answerSheets.isNullOrEmpty()) {
            Timber.i("Using AnswerSheets from database!")
            answerSheets = databaseHandler.answerSheets
        }
    }

    /**
     * This function is called after an [AnswerSheet] was successfully sent to the webserver.
     * The value of the location that was part of the [AnswerSheet] is stored in a specific
     * [SharedPreferences] key.
     * This is to show the user on which locations they already answered the questionnaires.
     *
     * */
    fun setLocationPreferences() {
        val locationKey = when (currentLocation) {
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

    /**
     * This function gets [Questionnaire]s and [AnswerSheet]s from the [HighSensoJsonParser].
     * They get loaded from .json files from the assets folder.
     * The only use is to have Questionnaires and AnswerSheets without connecting with the server.
     * This is only needed as long as the server is not online.
     * */
    fun setUpQuestionnairesAndAnswerSheetsFromJsonFiles() {
        Timber.i("Loading Questionnaires ans AnswerSheets from JSON!")
        val jsonParser = HighSensoJsonParser()
        questionnaires = jsonParser.getQuestionnaires(application.applicationContext)
        questionnaires?.forEach { questionnaire ->
            databaseHandler.insertQuestionnaire(
                DatabaseQuestionnaire(
                    questionnaire.id,
                    questionnaire.name,
                    gson.toJson(questionnaire.questions)
                )
            )
        }
        answerSheets = jsonParser.getAnswerSheets(application.applicationContext)
        initCurrentAnswers()
    }

    override fun onCleared() {
        Timber.i("SharedViewModel destroyed!")
        super.onCleared()
    }
}