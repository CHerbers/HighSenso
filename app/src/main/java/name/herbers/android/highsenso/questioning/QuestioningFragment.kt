package name.herbers.android.highsenso.questioning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.database.Question
import name.herbers.android.highsenso.databinding.FragmentQuestioningBinding
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
        val startingQuestionPos =
            if (sharedViewModel.backFromResult) databaseHandler.questions.size - 1
            else 0

        //reset backFromPersonalQuestioning
        sharedViewModel.backFromResult = false

        //init QuestioningViewModel
        val viewModelFactory =
            QuestioningViewModelFactory(databaseHandler, startingQuestionPos)
        viewModel = ViewModelProvider(this, viewModelFactory).get(QuestioningViewModel::class.java)
        binding.questioningViewModel = viewModel
        binding.lifecycleOwner = this

        //set actionBar (title and button)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title =
                resources.getString(R.string.questioning_actionBar_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        //add Observers
        addLiveDataObservers()

        //set Listeners
        setListeners()

        Timber.i("QuestionFragment created!")
        return binding.root
    }

    /**
     * Override fun to define an action when actionBar back button is clicked.
     * If clicked a fun in the [QuestioningViewModel] is called to handle the click.
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.i("$item clicked!")
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
     * */
    private fun addLiveDataObservers() {
        addBackToStartObserver()
        addIsFinishedObserver()
    }

    /**
     * Observed isFirstQuestion is true if the shown question is the first question.
     * If isFirstQuestion is false after backButton is clicked, the Fragment changes
     * to [StartFragment].
     * */
    private fun addBackToStartObserver() {
        viewModel.navBackToStartFrag.observe(viewLifecycleOwner, { isFirstQuestion ->
            if (isFirstQuestion) {
                findNavController(this)
                    .navigate(R.id.action_questioning_to_start)
            }
        })
    }

    /**
     * Observed isFinished becomes true after the nextButton is clicked while the last question
     * was shown.
     * If isFinished is true, Fragment changes to [PersonalQuestioningFragment]
     * */
    private fun addIsFinishedObserver() {
        viewModel.isFinished.observe(viewLifecycleOwner, { isFinished ->
            if (isFinished) {
                findNavController(this)
                    .navigate(R.id.action_questioning_destination_to_result_destination)
            }
        })
    }

    /**
     * Calls functions that add Listeners to the backButton, nextButton and seekBar.
     * */
    private fun setListeners() {
        addAnswerButtonListener(binding.agreeButton, true, "agreeButton")
        addAnswerButtonListener(binding.declineButton, false, "declineButton")
    }

    /**
     * Adds a Click Listener to the nextButton, which progresses to next [Question] or
     * to [ResultFragment].
     * */
    private fun addAnswerButtonListener(button: Button, rating: Boolean, logMessage: String) {
        button.setOnClickListener {
            Timber.i("$logMessage was clicked!")
            viewModel.handleAnswerButtonClick(rating)
        }
    }
}

