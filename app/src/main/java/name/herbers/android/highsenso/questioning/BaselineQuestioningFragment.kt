package name.herbers.android.highsenso.questioning

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.data.Headlines
import name.herbers.android.highsenso.data.Question
import name.herbers.android.highsenso.databinding.FragmentBaselineQuestioningBinding
import name.herbers.android.highsenso.dialogs.LocationDialogFragment
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class BaselineQuestioningFragment : Fragment() {

    private lateinit var binding: FragmentBaselineQuestioningBinding
    private lateinit var viewModel: BaselineQuestioningViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and QuestioningViewModel
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_baseline_questioning,
            container,
            false
        )
        val sharedViewModel: SharedViewModel by activityViewModels()
        val application = requireNotNull(this.activity).application
        val viewModelFactory =
            BaselineQuestioningViewModelFactory(application)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(BaselineQuestioningViewModel::class.java)
        binding.personalQuestioningViewModel = viewModel
        binding.lifecycleOwner = this

        val preferences = (activity as AppCompatActivity).getPreferences(Context.MODE_PRIVATE)

        //set title
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title =
                resources.getString(R.string.questioning_actionBar_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)
        val adapter = QuestionAdapter(sharedViewModel, viewModel)
        val baselineQuestions = mutableListOf<Question>()
        val baselineQuestionnaireElements =
            sharedViewModel.getQuestionnaireByQuestionnaireName(Constants.BASELINE_QUESTIONNAIRE)?.questions
        baselineQuestionnaireElements?.forEach { element ->
            Timber.i("Current baseline element: ${element.elementtype}")
            if (element.elementtype == Constants.ELEMENT_TYPE_QUESTION) {
                baselineQuestions.add(element as Question)
            } else if (element.elementtype == Constants.ELEMENT_TYPE_HEADLINE) {
                binding.personalQuestioningTitleTextView.text =
                    ((element as Headlines).translations[0].headline)
            }
        }
        adapter.data = baselineQuestions
        binding.baselineRecyclerView.adapter = adapter

        setLocationDialogObserver(sharedViewModel)

        //next button
        setNextButtonListener(sharedViewModel, preferences)

        Timber.i("BaselineQuestionFragment created!")
        return binding.root
    }

    /**
     *
     * */
    private fun setNextButtonListener(
        sharedViewModel: SharedViewModel,
        preferences: SharedPreferences
    ) {
        binding.questionNextButton.setOnClickListener {
            Timber.i("nextButton was clicked!")
            if (viewModel.validInput()) {
                if (sharedViewModel.locationQuestionAvailable()) {
                    LocationDialogFragment(preferences, sharedViewModel).show(
                        childFragmentManager,
                        "LocationDialog"
                    )
                } else {
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_personalQuestioning_destination_to_questioning_destination)
                }
            } else {
                Toast.makeText(context, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Override fun to define an action when actionBar back button is clicked.
     * If clicked a fun in the [QuestioningViewModel] is called to handle the click.
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Timber.i("actionBar back button clicked!")
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_personalQuestioning_destination_to_start_destination)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Sets an Observer to [SharedViewModel.locationDialogDismiss]. Navigates to [QuestioningFragment]
     * if true.
     *
     * @param sharedViewModel is the [SharedViewModel] holding the observed [LiveData]
     * */
    private fun setLocationDialogObserver(sharedViewModel: SharedViewModel) {
        sharedViewModel.locationDialogDismiss.observe(viewLifecycleOwner, { dismissed ->
            if (dismissed) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_personalQuestioning_destination_to_questioning_destination)
            }
        })
    }
}