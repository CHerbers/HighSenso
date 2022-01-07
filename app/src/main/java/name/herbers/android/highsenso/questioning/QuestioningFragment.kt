package name.herbers.android.highsenso.questioning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.slider.Slider
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.data.Questionnaire
import name.herbers.android.highsenso.databinding.FragmentQuestioningBinding
import name.herbers.android.highsenso.dialogs.EndQuestioningDialog
import name.herbers.android.highsenso.result.ResultFragment
import name.herbers.android.highsenso.result.ResultViewModel
import name.herbers.android.highsenso.start.StartFragment
import timber.log.Timber

/**
 * In the [QuestioningFragment] the user is asked to rate questions. The result whether the user is an
 * HSP or not will be calculated later on in the [ResultViewModel] depending on the users rating.
 * After rating a question there will be asked another question until the questioning is completed.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class QuestioningFragment : Fragment() {

    private lateinit var binding: FragmentQuestioningBinding
    private lateinit var viewModel: QuestioningViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init DataBinding and QuestioningViewModel
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_questioning,
            container,
            false
        )
        val sharedViewModel: SharedViewModel by activityViewModels()
        val databaseHandler = sharedViewModel.databaseHandler

        //checks if this Fragment was called from PersonalQuestioningFragment
        var startingQuestionPos = 0
        var startingQuestionnairePos = 0
        if (sharedViewModel.backFromResult) {
            startingQuestionPos = databaseHandler.questionnaires.last().questions.size - 1
            startingQuestionnairePos = sumOfQuestionnaires(databaseHandler.questionnaires) - 1
        }

        //reset backFromPersonalQuestioning
        sharedViewModel.backFromResult = false
        sharedViewModel.startGatherSensorData()

        //init QuestioningViewModel
        val viewModelFactory =
            QuestioningViewModelFactory(
                sharedViewModel,
                startingQuestionPos,
                startingQuestionnairePos
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(QuestioningViewModel::class.java)
        binding.questioningViewModel = viewModel
        binding.lifecycleOwner = this

        /* Set actionBar (title and button) */
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title =
                resources.getString(R.string.questioning_actionBar_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        //add Observers
        addLiveDataObservers(sharedViewModel)

        //set Listeners
        setListeners()

        enableAnswerButton(false)

        Timber.i("QuestionFragment created!")
        return binding.root
    }

    /**
     * Override fun to define an action when actionBar back button is clicked.
     * If clicked a fun in the [QuestioningViewModel] is called to handle the click.
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Timber.i("actionBar back button clicked!")
                viewModel.handleBackButtonClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Calls functions that add Observers to the LiveData.
     *
     * @param sharedViewModel is the [SharedViewModel] holding functionality for the added observers
     * */
    private fun addLiveDataObservers(sharedViewModel: SharedViewModel) {
        addQuestionRatingObserver()
        addBackToStartObserver(sharedViewModel)
        addIsFinishedObserver()
        addNavToResultFragmentObserver(sharedViewModel)
        addIsLastQuestionObserver()
    }

    /**
     * Observed [QuestioningViewModel.questionRating] changes if the question changes.
     * Depending on the rating the [LiveData] is changed to, ether one of the [RadioButton]s gets
     * checked or all checks get cleared.
     * */
    private fun addQuestionRatingObserver() {
        viewModel.questionRating.observe(viewLifecycleOwner, { rating ->
            val radioGroup = binding.questioningRadioGroup
            val nextButton = binding.questionNextButton
            when (rating) {
                "0" -> {
                    radioGroup.check(binding.questioningNegativeRadioButton.id)
                    nextButton.isEnabled = true
                }
                "1" -> {
                    radioGroup.check(binding.questioningPositiveRadioButton.id)
                    nextButton.isEnabled = true
                }
                else -> radioGroup.clearCheck()
            }
        })
    }

    /**
     * Observed [QuestioningViewModel.navBackToStartFrag] is true if the shown question is the first
     * question. If it is false after backButton is clicked, the Fragment changes
     * to [StartFragment].
     *
     * @param sharedViewModel is the [SharedViewModel] holding functionality for this observer
     * */
    private fun addBackToStartObserver(sharedViewModel: SharedViewModel) {
        viewModel.navBackToStartFrag.observe(viewLifecycleOwner, { isFirstQuestion ->
            if (isFirstQuestion) {
                sharedViewModel.stopGatherSensorData()
                findNavController(this)
                    .navigate(R.id.action_questioning_to_start)
            }
        })
    }

    /**
     * Counts the sum of given [Questionnaire]s that are not [Constants.BASELINE_QUESTIONNAIRE].
     *
     * @param questionnaires the [List] of [Questionnaire]s that shall be counted
     * @return the number of relevant [Questionnaire]s
     * */
    private fun sumOfQuestionnaires(questionnaires: List<Questionnaire>): Int {
        var sum = 0
        questionnaires.forEach { questionnaire ->
            if (questionnaire.name != Constants.BASELINE_QUESTIONNAIRE) sum++
        }
        return sum
    }

    /**
     * Observed [QuestioningViewModel.isFinished] becomes true after the nextButton is clicked
     * while the last question was shown.
     * If isFinished is true, the [EndQuestioningDialog] is shown.
     * */
    private fun addIsFinishedObserver() {
        viewModel.isFinished.observe(viewLifecycleOwner, { isFinished ->
            if (isFinished) {
                EndQuestioningDialog(viewModel).show(
                    childFragmentManager,
                    EndQuestioningDialog.TAG
                )
            }
        })
    }

    /**
     * Observed NavBackToResultFragment becomes true after the [EndQuestioningDialog] was answered
     * positive.
     * If NavBackToResultFragment is true, Fragment changes to [ResultFragment]
     *
     * @param sharedViewModel is the [SharedViewModel] holding functionality for this observer
     * */
    private fun addNavToResultFragmentObserver(sharedViewModel: SharedViewModel) {
        viewModel.navToResultFrag.observe(viewLifecycleOwner, { toNav ->
            if (toNav) {
                sharedViewModel.stopGatherSensorData()
                sharedViewModel.createAndSendAnswerSheets()
                findNavController(this)
                    .navigate(R.id.action_questioning_destination_to_result_destination)
            }

        })
    }

    /**
     * Observed isFinished becomes true if the current question is the last question.
     * isFinished becomes false if the current question is not the last question.
     * Depending if isFinished is true or false, another text is shown on the nextButton.
     * */
    private fun addIsLastQuestionObserver() {
        viewModel.isLastQuestion.observe(viewLifecycleOwner, { isLastQuestion ->
            if (isLastQuestion) binding.questionNextButton.text =
                resources.getString(R.string.confirm_and_move_to_result)
            else binding.questionNextButton.text = resources.getString(R.string.next_button)
        })
    }

    /**
     * Calls functions that add Listeners to the backButton, nextButton and seekBar.
     * */
    private fun setListeners() {
        addAnswerButtonListener()
        addRadioButtonListener(binding.questioningPositiveRadioButton)
        addRadioButtonListener(binding.questioningNegativeRadioButton)
    }

    /**
     * Adds a Click Listener to the nextButton, which progresses to next [Question] or
     * to [ResultFragment].
     * */
    private fun addAnswerButtonListener() {
        binding.questionNextButton.setOnClickListener {
            Timber.i("NextButton was clicked!")
            //reset checked status on both radioButtons
            enableAnswerButton(false)
            viewModel.handleNextButtonClick(
                if (binding.questioningPositiveRadioButton.isChecked) 1 else 0
            )
        }
    }

    /**
     * This function enables or disables the next button and changes its background color, depending
     * on the given input.
     *
     * @param toEnable enables [Button] if true, disables it otherwise
     * */
    private fun enableAnswerButton(toEnable: Boolean) {
        binding.questionNextButton.isEnabled = toEnable
        val backgroundColor =
            if (toEnable) R.color.question_answerButton_default_color
            else R.color.question_answerButton_disabled_color
        binding.questionNextButton.setBackgroundColor(requireContext().getColor(backgroundColor))
    }

    /**
     * Sets a [Slider.OnChangeListener] to a [RadioButton].
     * If the RadioButton gets checked, a function to enable the answerButton is called.
     *
     * @param radioButton the [RadioButton] the listener is set on
     * */
    private fun addRadioButtonListener(radioButton: RadioButton) {
        radioButton.setOnCheckedChangeListener { _, isChecked ->
            Timber.i("RadioButton was clicked!")
            if (isChecked) {
                enableAnswerButton(true)
            }
        }
    }
}

