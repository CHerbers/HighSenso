package name.herbers.android.highsenso

/**
 *
 *@project HighSenso
 *@author Herbers
 */
object Constants {

    const val OFFLINE_MODE = true
    const val FIRST_START = false
    const val DELETE_DATABASE = false
    /* Questionnaire */
    const val BASELINE_QUESTIONNAIRE = "Baseline"
    const val BASELINE_QUESTIONNAIRE_ID = 1
    const val HSP_SCALE_QUESTIONNAIRE = "HSP_Scala"
    const val HSP_SCALE_QUESTIONNAIRE_ID = 2
    const val DEAL_WITH_HS_QUESTIONNAIRE = "DealingWithHS"
    const val DEAL_WITH_HS_QUESTIONNAIRE_ID = 3
    const val LOCATION_QUESTION_LABEL = "current_location"

    /* Question names */
    const val QUESTION_LABEL_ENTHUSIASM = "enthusiasm"
    const val QUESTION_LABEL_EMOTIONAL_VULNERABILITY = "emotional_vulnerability"
    const val QUESTION_LABEL_SELF_DOUBT = "self_doubt"
    const val QUESTION_LABEL_WORLD_PAIN = "world_pain"
    const val QUESTION_LABEL_LINGERING_EMOTION = "lingering_emotion"
    const val QUESTION_LABEL_SOCIAL_ISOLATED = "social_isolated"
    const val QUESTION_LABEL_FREQUENT_ANXIETY = "frequent_anxiety"
    const val QUESTION_LABEL_UNSATISFACTORY_LIFE = "unsatisfactory_life"
    const val QUESTION_LABEL_WORKPLACE_MOOD = "workspace_mood"
    const val QUESTION_LABEL_WORKSPACE_STIMULUS = "workspace_stimulus"

    /* Communication */
    const val TOKEN_DURABILITY_TIME =
        10000L //TODO adjust durability to actual token durability

    /* Data */
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