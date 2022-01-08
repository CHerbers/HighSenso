package name.herbers.android.highsenso.database

import android.content.Context
import com.google.gson.Gson
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.data.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

/**
 * This class exist to work around the non existence of a webserver.
 * It provides the three needed [Questionnaire]s by reading them from .json files in the
 * assets/questionnaires folder.
 *
 *@project HighSenso
 *@author Herbers
 */
class OfflineHelper {
    private val gson = Gson()

    /**
     * This function reads the questionnaires from the .json files in the assets/questionnaires folder,
     * creates [Questionnaire] object and returns them in a [List].
     *
     * @param context the applications context, needed to access the assets folder
     * @return a [List] of [Questionnaire]s read from .json files
     * */
    fun getQuestionnaires(context: Context): List<Questionnaire> {
        try {
            val path = "questionnaires/highsenso_"
            val questionnaireList = mutableListOf<Questionnaire>()

            val jsonObjectBaseline = JSONObject(
                context.assets.open(path + "Baseline_structure.json").bufferedReader()
                    .use { it.readText() }
            )
            val jsonObjectHSP = JSONObject(
                context.assets.open(path + "HSPScala_structure.json").bufferedReader()
                    .use { it.readText() }
            )
            val jsonObjectDWHS = JSONObject(
                context.assets.open(path + "DealingWithHS_structure.json").bufferedReader()
                    .use { it.readText() }
            )

            questionnaireList.add(
                Questionnaire(
                    Constants.BASELINE_QUESTIONNAIRE_ID,
                    Constants.BASELINE_QUESTIONNAIRE,
                    getQuestionListFromJsonObject(jsonObjectBaseline)
                )
            )
            questionnaireList.add(
                Questionnaire(
                    Constants.HSP_SCALE_QUESTIONNAIRE_ID,
                    Constants.HSP_SCALE_QUESTIONNAIRE,
                    getQuestionListFromJsonObject(jsonObjectHSP)
                )
            )
            questionnaireList.add(
                Questionnaire(
                    Constants.DEAL_WITH_HS_QUESTIONNAIRE_ID,
                    Constants.DEAL_WITH_HS_QUESTIONNAIRE,
                    getQuestionListFromJsonObject(jsonObjectDWHS)
                )
            )
            return questionnaireList
        } catch (e: IOException) {
            Timber.i("Exception while trying to create Questionnaire list from assets! + ${e.printStackTrace()}")
        }
        return listOf()
    }

    fun getAnswerSheets(context: Context): List<AnswerSheet> {

        val answerSheetList = mutableListOf<AnswerSheet>()
        val jsonObjectList = mutableListOf<JSONObject>()

        for (i in 1..100) {
            val path = "answersheets/answersheet$i.json"
            try {
                jsonObjectList.add(
                    JSONObject(
                        context.assets.open(path).bufferedReader().use { it.readText() })
                )
            } catch (e: IOException) {
                Timber.i("Exception while trying to create AnswerSheets list from assets! $e")
                break
            }
        }

        jsonObjectList.forEach { answerSheet ->
            answerSheetList.add(
                AnswerSheet(
                    answerSheet.getInt("id"),
                    answerSheet.getLong("collected_at"),
                    getAnswersListFromJSONArray(answerSheet.getJSONArray("answers")),
                    try {
                        getSensorDataListFromJSONArray(answerSheet.getJSONArray("sensor_data"))
                    } catch (e: JSONException) {
                        null
                    },
                    gson.fromJson(
                        answerSheet.getJSONObject("client").toString(),
                        Client::class.java
                    ),
                    answerSheet.getString("locale")
                )
            )
        }
        return answerSheetList
    }

    /**
     * Extracts the [Element]s from a given [JSONObject]. Because an [Element] is either an [Headlines]
     * or a [Question], [Gson] fails to automatically parse them.
     *
     * @param jsonObject a [JSONObject] that should represent a [List] of [Element]s in JSON format
     * @return a [List] of [Element]s parsed from the given [JSONObject]
     * */
    private fun getQuestionListFromJsonObject(jsonObject: JSONObject): List<Element> {
        return getQuestionListFromJsonArray(jsonObject.getJSONArray(Constants.ELEMENTS))
    }

    fun getQuestionListFromJsonArray(array: JSONArray): List<Element> {
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

    fun getAnswersListFromJSONArray(array: JSONArray): List<Answer> {
        val answerList = mutableListOf<Answer>()

        for (i in 0 until array.length()) {
            val answer = array.getJSONObject(i)
            answerList.add(
                gson.fromJson(answer.toString(), Answer::class.java)
            )
        }
        return answerList
    }

    fun getSensorDataListFromJSONArray(array: JSONArray): List<SensorData> {
        val sensorDataList = mutableListOf<SensorData>()

        for (i in 0 until array.length()) {
            val sensorData = array.getJSONObject(i)
            when (sensorData.getString("name")) {
                "ambientAudioSensorData" -> sensorDataList.add(
                    gson.fromJson(sensorData.toString(), AmbientAudioSensorData::class.java)
                )
                "ambientLightSensorData" -> sensorDataList.add(
                    gson.fromJson(sensorData.toString(), AmbientLightSensorData::class.java)
                )
                "ambientTempSensorData" -> sensorDataList.add(
                    gson.fromJson(sensorData.toString(), AmbientTempSensorData::class.java)
                )
            }
        }
        return sensorDataList
    }
}