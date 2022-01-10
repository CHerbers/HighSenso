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
class HighSensoJsonParser {
    private val gson = Gson()

    /**
     * This function reads the Questionnaires from the .json files in the assets/questionnaires folder,
     * creates [Questionnaire]s and returns them in a [List].
     *
     * @param context the applications context, needed to access the assets folder
     * @return a [List] of [Questionnaire]s read from .json files, or an empty list if reading or
     * parsing fails
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

    /**
     * This function reads the AnswerSheets from the .json files in the assets/answersheets folder,
     * creates [AnswerSheet]s and returns them in a [List].
     *
     * @param context the applications context, needed to access the assets folder
     * @return a [List] of [AnswerSheet]s read from .json files, or an empty list if reading or
     * parsing fails
     * */
    fun getAnswerSheets(context: Context): List<AnswerSheet>? {
        if (!Constants.TEST_USE_DUMMY_ANSWER_SHEETS) return null
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

        try {
            jsonObjectList.forEach { answerSheet ->
                answerSheetList.add(
                    AnswerSheet(
                        answerSheet.getInt(Constants.FIELD_ID),
                        answerSheet.getLong(Constants.FIELD_COLLECTED_AT),
                        getAnswersListFromJSONArray(answerSheet.getJSONArray(Constants.FIELD_ANSWERS)),
                        try {
                            getSensorDataListFromJSONArray(answerSheet.getJSONArray(Constants.FIELD_SENSOR_DATA))
                        } catch (e: JSONException) {
                            null
                        },
                        gson.fromJson(
                            answerSheet.getJSONObject(Constants.FIELD_CLIENT).toString(),
                            Client::class.java
                        ),
                        answerSheet.getString(Constants.FIELD_LOCALE)
                    )
                )
            }
        } catch (e: JSONException) {
            Timber.i("Failed to get SensorData List from JSONArray: $e")
        }
        return answerSheetList
    }

    /**
     * Extracts the [AnswerSheet]s from a given [JSONArray] and returns them in a [List].
     *
     * @return a [List] of [AnswerSheet]s parsed from the given [JSONArray], or an empty
     * list if reading or parsing fails
     * */
    fun getAnswerSheetListFromJsonArray(array: JSONArray): List<AnswerSheet> {
        val answerSheetList = mutableListOf<AnswerSheet>()
        return try {
            for (i in 0 until array.length()) {
                val answerSheet = array.getJSONObject(i)
                answerSheetList.add(
                    AnswerSheet(
                        answerSheet.getInt(Constants.FIELD_ID),
                        answerSheet.getLong(Constants.FIELD_COLLECTED_AT),
                        getAnswersListFromJSONArray(answerSheet.getJSONArray(Constants.FIELD_ANSWERS)),
                        try {
                            getSensorDataListFromJSONArray(answerSheet.getJSONArray(Constants.FIELD_SENSOR_DATA))
                        } catch (e: JSONException) {
                            null
                        },
                        gson.fromJson(
                            answerSheet.getJSONObject(Constants.FIELD_CLIENT).toString(),
                            Client::class.java
                        ),
                        answerSheet.getString(Constants.FIELD_LOCALE)
                    )
                )
            }
            answerSheetList
        } catch (e: JSONException) {
            Timber.i("Failed to get AnswerSheets List from JSONArray: $e")
            listOf()
        }
    }

    /**
     * Extracts the elements [JSONArray] from a given [JSONObject].
     *
     * @param jsonObject a [JSONObject] that should represent a [List] of [Element]s in JSON format
     *
     * @return a [List] of [Element]s parsed from the given [JSONObject], or an empty List if parsing
     * fails
     * */
    private fun getQuestionListFromJsonObject(jsonObject: JSONObject): List<Element> {
        return try {
            getQuestionListFromJsonArray(jsonObject.getJSONArray(Constants.ELEMENTS))
        } catch (e: JSONException) {
            Timber.i("Failed get elements JSONArray from JSONObject: $e")
            listOf()
        }
    }

    /**
     * Extracts the [List] of [Questionnaire]s from a given [JSONArray].
     *
     * @param array a [JSONArray] that should represent a [List] of [Questionnaire]s in JSON format
     *
     * @return a [List] of [Questionnaire] parsed from the given [JSONArray], or an empty List if
     * parsing fails
     * */
    fun getQuestionnaireListFromJsonArray(array: JSONArray): List<Questionnaire> {
        val questionnaireList = mutableListOf<Questionnaire>()
        return try {
            for (i in 0 until array.length()) {
                val questionnaire = array.getJSONObject(i)
                questionnaireList.add(
                    Questionnaire(
                        questionnaire.getInt(Constants.FIELD_ID),
                        questionnaire.getString(Constants.FIELD_NAME),
                        getQuestionListFromJsonArray(questionnaire.getJSONArray(Constants.FIELD_QUESTIONS))
                    )
                )
            }
            questionnaireList
        } catch (e: JSONException) {
            Timber.i("Failed to get Questionnaires List from JSONArray: $e")
            listOf()
        }
    }

    /**
     * Extracts the [Element]s from a given [JSONArray]. Because an [Element] is either an [Headlines]
     * or a [Question], [Gson] fails to automatically parse them.
     *
     * @param array a [JSONArray] that should represent a [List] of [Element]s in JSON format
     * @return a [List] of [Element]s parsed from the given [JSONArray], or an empty List if parsing
     * fails
     * */
    fun getQuestionListFromJsonArray(array: JSONArray): List<Element> {
        val elementList = mutableListOf<Element>()
        return try {
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
            elementList
        } catch (e: JSONException) {
            Timber.i("Failed to get Elements List from JSONArray: $e")
            listOf()
        }
    }

    /**
     * This function parses a given [JSONArray] in a [List] of [Answer]s
     *
     * @param array a [JSONArray] containing a [List] of [Answer]s
     *
     * @return a [List] of [Answer]s parsed from the given [JSONArray], or an empty List if
     * parsing fails
     * */
    fun getAnswersListFromJSONArray(array: JSONArray): List<Answer> {
        val answerList = mutableListOf<Answer>()
        return try {
            for (i in 0 until array.length()) {
                val answer = array.getJSONObject(i)
                answerList.add(
                    gson.fromJson(answer.toString(), Answer::class.java)
                )
            }
            answerList
        } catch (e: JSONException) {
            Timber.i("Failed to get Answers List from JSONArray: $e")
            listOf()
        }
    }

    /**
     * This function parses a given [JSONArray] in a [List] of [SensorData]
     *
     * @param array a [JSONArray] containing a [List] of [SensorData]
     *
     * @return a [List] of [SensorData] parsed from the given [JSONArray], or an empty List if
     * parsing fails
     * */
    fun getSensorDataListFromJSONArray(array: JSONArray): List<SensorData> {
        val sensorDataList = mutableListOf<SensorData>()
        return try {
            for (i in 0 until array.length()) {
                val sensorData = array.getJSONObject(i)
                when (sensorData.getString(Constants.FIELD_NAME)) {
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
            sensorDataList
        } catch (e: JSONException) {
            Timber.i("Failed to get SensorData List from JSONArray: $e")
            listOf()
        }
    }
}