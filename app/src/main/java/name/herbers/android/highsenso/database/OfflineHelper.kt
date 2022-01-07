package name.herbers.android.highsenso.database

import android.content.Context
import com.google.gson.Gson
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.data.*
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.util.*

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
                    "birthdate",
                    Date().time
                ),
                Answer(
                    "0",
                    "sex",
                    Date().time
                ),
                Answer(
                    "AFG",
                    "country",
                    Date().time
                ),
                Answer(
                    "1",
                    "family_status",
                    Date().time
                ),
                Answer(
                    "3",
                    "children",
                    Date().time
                ),
                Answer(
                    "0",
                    "education",
                    Date().time
                ),
                Answer(
                    "2",
                    "employment_relationship",
                    Date().time
                ),
                Answer(
                    "Student",
                    "profession",
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

    /**
     * Extracts the [Element]s from a given [JSONObject]. Because an [Element] is either an [Headlines]
     * or a [Question], [Gson] fails to automatically parse them.
     *
     * @param jsonObject a [JSONObject] that should represent a [List] of [Element]s in JSON format
     * @return a [List] of [Element]s parsed from the given [JSONObject]
     * */
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