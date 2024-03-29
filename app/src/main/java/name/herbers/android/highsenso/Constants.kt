package name.herbers.android.highsenso

import name.herbers.android.highsenso.Constants.OFFLINE_MODE
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.data.Questionnaire

/**
 * Holds constants for the whole application.
 *
 * *IMPORTANT*:
 *
 * The constant [OFFLINE_MODE] is managed here. This constant enables or disables the
 * use of this app without a running server. More information about this constant is commented at
 * its declaration in this class.
 *
 *@project HighSenso
 *@author Herbers
 */
object Constants {

    /**
     * This constant manages the offline usage of the HighSenso App!
     *
     * If OFFLINE_MODE is false, this app can only properly be used with a running server!
     * This is especially because there is no way to log in without a server.
     *
     * If OFFLINE_MODE is true, the user is free to use all features of the app!
     *
     * It is not necessary to type in login data in the login dialog,
     * just press "login" and the app can be used as if properly logged in.
     *
     * [Questionnaire]s are either loaded from the on device database
     * (if ever loaded from the server before)
     * or read from .json-files in the "assets/questionnaires" folder.
     *
     * In offline mode [AnswerSheet]s are not saved after finishing the questioning
     * (the app will still try to send them to the server)!
     *
     * This is to eliminate possible inconsistency between server data and local data.
     * The result of the current questioning will still be shown.
     * */
    const val OFFLINE_MODE = true
    /**
     * This constant is only for testing.
     *
     * It simulates a first start of the app after installation.
     * */
    const val TEST_FIRST_START = false
    /**
     * This constant is only for testing.
     *
     * If it is true, three dummy [AnswerSheet]s can be loaded from .json files in the
     * assets folder.
     * */
    const val TEST_USE_DUMMY_ANSWER_SHEETS = false

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
    const val TOKEN_DURABILITY_TIME = 10000L //TODO adjust durability to actual token durability
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val HEADER_ACCEPT_LANGUAGE = "Accept-Language"
    const val CONTENT_TYPE_APPLICATION = "application/json"
    const val ACCEPTED_LANGUAGE_DE = "de"
    const val HEADER_ROLE = "Role"
    const val ROLE_TYPE_USER = "user"

    /* URI */ //TODO insert right URIs
    const val SERVER_URL = "https://www.google.com"
    const val QUESTIONNAIRES_URI = "studies/1/questionnaires/"
    const val ANSWER_SHEETS_URI = "/answersheets/"
    const val TOKEN_URI = "?token="

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

    const val FIELD_ID = "id"
    const val  FIELD_NAME = "name"
    const val FIELD_COLLECTED_AT = "collected_at"
    const val FIELD_QUESTIONS = "questions"
    const val FIELD_ANSWERS = "answers"
    const val FIELD_SENSOR_DATA = "sensor_data"
    const val FIELD_CLIENT = "client"
    const val FIELD_LOCALE = "locale"

    const val QUESTION_NO_ANSWER_DEFAULT = 9

    /* Sensor */
    const val AUDIO_SENSOR_MEASURING_DURATION = 8
    const val SENSOR_MEASURING_INTERVAL_NANOS =
        5000000000L    //Interval between two sensor measurements in nanoseconds
}