package name.herbers.android.highsenso.database

import android.content.Context
import com.google.gson.Gson
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.data.*
import name.herbers.android.highsenso.data.Question
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.util.*

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class DatabaseHelper {

    private val gson = Gson()

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


    fun getAnswerSheets(): List<AnswerSheet> {
        return listOf(getBaselineAnswerSheet())
    }

    private fun getBaselineAnswerSheet(): AnswerSheet {
        val client = Client(
            android.os.Build.MODEL,
            android.os.Build.DEVICE,
            android.os.Build.VERSION.RELEASE
        )
        return AnswerSheet(
            1,
            Date().time,
            listOf(
                Answer(
                    "1.1.2022",
                    "1.1.2022",
                    Date().time
                ),
                Answer(
                    "0",
                    "Webilich",
                    Date().time
                ),
                Answer(
                    "AFG",
                    "Afghanistan",
                    Date().time
                ),
                Answer(
                    "1",
                    "Verheiratet bzw. in fester Partnerschaft",
                    Date().time
                ),
                Answer(
                    "3",
                    "3",
                    Date().time
                ),
                Answer(
                    "0",
                    "Keine formale Bildung",
                    Date().time
                ),
                Answer(
                    "2",
                    "Arbeitssuchend",
                    Date().time
                ),
                Answer(
                    "Student",
                    "Student",
                    Date().time
                )
            ),
            listOf(
                AmbientLightSensorData(Date().time, 1F),
                AmbientAudioSensorData(Date().time, 0.4f),
                AmbientTempSensorData(Date().time, 17F)
            ),
            client
        )
    }

    private fun getQuestionListFromJsonObject(jsonObject: JSONObject): List<Element> {
        val array = jsonObject.getJSONArray(Constants.ELEMENTS)
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
}