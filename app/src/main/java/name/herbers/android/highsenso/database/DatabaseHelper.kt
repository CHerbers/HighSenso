package name.herbers.android.highsenso.database

import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.data.*
import name.herbers.android.highsenso.data.Question
import java.util.*

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class DatabaseHelper {

    fun getQuestionnaires(): List<Questionnaire> {
        return listOf(
            getBaselineQuestionnaire(),
            getHSPScalaQuestionnaire(),
            getDealWithHSQuestionnaire()
        )
    }

    private fun getBaselineQuestionnaire(): Questionnaire {
        return Questionnaire(
            Constants.BASELINE_QUESTIONNAIRE_ID, Constants.BASELINE_QUESTIONNAIRE, listOf(
                Headlines(
                    4,
                    listOf(TranslationHeadline("Bitte gib uns ein paar Angaben zu Deiner Person."))
                ),
                Question(
                    2,
                    "element2",
                    Constants.QUESTION_TYPE_DATE,
                    "birthdate",
                    "",
                    listOf(TranslationQuestion("Was ist Dein Geburtsdatum", listOf<String>())),
                    "PASTS"
                ),
                Question(
                    3,
                    "element3",
                    Constants.QUESTION_TYPE_SINGLE_CHOICE,
                    "sex",
                    listOf("0", "1", "2"),
                    listOf(
                        TranslationQuestion(
                            "Welches Geschlecht hast Du?",
                            listOf("Weiblich", "Männlich", "Divers")
                        )
                    )
                ),
                Question(
                    4,
                    "element4",
                    Constants.QUESTION_TYPE_SINGLE_CHOICE_KNOB,
                    "country",
                    listOf("AFG", "EGY", "ALB", "DZA", "AND"),
                    listOf(
                        TranslationQuestion(
                            "In welchem Land lebst Du aktuell?",
                            listOf(
                                "Afghanistan",
                                "Ägypten",
                                "Albanien",
                                "Algerien",
                                "Andorra"
                            )
                        )
                    )
                ),
                Question(
                    5,
                    "element5",
                    Constants.QUESTION_TYPE_SINGLE_CHOICE,
                    "family_status",
                    listOf("1", "2", "3", "4", "5", "6", "7"),
                    listOf(
                        TranslationQuestion(
                            "Welchen Familienstand hast Du?", listOf(
                                "Verheiratet bzw. in fester Partnerschaft",
                                "Verheiratet, getrennt lebend",
                                "Geschieden",
                                "In eingetragener Partnerschaft (gleichgeschlechtlich)",
                                "In eingetragener Partnerschaft (gleichgeschlechtlich), getrennt lebend",
                                "Verwitwet",
                                "Ledig"
                            )
                        )
                    )
                ),
                Question(
                    6,
                    "element6",
                    Constants.QUESTION_TYPE_KNOB,
                    "children",
                    Values(0, 20, 1),
                    listOf(
                        TranslationQuestion(
                            "Wie viele Kinder hast Du?", listOf(
                                Answer("0", "0"), Answer("20", "20")

                            )
                        )
                    )
                ),
                Question(
                    7,
                    "element7",
                    Constants.QUESTION_TYPE_SINGLE_CHOICE_KNOB,
                    "education",
                    listOf("0", "1", "2", "3", "4", "5", "6", "9"),
                    listOf(
                        TranslationQuestion(
                            "Was ist Dein höhchster Bildungsabschluss?", listOf(
                                "Keine formale Bildung",
                                "Primarschule (Grundschulbildung)",
                                "Sekundarstufe I (Abschluss der Sekundarstufe, der keinen Zugang zu einer Universität ermöglicht)",
                                "Obere Sekundarstufe (Studiengänge, die den Zugang zur Universität ermöglichen)",
                                "Postsekundäre, nicht-tertiäre Bildung (andere Bildungsgänge der Sekundarstufe II)",
                                "Tertiärstufe, erste Stufe (auch Fachschulen)",
                                "Oberer Tertiärbereich (Master, Doktor)",
                                "Keine Antwort, andere"
                            )
                        )
                    )
                ),
                Question(
                    8,
                    "element8",
                    Constants.QUESTION_TYPE_SINGLE_CHOICE,
                    "employment_relationship",
                    listOf("0", "1", "2", "3", "4", "5", "6", "9", "10"),
                    listOf(
                        TranslationQuestion(
                            "In welchem Arbeitsverhätnis stehst Du?", listOf(
                                "Schüler*in",
                                "Student*in",
                                "Arbeitssuchend",
                                "Angestellt (Teilzeit)",
                                "Angestellt (Vollzeit)",
                                "Selbstständig",
                                "Berentet",
                                "Anderes",
                                "Keine Angabe"
                            )
                        )
                    )
                ),
                Question(
                    9,
                    "element9",
                    Constants.QUESTION_TYPE_TEXT_STRING,
                    "profession",
                    "",
                    listOf(TranslationQuestion("Was ist dein Beruf?", listOf<String>()))
                ),
            )
        )
    }

    private fun getHSPScalaQuestionnaire(): Questionnaire {
        return Questionnaire(
            Constants.HSP_SCALE_QUESTIONNAIRE_ID, Constants.HSP_SCALE_QUESTIONNAIRE, listOf(

            )
        )
    }

    private fun getDealWithHSQuestionnaire(): Questionnaire {
        return Questionnaire(
            Constants.DEAL_WITH_HS_QUESTIONNAIRE_ID, Constants.DEAL_WITH_HS_QUESTIONNAIRE, listOf(

            )
        )
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
                    "1-1-2022",
                    "1-1-2022",
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
}