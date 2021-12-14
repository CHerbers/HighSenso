package name.herbers.android.highsenso

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.connection.ServerCommunicationHandler
import name.herbers.android.highsenso.data.LoginRequest
import name.herbers.android.highsenso.data.RegistrationRequest
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.UserProfile
import timber.log.Timber

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
    val communicationHandler: ServerCommunicationHandler
) : ViewModel() {
    var backFromResult: Boolean = false

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

    private val _serverLoginResponse = MutableLiveData(-1)
    val serverLoginResponse: LiveData<Int>
        get() = _serverLoginResponse

    private val _serverRegisterResponse = MutableLiveData(-1)
    val serverRegisterResponse: LiveData<Int>
        get() = _serverRegisterResponse

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

    fun callMailSentDialog(){
        _startSentMailDialog.value = true
        _startSentMailDialog.value = false
    }

    fun sendLogin(loginRequest: LoginRequest) {
        communicationHandler.sendLoginRequest(loginRequest)
    }

    fun sendRegister(registrationRequest: RegistrationRequest) {
        communicationHandler.sendRegistrationRequest(registrationRequest)
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

    fun handleLogoutButtonClick() {

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

    override fun onCleared() {
        super.onCleared()
        Timber.i("SharedViewModel destroyed!")
    }
}