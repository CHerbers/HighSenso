package name.herbers.android.highsenso

/**
 *
 *@project HighSenso
 *@author Herbers
 */
object Constants {

    const val OFFLINE_MODE = true
    /* Questionnaire */
    const val BASELINE_QUESTIONNAIRE = "Baseline"
    const val BASELINE_QUESTIONNAIRE_ID = 1
    const val HSP_SCALE_QUESTIONNAIRE = "HSP_Scala"
    const val HSP_SCALE_QUESTIONNAIRE_ID = 2
    const val DEAL_WITH_HS_QUESTIONNAIRE = "DealingWithHS"
    const val DEAL_WITH_HS_QUESTIONNAIRE_ID = 3

    /* Communication */
    const val TOKEN_DURABILITY_TIME =
        10000L //TODO adjust durability to actual token durability

    /* Data */
    const val LOCALE = "DEU"
    const val ELEMENTS = "elements"
    const val ELEMENT_TYPE = "elementtype"
    const val ELEMENT_TYPE_QUESTION = "elements/questions"
    const val ELEMENT_TYPE_HEADLINE = "elements/headlines"
    const val QUESTION_TYPE_DATE = "TextDate"
    const val QUESTION_TYPE_SINGLE_CHOICE = "SingleChoice"
    const val QUESTION_TYPE_SINGLE_CHOICE_KNOB = "SingleChoiceKnob"
    const val QUESTION_TYPE_KNOB = "Knob"
    const val QUESTION_TYPE_TEXT_STRING = "TextString"


    /* Sensor */
    const val AUDIO_SENSOR_MEASURING_DURATION = 8
    const val SENSOR_MEASURING_INTERVAL_NANOS =
        5000000000L    //Interval between two sensor measurements in nanoseconds

    /* URI */
    const val SERVER_URL = "https://www.google.com" //TODO insert right URL
    const val QUESTIONNAIRES_URI = "studies/1/questionnaires/"
    const val ANSWER_SHEETS_URI = "/answersheets"
    const val TOKEN_URI = "?token="
}