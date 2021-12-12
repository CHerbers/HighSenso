package name.herbers.android.highsenso.connection

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import name.herbers.android.highsenso.data.AnswerSheet
import org.json.JSONException
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class ServerCommunicationHandler(private val serverURL: String, private val context: Context) {
    private val answerSheetList = mutableListOf<AnswerSheet>()
    val gson = Gson()
    private val questionnairesURLString = "studies/1/questionnaires/1/"
    private val answerSheetURLString = "answersheets"
    private val tokenURLString = "?token="

    init {
//        val bodyJSON = gson.toJson(arrayOf("one", "two"))
//        Timber.i(bodyJSON.toString())
//        Timber.i(String(bodyJSON.toString().toByteArray()))

        Timber.i("ServerCommunicationHandler initialized!")
    }

    fun sendGETRequest() {
        val stringRequest = StringRequest(
            Request.Method.GET,
            serverURL,
            { response ->
                Timber.i("Received response to GET request is: $response")
            },
            { error ->
                Timber.i("Received error after GET request: $error")
            })
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    fun sendRegisterRequest(requestBody: Objects) {
        val url = serverURL
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Timber.i("Received response to send register request is: $response")
                //TODO show Dialog "U got Mail -> OK"
            },
            { error ->
                Timber.i("Received error after registerRequest: $error")
                //TODO Error handling
            }) {
            override fun getBody(): ByteArray {
                val bodyJSON = gson.toJson(requestBody)
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

    fun sendLoginRequest(requestBody: Objects) {
        val url = serverURL
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Timber.i("Received response to send login request is: $response")
                //TODO save the received token
                // send GET to get the questions into send GET to get all sheets
            },
            { error ->
                Timber.i("Received error after registerRequest: $error")
            }) {
            override fun getBody(): ByteArray {
                val bodyJSON = gson.toJson(requestBody)
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

    fun sendAnswerSheet(token: String, answerSheet: AnswerSheet) {
        val url =
            serverURL + questionnairesURLString + answerSheetURLString + tokenURLString + token
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Timber.i("Received response to send answerSheet (POST) is: $response")
            },
            { error ->
                Timber.i("Received error after registerRequest: $error")

            }) {
            override fun getBody(): ByteArray {
                val bodyJSON = gson.toJson(answerSheet)
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

    fun sendResetPasswordRequest() {}

    fun getQuestionnaire(token: String){}

    fun getAllAnswerSheets(token: String) {
        val url = serverURL + questionnairesURLString + tokenURLString + token
        val answerSheets: Array<AnswerSheet>
        val jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            serverURL,
            null,
            { response ->
                Timber.i("Received response to get AnswerSheets request is: $response")
                try {
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        answerSheetList.add(data.get(i) as AnswerSheet)
                    }
                    //TODO save answerSheets in ViewModel?
                } catch (e: JSONException) {

                }
            },
            { error ->
                Timber.i("Received error after get AnswerSheets request: $error")
            })
        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }
}