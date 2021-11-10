package name.herbers.android.highsenso.questioning

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.database.PersonalData
import name.herbers.android.highsenso.databinding.FragmentPersonalQuestioningBinding
import name.herbers.android.highsenso.result.ResultFragment
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PersonalQuestioningFragment : Fragment() {

    private lateinit var binding: FragmentPersonalQuestioningBinding
    private lateinit var viewModel: PersonalQuestioningViewModel
    private lateinit var personalData: PersonalData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and QuestioningViewModel
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_personal_questioning,
            container,
            false
        )
        val sharedViewModel: SharedViewModel by activityViewModels()
        val databaseHandler = sharedViewModel.databaseHandler
        val application = requireNotNull(this.activity).application
        val viewModelFactory =
            PersonalQuestioningViewModelFactory(databaseHandler, application)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(PersonalQuestioningViewModel::class.java)
        binding.personalQuestioningViewModel = viewModel
        binding.lifecycleOwner = this

        personalData = sharedViewModel.personalData

        //set title
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.questioning_actionBar_title)

        //init spinners (dropdown selections)
        initAllSpinners()

        //init editTexts
        initAllEditTexts()

        //next button
        binding.nextButton.setOnClickListener {
            Timber.i("nextButton was clicked!")
            viewModel.handleNextButtonClick()
        }

        //back button
        binding.backButton.setOnClickListener {
            Timber.i("backButton was clicked!")
            viewModel.handleBackButtonClick()
        }

        /**
         * Observed isFinished becomes true after the nextButton is clicked while the last question
         * was shown.
         * If isFinished is true, Fragment changes to [ResultFragment]
         * */
        viewModel.isFinished.observe(viewLifecycleOwner, { isFinished ->
            if (isFinished) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_personalQuestioningFragment_to_result_destination)
            }
        })

        Timber.i("PersonalQuestionFragment created!")
        return binding.root
    }

    /**
     * Calls [initSpinner] for every of the three [Spinner]s with its corresponding string-array
     * and starting selection.
     * */
    private fun initAllSpinners() {
        initSpinner(
            binding.genderSpinner,
            R.array.gender_array,
            personalData.gender
        )
        initSpinner(
            binding.martialStatusSpinner,
            R.array.marital_Status_array,
            personalData.martialStatus
        )
        initSpinner(
            binding.educationSpinner,
            R.array.education_array,
            personalData.education
        )
    }

    /**
     * Fills a [Spinner] with data from a string-array given in [res].
     * Sets the current selection position of the Spinner to given [selection].
     * Adds an [AdapterView.OnItemSelectedListener] to the Spinner which changes the [personalData]
     * depending on the new selection position.
     * @param spinner is the Spinner that is manipulated
     * @param res is the id of a string-array
     * @param selection is the current selection position
     * */
    private fun initSpinner(spinner: Spinner, res: Int, selection: Int) {
        ArrayAdapter.createFromResource(
            requireContext(),
            res,
            android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        }
        spinner.setSelection(selection)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (spinner) {
                    binding.genderSpinner -> {
                        personalData.gender = position
                        Timber.i("Gender was changed to '${personalData.genderString}'!")
                    }
                    binding.martialStatusSpinner -> {
                        personalData.martialStatus = position
                        Timber.i("Martial status was changed to '${personalData.martialStatusString}'!")
                    }
                    binding.educationSpinner -> {
                        personalData.education = position
                        Timber.i("Education was changed to '${personalData.educationString}'!")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing (needed override fun)
            }
        }
    }

    private fun initAllEditTexts() {
        initEditText(binding.ageEditText)
        initEditText(binding.childrenEditText)
        initEditText(binding.professionEditText)
    }

    private fun initEditText(editText: EditText) {
        /* EditText shows realtime error if input is invalid */
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null) return
                val errorMessage = when (editText) {
                    binding.ageEditText -> viewModel.getAgeErrorMessage(s.toString())
                    binding.childrenEditText -> viewModel.getChildrenErrorMessage(s.toString())
                    binding.professionEditText -> viewModel.getProfessionErrorMessage(s.toString())
                    else -> ""
                }

                if (errorMessage != "") {
                    editText.error = errorMessage
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //not needed
            }
        })
    }
}