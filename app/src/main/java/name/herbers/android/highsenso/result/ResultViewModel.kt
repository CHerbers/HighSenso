package name.herbers.android.highsenso.result

import android.annotation.SuppressLint
import android.app.Application
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.data.Answer
import name.herbers.android.highsenso.data.AnswerSheet
import timber.log.Timber

/**
 * [AndroidViewModel] for the [ResultFragment].
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class ResultViewModel(
    val sharedViewModel: SharedViewModel,
    application: Application
) : AndroidViewModel(application) {

    private val appRes = application.applicationContext.resources

    /* some constants */
    companion object {
        private const val HSP_POSITIVE_LIMIT = 14
    }

    /* Observed LiveData */
    //LiveData observed by Fragment to decide if a TextView is shown
    private val _hasEnthusiasm = MutableLiveData<Boolean>()
    val hasEnthusiasm: LiveData<Boolean>
        get() = _hasEnthusiasm

    private val _isEmotionalVulnerable = MutableLiveData<Boolean>()
    val isEmotionalVulnerable: LiveData<Boolean>
        get() = _isEmotionalVulnerable

    private val _hasSelfDoubt = MutableLiveData<Boolean>()
    val hasSelfDoubt: LiveData<Boolean>
        get() = _hasSelfDoubt

    private val _hasWorldPain = MutableLiveData<Boolean>()
    val hasWorldPain: LiveData<Boolean>
        get() = _hasWorldPain

    private val _hasLingeringEmotions = MutableLiveData<Boolean>()
    val hasLingeringEmotions: LiveData<Boolean>
        get() = _hasLingeringEmotions

    private val _isSuffering = MutableLiveData<Boolean>()
    val isSuffering: LiveData<Boolean>
        get() = _isSuffering

    private val _hasWorkplaceProblem = MutableLiveData<Boolean>()
    val hasWorkplaceProblem: LiveData<Boolean>
        get() = _hasWorkplaceProblem

    //LiveData observed by TextViews
    private val _resultGeneralHspContent = MutableLiveData<String>()
    val resultGeneralHspContent: LiveData<String>
        get() = _resultGeneralHspContent

    private val _sufferingMessageContent = MutableLiveData<String>()
    val sufferingMessageContent: LiveData<String>
        get() = _sufferingMessageContent

    private val _resultPersonalContent = MutableLiveData<String>()
    val resultPersonalContent: LiveData<String>
        get() = _resultPersonalContent

    private val _resultConditionalContent = MutableLiveData<String>()
    val resultConditionalContent: LiveData<String>
        get() = _resultConditionalContent

    init {
        Timber.i("ResultViewModel created!")
        calculateResult()
    }

    /**
     * This function is calculating, which messages should be shown onscreen, depending on the
     * question rating the user has given.
     * */
    private fun calculateResult() {
        /* Calculate the HSP-Scala-Score */
        var currentRatingSum = 0
        sharedViewModel.currentAnswers[Constants.HSP_SCALE_QUESTIONNAIRE]?.forEach { (_, answer) ->
            currentRatingSum += answer.value.toInt()
        }
//        if (currentRatingSum > -1) currentRatingSum++
        Timber.i("Current rating: $currentRatingSum")
        val ratingSum = getAverageSumOfOldAnswerSheets(currentRatingSum)
        Timber.i("Total rating sum: $ratingSum!")

        /* General check if user is a HSP and generating message */
        _resultGeneralHspContent.value =
            buildGeneralResultString(if (ratingSum == -1) 0 else ratingSum)

        /* checks the influence questions (besides suffering and workplace) and triggers actions to
        * show their specific messages onscreen */
        checkInfluenceQuestions()

        /* Suffering check and message generation */
        if (checkSuffering()) {
            Timber.i("User seems to suffer!")
            _isSuffering.value = true
            _sufferingMessageContent.value =
                if (currentRatingSum < HSP_POSITIVE_LIMIT) appRes.getString(R.string.detected_suffering_negative_hsp_message)
                else appRes.getString(R.string.detected_suffering_positive_hsp_message)
        }

        /* Workplace problems check and message generation */
        if (checkWorkplaceProblems()) {
            Timber.i("User seems to feel uncomfortable at their workplace!")
            _hasWorkplaceProblem.value = true
        }
    }

    /**
     * This function calculates the average sum of positive answered questions form all old [AnswerSheet]s.
     *
     * @return -1 if there are no old nor current AnswerSheet(s) for the "HSP-Scale"
     * else the mean of the sum of positive answered items of the old und current AnswerSheet(s)
     * */
    private fun getAverageSumOfOldAnswerSheets(currentScore: Int): Int {
        var sum = 0
        var count = 0
        var noOldAnswerSheets = true
        sharedViewModel.answerSheets?.forEach { answerSheet ->
            if (answerSheet.id == Constants.HSP_SCALE_QUESTIONNAIRE_ID) {
                noOldAnswerSheets = false
                count++
                answerSheet.answers.forEach { answer ->
                    sum += answer.value.toInt()
                }
            }
        }
        if (noOldAnswerSheets) return currentScore
        //check if there are current answers for the HSP Scale
        return if (currentHspScaleAnswersAvailable()) sum.div(count)
        else (sum + currentScore).div(count + 1)
    }

    /**
     * This function returns if there are current [Answer]s for the HSP-Scale-Questionnaire
     * available.
     *
     * @return true if there are answers available, false otherwise
     * */
    private fun currentHspScaleAnswersAvailable(): Boolean{
        return sharedViewModel.currentAnswers[Constants.HSP_SCALE_QUESTIONNAIRE].isNullOrEmpty()
    }

    /**
     * This function checks the rating of the influence questions and sets the specific observed
     * [LiveData] to true, to trigger the corresponding [TextView] to show onscreen.
     * */
    private fun checkInfluenceQuestions() {
        getAnswersFromLabels(listOf(Constants.QUESTION_LABEL_ENTHUSIASM)).forEach { answer ->
            if (answer.value == "1") _hasEnthusiasm.value = true
        }
        getAnswersFromLabels(listOf(Constants.QUESTION_LABEL_EMOTIONAL_VULNERABILITY)).forEach { answer ->
            if (answer.value == "1") _isEmotionalVulnerable.value = true
        }
        getAnswersFromLabels(listOf(Constants.QUESTION_LABEL_SELF_DOUBT)).forEach { answer ->
            if (answer.value == "1") _hasSelfDoubt.value = true
        }
        getAnswersFromLabels(listOf(Constants.QUESTION_LABEL_WORLD_PAIN)).forEach { answer ->
            if (answer.value == "1") _hasWorldPain.value = true
        }
        getAnswersFromLabels(listOf(Constants.QUESTION_LABEL_LINGERING_EMOTION)).forEach { answer ->
            if (answer.value == "1") _hasLingeringEmotions.value = true
        }
    }

    /**
     * This function checks the rating of the questions that are meant to detect if the user is
     * suffering.
     * If one of those three questions was answered positively, the return value is true.
     *
     * @return true if the user is suffering, false otherwise
     * */
    private fun checkSuffering(): Boolean {
        getAnswersFromLabels(
            listOf(
                Constants.QUESTION_LABEL_SOCIAL_ISOLATED,
                Constants.QUESTION_LABEL_FREQUENT_ANXIETY,
                Constants.QUESTION_LABEL_UNSATISFACTORY_LIFE
            )
        ).forEach { answer ->
            if (answer.value == "1") return true
        }
        return false
    }

    /**
     * This function checks the rating of the questions that are meant to detect, if the user feels
     * uncomfortable at their workplace.
     * If one of those two questions was answered positively, the return value is true.
     *
     * @return true, if the user feels uncomfortable at their workplace, false otherwise
     * */
    private fun checkWorkplaceProblems(): Boolean {
        getAnswersFromLabels(
            listOf(
                Constants.QUESTION_LABEL_WORKPLACE_MOOD,
                Constants.QUESTION_LABEL_WORKSPACE_STIMULUS
            )
        ).forEach { answer ->
            if (answer.value == "1") return true
        }
        return false
    }

    /**
     * Searches all [Answer]s for the ones with the given labels and returns them if found
     *
     * @param labels a [List] of the labels, the [Answer]s are searched of
     * @return a [List] of found [Answer]s
     * */
    private fun getAnswersFromLabels(labels: List<String>): List<Answer> {
        val answers = mutableListOf<Answer>()
        sharedViewModel.currentAnswers.forEach { (_, questionnaires) ->
            questionnaires.forEach { (_, answer) ->
                labels.forEach { label ->
                    if (answer.label == label) answers.add(answer)
                }
            }
        }
        return answers
    }

    /**
     * This functions build the string, that shows the result, if the user is a HSP and their
     * HSP-Scale-Score.
     * */
    @SuppressLint("StringFormatMatches")
    fun buildGeneralResultString(rating: Int): String {
        var resultString: String
        val isNegative = rating < HSP_POSITIVE_LIMIT
        var iteration = if (currentHspScaleAnswersAvailable()) 0 else 1
        val answerSheets = sharedViewModel.answerSheets
        answerSheets?.forEach { answerSheet ->
            if (answerSheet.id == Constants.HSP_SCALE_QUESTIONNAIRE_ID){
                iteration++
            }
        }

        val probabilityArray = appRes.getStringArray(R.array.general_HSP_result_probability_array)
        val resultPositivity =
            if (rating > HSP_POSITIVE_LIMIT * 1.5 || rating < HSP_POSITIVE_LIMIT * 0.5)
                probabilityArray[0]
            else probabilityArray[1]

        resultString = appRes.getString(
            R.string.general_HSP_part_0_declaration,
            resultPositivity,
            if (isNegative) {
                appRes.getString(R.string.general_HSP_negative)
            } else ""
        )
        resultString += appRes.getString(
            R.string.general_HSP_part_1_score,
            rating
        )
        resultString +=
            if (isNegative) appRes.getString(R.string.negative_HSP_message)
            else appRes.getString(R.string.positive_HSP_message)
        resultString += appRes.getString(R.string.general_HSP_part_2_iteration, iteration)
        return resultString
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ResultViewModel destroyed!")
    }
}