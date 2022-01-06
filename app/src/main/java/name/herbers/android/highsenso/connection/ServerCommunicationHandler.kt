package name.herbers.android.highsenso.connection

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.data.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class ServerCommunicationHandler(private val serverURL: String, val context: Context) {
    private val answerSheetList = mutableListOf<AnswerSheet>()
    val gson = Gson()

    /* data string fields */
    //general
    private val nameField = context.getString(R.string.name_field)
    private val valueField = context.getString(R.string.value_field)
    private val collectedAtField = context.getString(R.string.collected_at_field)
    private val labelField = context.getString(R.string.label_field)
    private val idField = context.getString(R.string.id_field)

    //question
    private val minField = context.getString(R.string.min_field)
    private val maxField = context.getString(R.string.max_field)
    private val stepField = context.getString(R.string.step_field)
    private val requiredField = context.getString(R.string.required_field)
    private val variableField = context.getString(R.string.variable_field)
    private val localeField = context.getString(R.string.locale_field)
    private val answerField = context.getString(R.string.answers_field)
    private val sensorDataField = context.getString(R.string.sensor_data_field)
    private val clientField = context.getString(R.string.client_field)
    private val questionTypeField = context.getString(R.string.question_type_field)
    private val questionField = context.getString(R.string.questions_field)

    //sensorData
    private val ambientAudioSDField = context.getString(R.string.ambient_audio_sd_field)
    private val ambientTempSDField = context.getString(R.string.ambient_temp_sd_field)
    private val ambientLightSDField = context.getString(R.string.ambient_light_sd_field)
    private val amplitudeField = context.getString(R.string.amplitude_field)
    private val luxField = context.getString(R.string.lux_field)
    private val degreesField = context.getString(R.string.degrees_field)

    //client
    private val deviceField = context.getString(R.string.degrees_field)
    private val osField = context.getString(R.string.os_field)

    /* header fields */
    private val contentTypeHeader = context.getString(R.string.content_type_header)
    private val contentTypeApplication = context.getString(R.string.content_type_application)
    private val acceptLanguageHeader = context.getString(R.string.accept_language_header)
    private val acceptLanguageDE = context.getString(R.string.accept_language_de)

    companion object {
        // URL constants
        private const val QUESTIONNAIRES_URL = "studies/1/questionnaires/"
        private const val ANSWER_SHEETS_URL = "/answersheets"
        private const val TOKEN_URL = "?token="

        //Timber logging messages constants
        private const val RECEIVED_RESPONSE = "Received response for "
        private const val RECEIVED_ERROR = "Received error for "
        private const val JSON_EXCEPTION =
            "An JSONException occurred while trying to parse received response into "
        private const val SUCCESSFULLY_PARSED = "Successfully pared response into data!"
    }

    init {
        Timber.i("ServerCommunicationHandler initialized!")
    }

    fun sendRegistrationRequest(
        registrationRequest: RegistrationRequest,
        sharedViewModel: SharedViewModel
    ) {
        val url = Constants.SERVER_URL
        val request = RequestData(Data("users", registrationRequest))
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Timber.i(RECEIVED_RESPONSE + "registrationRequest: $response")
                sharedViewModel.registerResponseReceived(response)
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

    fun sendLoginRequest(
        username: String,
        password: String,
        sharedViewModel: SharedViewModel,
        answerSheets: List<AnswerSheet>?
    ) {
        val url = Constants.SERVER_URL
        val loginRequest = LoginRequest(username, password)
        val request = RequestData(Data("users", loginRequest))
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Timber.i(RECEIVED_RESPONSE + "loginRequest: $response")
                sharedViewModel.loginResponseReceived(response, username, password, answerSheets)
            },
            { error ->
                sharedViewModel.loginErrorReceived(error)
                Timber.i(RECEIVED_ERROR + "loginRequest: $error")
            }) {
            override fun getBody(): ByteArray {
                val bodyJSON = gson.toJson(request)
                Timber.i("Login request sent: $bodyJSON")
                return bodyJSON.toString().toByteArray()
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return addHeader(contentHeader = true, languageHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

//    fun sendLoginRequestForAnswerSheets(username: String, password: String, sharedViewModel: SharedViewModel) {
//        val url = serverURL
//        val loginRequest = LoginRequest(username, password)
//        val request = RequestData(Data("users", loginRequest))
//        val stringRequest: StringRequest = object : StringRequest(
//            Method.POST,
//            url,
//            { response ->
//                Timber.i(RECEIVED_RESPONSE + "loginRequest: $response")
//                sharedViewModel.sendAnswerSheets(response, username, password)
//            },
//            { error ->
//                Timber.i(RECEIVED_ERROR + "loginRequest: $error")
//            }) {
//            override fun getBody(): ByteArray {
//                val bodyJSON = gson.toJson(request)
//                Timber.i("Login request sent: $bodyJSON")
//                return bodyJSON.toString().toByteArray()
//            }
//
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                return addHeader(contentHeader = true, languageHeader = true)
//            }
//        }
//        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
//    }

    fun sendAnswerSheet(token: String, answerSheet: AnswerSheet) {
        val url =
            Constants.SERVER_URL + Constants.QUESTIONNAIRES_URI + answerSheet.id + Constants.ANSWER_SHEETS_URI + Constants.TOKEN_URI + token //TODO check URL
        val request = RequestData(Data("questionnaires", answerSheet))
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Timber.i(RECEIVED_RESPONSE + "sendAnswerSheet (POST): $response")
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
                return addHeader(contentHeader = true, languageHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    fun sendResetPasswordRequest(mail: String, sharedViewModel: SharedViewModel) {
        val url = Constants.SERVER_URL //TODO complete URL
        val resetPasswordRequest = ResetPasswordRequest(mail)
        val request = RequestData(Data("users", resetPasswordRequest))
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
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
                return addHeader(contentHeader = true, languageHeader = true)
            }

            override fun getBody(): ByteArray {
                val bodyJSON = gson.toJson(request)
                Timber.i("Login request sent: $bodyJSON")
                return bodyJSON.toString().toByteArray()
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }


    fun getAllQuestionnaires(token: String, sharedViewModel: SharedViewModel) {
        val url = Constants.SERVER_URL + Constants.TOKEN_URI + token //TODO url
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            { response ->
                Timber.i(RECEIVED_RESPONSE + "getAllQuestionnaires: $response")
                try {
                    val dataJSON: JSONObject = response.getJSONObject("data")
                    sharedViewModel.updateQuestionnaires(
                        getQuestionnaireListFromJSONArray(dataJSON.getJSONArray("attributes"))
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
                return addHeader(contentHeader = false, languageHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }

    //TODO test his gson stuff
//    fun getAllQuestionnaires(token: String, sharedViewModel: SharedViewModel) {
//        val url = serverURL //TODO url
//        val questionnaires = mutableListOf<Questionnaire>()
//        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
//            Method.GET,
//            url,
//            null,
//            { response ->
//                Timber.i(RECEIVED_RESPONSE + "getAllQuestionnaires: $response")
//                try {
//                    val dataJSON: JSONObject = response.getJSONObject("data")
//                    val attributes = dataJSON.getJSONArray("attributes")
//                    for (i in 0 until attributes.length()) {
//                        questionnaires.add(
//                            gson.fromJson(
//                                attributes[i].toString(),
//                                Questionnaire::class.java
//                            )
//                        )
//                    }
//                    sharedViewModel.updateQuestionnaires(questionnaires)
//                    Timber.i(SUCCESSFULLY_PARSED)
//                } catch (e: JSONException) {
//                    Timber.i(JSON_EXCEPTION + "Questionnaires! \n" + e.printStackTrace())
//                }
//            },
//            { error ->
//                Timber.i(RECEIVED_ERROR + "getAllQuestionnaires: $error")
//            }) {
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                return addHeader(contentHeader = false, languageHeader = true)
//            }
//        }
//        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
//    }
//
//    //TODO test this gson stuff too
//    fun getAllQuestionnairesALT2(token: String, sharedViewModel: SharedViewModel) {
//        val url = serverURL //TODO url
//        val questionnaires: List<Questionnaire>
//        val jsonObjectRequest: StringRequest = object : StringRequest(
//            Method.GET,
//            url,
//            { response ->
//                Timber.i(RECEIVED_RESPONSE + "getAllQuestionnaires: $response")
//                try {
//                    val data = gson.fromJson(response, Data::class.java)
//                    if (data.attributes is List<*>) {
//                        if (data.attributes is Questionnaire) {
//                            sharedViewModel.updateQuestionnaires(data.attributes as List<Questionnaire>)
//                        }
//                    }
////                    val dataJSON: JSONObject = response.getJSONObject("data")
////                    sharedViewModel.questionnaires =
////                        getQuestionnaireListFromJSONArray(dataJSON.getJSONArray("attributes"))
//                    Timber.i(SUCCESSFULLY_PARSED)
//                } catch (e: JSONException) {
//                    Timber.i(JSON_EXCEPTION + "Questionnaires! \n" + e.printStackTrace())
//                }
//            },
//            { error ->
//                Timber.i(RECEIVED_ERROR + "getAllQuestionnaires: $error")
//            }) {
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                return addHeader(contentHeader = false, languageHeader = true)
//            }
//        }
//        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
//    }

    fun getAllAnswerSheets(token: String, sharedViewModel: SharedViewModel) {
        val url = Constants.SERVER_URL + Constants.QUESTIONNAIRES_URI + Constants.TOKEN_URI + token //TODO check URL
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            { response ->
                Timber.i(RECEIVED_RESPONSE + "getAllAnswerSheets: $response")
                try {
                    val dataJSON: JSONObject = response.getJSONObject("data")
//                    val answerSheets = mutableListOf<AnswerSheet>()
//                    val attributes = dataJSON.getJSONArray("attributes")
//                    for (i in 0 until attributes.length()) {
//                        answerSheets.add(
//                            gson.fromJson(
//                                attributes[i].toString(),
//                                AnswerSheet::class.java
//                            )
//                        )
//                    }
//                    sharedViewModel.updateAnswerSheets(answerSheets)
                    sharedViewModel.updateAnswerSheets(
                        getAnswerSheetListFromJSONArray(dataJSON.getJSONArray("attributes"))
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
                return addHeader(contentHeader = false, languageHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }

    fun sendLogoutRequest(token: String) {
        val url = serverURL + "" + TOKEN_URL + token //TODO complete URL
        val stringRequest: StringRequest = object : StringRequest(
            Method.DELETE,
            url,
            { response ->
                Timber.i(RECEIVED_RESPONSE + "logout request: $response")
            },
            { error ->
                Timber.i(RECEIVED_ERROR + "logout request: $error")

            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return addHeader(contentHeader = true, languageHeader = true)
            }
        }
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    private fun addHeader(contentHeader: Boolean, languageHeader: Boolean): Map<String, String> {
        val headers = HashMap<String, String>()
        if (contentHeader) headers[contentTypeHeader] = contentTypeApplication
        if (languageHeader) headers[acceptLanguageHeader] = acceptLanguageDE
        return headers
    }

    private fun getQuestionnaireListFromJSONArray(array: JSONArray): List<Questionnaire> {
        val questionnaireList = mutableListOf<Questionnaire>()

        for (i in 0 until array.length()) {
            val questionnaire = array.getJSONObject(i)
            questionnaireList.add(
                Questionnaire(
                    questionnaire.getInt(idField),
                    questionnaire.getString(nameField),
                    getQuestionListFromJSONArray(questionnaire.getJSONArray(questionField))
                )
            )
        }
        return questionnaireList
    }

    private fun getQuestionListFromJSONArray(array: JSONArray): List<Element> {
        val elementList = mutableListOf<Element>()

        for (i in 0 until array.length()) {
            val element = array.getJSONObject(i)
            if (element.getString(Constants.ELEMENT_TYPE) == Constants.ELEMENT_TYPE_HEADLINE) {
                elementList.add(
                    gson.fromJson(element.toString(), Headlines::class.java)
                )
            } else {
                elementList.add(
                    gson.fromJson(element.toString(), Question::class.java)
                )
            }
        }
        return elementList
    }

    private fun getAnswerSheetListFromJSONArray(array: JSONArray): List<AnswerSheet> {
        val answerSheetList = mutableListOf<AnswerSheet>()

        for (i in 0 until array.length()) {
            val answerSheet = array.getJSONObject(i)
            answerSheetList.add(
                AnswerSheet(
                    answerSheet.getInt("id"),
                    answerSheet.getLong(collectedAtField),
                    getAnswersListFromJSONArray(answerSheet.getJSONArray(answerField)),
                    getSensorDataListFromJSONArray(answerSheet.getJSONArray(sensorDataField)),
//                    getClientFromJSONObject(answerSheet.getJSONObject(clientField)),
                    gson.fromJson(answerSheet.getJSONObject(clientField).toString(), Client::class.java),
                    answerSheet.getString(localeField)
                )
            )
        }
        return answerSheetList
    }

    private fun getAnswersListFromJSONArray(array: JSONArray): List<Answer> {
        val answerList = mutableListOf<Answer>()

        for (i in 0 until array.length()) {
            val answer = array.getJSONObject(i)
            answerList.add(
                gson.fromJson(answer.toString(), Answer::class.java)
//                Answer(
//                    answer.getString(valueField),
//                    answer.getString(labelField),
//                    answer.getLong(collectedAtField)
//                )
            )
        }
        return answerList
    }

    private fun getSensorDataListFromJSONArray(array: JSONArray): List<SensorData> {
        val sensorDataList = mutableListOf<SensorData>()

        for (i in 0 until array.length()) {
            val sensorData = array.getJSONObject(i)
            when (sensorData.getString(nameField)) {
                ambientAudioSDField -> sensorDataList.add(
                    gson.fromJson(sensorData.toString(), AmbientAudioSensorData::class.java)
//                    AmbientAudioSensorData(
//                        sensorData.getLong(collectedAtField),
//                        sensorData.getDouble(amplitudeField).toFloat()
//                    )
                )
                ambientLightSDField -> sensorDataList.add(
                    gson.fromJson(sensorData.toString(), AmbientLightSensorData::class.java)
//                    AmbientLightSensorData(
//                        sensorData.getLong(collectedAtField),
//                        sensorData.getDouble(luxField).toFloat()
//                    )
                )
                ambientTempSDField -> sensorDataList.add(
                    gson.fromJson(sensorData.toString(), AmbientTempSensorData::class.java)
//                    AmbientTempSensorData(
//                        sensorData.getLong(collectedAtField),
//                        sensorData.getDouble(degreesField).toFloat()
//                    )
                )
            }
        }
        return sensorDataList
    }

//    private fun getClientFromJSONObject(jsonObject: JSONObject): Client {
//        return Client(
//            jsonObject.getString(nameField),
//            jsonObject.getString(deviceField),
//            jsonObject.getString(osField)
//        )
//    }
}