package name.herbers.android.highsenso.connection

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.data.*
import name.herbers.android.highsenso.database.HighSensoDatabase
import name.herbers.android.highsenso.database.HighSensoJsonParser
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.UnsupportedEncodingException

/**
 * This class holds the logic for server communication.
 * It creates different requests and sends them.
 * Responses and error following requests are managed in this class.
 *
 *@project HighSenso
 *@author Herbers
 */
class ServerCommunicationHandler(val context: Context) {
    private val gson = Gson()
    private val jsonParser = HighSensoJsonParser()

    companion object {
        /* Request types */
        private const val REQUEST_TYPE_REGISTRATION = 0
        private const val REQUEST_TYPE_LOGIN = 1
        private const val REQUEST_TYPE_LOGOUT = 2
        private const val REQUEST_TYPE_RESET_PASSWORD = 3
        private const val REQUEST_TYPE_SEND_ANSWER_SHEET = 4
        private const val REQUEST_TYPE_GET_QUESTIONNAIRES = 5
        private const val REQUEST_TYPE_GET_ANSWER_SHEETS = 6

        /* Timber logging messages constants */
        private const val RECEIVED_RESPONSE = "Received response for "
        private const val RECEIVED_ERROR = "Received error for "
        private const val JSON_EXCEPTION =
            "An JSONException occurred while trying to parse received response into "
        private const val SUCCESSFULLY_PARSED = "Successfully pared response into data!"
    }

    init {
        Timber.i("ServerCommunicationHandler initialized!")
    }

    /**
     * This function sends a [RegistrationRequest] to the webserver.
     * After receiving a response, a function in [SharedViewModel] is called to perform an action
     * depending on successfulness
     *
     * @param registrationRequest the [RegistrationRequest] that is to send
     * @param sharedViewModel the [SharedViewModel] holding logic
     * */
    fun sendRegistrationRequest(
        registrationRequest: RegistrationRequest,
        sharedViewModel: SharedViewModel
    ) {
        val request = RequestData(Data("users", registrationRequest))
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            getNeededURL(REQUEST_TYPE_REGISTRATION),
            { response ->
                Timber.i(RECEIVED_RESPONSE + "registrationRequest: $response")
                sharedViewModel.registerResponseReceived()
            },
            { error ->
                Timber.i(RECEIVED_ERROR + "registrationRequest: $error")
                sharedViewModel.registerErrorReceived(error)
            }) {
            override fun getBody(): ByteArray {
                val bodyJSON = gson.toJson(request)
                Timber.i("Registration request sent: $bodyJSON")
                return bodyJSON.toString().toByteArray()
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    /**
     * This function sends a [LoginRequest] to the webserver.
     * If successful, a function in [SharedViewModel] is called to save user data and received token
     * and to perform a login.
     * If not successful error handling is called in the SharedViewModel.
     *
     * @param username the login username
     * @param password the login password
     * @param sharedViewModel the [SharedViewModel] holding logic
     * @param answerSheets the [AnswerSheet]s that may be to send after a successful login
     * */
    fun sendLoginRequest(
        username: String,
        password: String,
        sharedViewModel: SharedViewModel,
        answerSheets: List<AnswerSheet>?
    ) {
        val loginRequest = LoginRequest(username, password)
        var token = ""
        val request = RequestData(Data("users", loginRequest))
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            getNeededURL(REQUEST_TYPE_LOGIN),
            { response ->
                Timber.i(RECEIVED_RESPONSE + "loginRequest: $response")
                sharedViewModel.loginResponseReceived(token, username, password, answerSheets)
            },
            { error ->
                sharedViewModel.loginErrorReceived(error)
                Timber.i(RECEIVED_ERROR + "loginRequest: $error")
            }) {
            /* put LoginRequest in request body */
            override fun getBody(): ByteArray {
                val bodyJSON = gson.toJson(request)
                Timber.i("Login request sent: $bodyJSON")
                return bodyJSON.toString().toByteArray()
            }

            /* get token from response headers */
            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                try {
                    token = response?.headers!!["token"] ?: ""
                } catch (e: UnsupportedEncodingException) {
                    Timber.i("Token could not be extracted from response header! $e")
                }
                return super.parseNetworkResponse(response)
            }

            /* set needed headers */
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return addHeader(contentHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    /**
     * This function sends an [AnswerSheet] to the webserver.
     * If successful, all [AnswerSheet]s are loaded from the server and the location preference key
     * is adjusted.
     *
     * @param token the user's token, needed for authentication
     * @param answerSheet the [AnswerSheet] that shall be send
     * @param sharedViewModel the [SharedViewModel] holding logic
     * */
    fun sendAnswerSheet(token: String, answerSheet: AnswerSheet, sharedViewModel: SharedViewModel) {
        val request = RequestData(Data("questionnaires", answerSheet))
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            getNeededURL(REQUEST_TYPE_SEND_ANSWER_SHEET, token, answerSheet.id.toString()),
            { response ->
                Timber.i(RECEIVED_RESPONSE + "sendAnswerSheet (POST): $response")
                getAllAnswerSheets(token, sharedViewModel)
                sharedViewModel.setLocationPreferences()
            },
            { error ->
                Timber.i(RECEIVED_ERROR + "sendAnswerSheet: $error")
            }) {
            override fun getBody(): ByteArray {
                val bodyJSON = gson.toJson(request)
                Timber.i("AnswerSheet sent: $bodyJSON")
                return bodyJSON.toString().toByteArray()
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return addHeader(contentHeader = true, roleHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    /**
     * This function sends a [ResetPasswordRequest] to the webserver.
     * After receiving a response a function on the [SharedViewModel] is called to perform an
     * action depending on the successfulness
     *
     * @param mail the email of the account which password shall be reset
     * @param sharedViewModel the [SharedViewModel] holding logic
     * */
    fun sendResetPasswordRequest(mail: String, sharedViewModel: SharedViewModel) {
        val resetPasswordRequest = ResetPasswordRequest(mail)
        val request = RequestData(Data("users", resetPasswordRequest))
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            getNeededURL(REQUEST_TYPE_RESET_PASSWORD),
            { response ->
                Timber.i(RECEIVED_RESPONSE + "sendResetPasswordRequest (POST): $response")
                sharedViewModel.resetPasswordResponseReceived(true)
            },
            { error ->
                Timber.i(RECEIVED_ERROR + "sendResetPasswordRequest: $error")
                sharedViewModel.resetPasswordResponseReceived(false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return addHeader(contentHeader = true)
            }

            override fun getBody(): ByteArray {
                val bodyJSON = gson.toJson(request)
                Timber.i("Login request sent: $bodyJSON")
                return bodyJSON.toString().toByteArray()
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    /**
     * This function loads every available [Questionnaire] from the webserver.
     * If successful a function on the [SharedViewModel] is called to update the Questionnaires.
     * Else a function is called to load Questionnaires from local [HighSensoDatabase]
     *
     * @param token the user's token, needed for authentication
     * @param sharedViewModel the [SharedViewModel] holding logic
     * */
    fun getAllQuestionnaires(token: String, sharedViewModel: SharedViewModel) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            getNeededURL(REQUEST_TYPE_GET_QUESTIONNAIRES, token),
            null,
            { response ->
                Timber.i(RECEIVED_RESPONSE + "getAllQuestionnaires: $response")
                try {
                    val dataJSON: JSONObject = response.getJSONObject("data")
                    sharedViewModel.updateQuestionnaires(
                        jsonParser.getQuestionnaireListFromJsonArray(dataJSON.getJSONArray("attributes"))
                    )
                    Timber.i(SUCCESSFULLY_PARSED)
                } catch (e: JSONException) {
                    Timber.i(JSON_EXCEPTION + "Questionnaires! \n" + e.printStackTrace())
                }
            },
            { error ->
                Timber.i(RECEIVED_ERROR + "getAllQuestionnaires: $error")
                sharedViewModel.loadQuestionnairesFromDeviceDatabase()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return addHeader(contentHeader = false, roleHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }

    /**
     * This function loads all available [AnswerSheet]s from the webserver.
     * If successful a function on the [SharedViewModel] is called to update the AnswerSheets.
     *
     * @param token the user's token, needed for authentication
     * @param sharedViewModel the [SharedViewModel] holding logic
     * */
    fun getAllAnswerSheets(token: String, sharedViewModel: SharedViewModel) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            getNeededURL(REQUEST_TYPE_GET_ANSWER_SHEETS, token),
            null,
            { response ->
                Timber.i(RECEIVED_RESPONSE + "getAllAnswerSheets: $response")
                try {
                    val dataJSON: JSONObject = response.getJSONObject("data")
                    sharedViewModel.updateAnswerSheets(
                        jsonParser.getAnswerSheetListFromJsonArray(dataJSON.getJSONArray("attributes"))
                    )
                    Timber.i(SUCCESSFULLY_PARSED)
                } catch (e: JSONException) {
                    Timber.i(JSON_EXCEPTION + "AnswerSheets! \n" + e.printStackTrace())
                }
            },
            { error ->
                Timber.i(RECEIVED_ERROR + "getAllAnswerSheets request: $error")
                sharedViewModel.loadAnswerSheetsFromDeviceDatabase()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return addHeader(contentHeader = false, roleHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }

    /**
     * This function sends a [LoginRequest] to the webserver.
     *
     * @param token the user's token, needed for authentication
     * */
    fun sendLogoutRequest(token: String) {
        val stringRequest: StringRequest = object : StringRequest(
            Method.DELETE,
            getNeededURL(REQUEST_TYPE_LOGOUT, token),
            { response ->
                Timber.i(RECEIVED_RESPONSE + "logout request: $response")
            },
            { error ->
                Timber.i(RECEIVED_ERROR + "logout request: $error")

            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return addHeader(contentHeader = true, roleHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    /**
     * This function returns a headers [Map] depending on requested headers in the params.
     *
     * @param contentHeader determines if a content header shall be set
     * @param languageHeader determines if a language header shall be set
     *
     * @return a [Map] of headers
     * */
    private fun addHeader(
        contentHeader: Boolean,
        roleHeader: Boolean = false,
        languageHeader: Boolean = true
    ): Map<String, String> {
        val headers = HashMap<String, String>()
        if (contentHeader) headers[Constants.HEADER_CONTENT_TYPE] =
            Constants.CONTENT_TYPE_APPLICATION
        if (roleHeader) headers[Constants.HEADER_ROLE] =
            Constants.ROLE_TYPE_USER
        if (languageHeader) headers[Constants.HEADER_ACCEPT_LANGUAGE] =
            Constants.ACCEPTED_LANGUAGE_DE
        return headers
    }

    /**
     * This function calculates the needed url for a given request type.
     *
     * @param requestType the type of request
     * @param token the token needed for authentication
     * @param answerSheetId the id of the [AnswerSheet] that shall be send if so
     * */
    private fun getNeededURL(
        requestType: Int,
        token: String = "",
        answerSheetId: String = ""
    ): String {
        val tokenString = Constants.TOKEN_URI + token
        var url = Constants.SERVER_URL
        url += when (requestType) {
            REQUEST_TYPE_REGISTRATION -> ""
            REQUEST_TYPE_LOGIN -> ""
            REQUEST_TYPE_LOGOUT -> tokenString
            REQUEST_TYPE_SEND_ANSWER_SHEET -> Constants.QUESTIONNAIRES_URI + answerSheetId + Constants.ANSWER_SHEETS_URI + tokenString
            REQUEST_TYPE_RESET_PASSWORD -> ""
            REQUEST_TYPE_GET_QUESTIONNAIRES -> tokenString
            REQUEST_TYPE_GET_ANSWER_SHEETS -> Constants.QUESTIONNAIRES_URI + tokenString
            else -> ""
        }
        return url
    }
}