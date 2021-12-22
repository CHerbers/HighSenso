package name.herbers.android.highsenso.questioning

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.database.UserProfile
import name.herbers.android.highsenso.databinding.FragmentPersonalQuestioningBinding
import name.herbers.android.highsenso.dialogs.LocationDialogFragment
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PersonalQuestioningFragment : Fragment() {

    private lateinit var binding: FragmentPersonalQuestioningBinding
    private lateinit var viewModel: PersonalQuestioningViewModel
    private lateinit var userProfile: UserProfile

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

        val preferences = (activity as AppCompatActivity).getPreferences(Context.MODE_PRIVATE)

        userProfile = sharedViewModel.userProfile

        //set title
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title =
                resources.getString(R.string.questioning_actionBar_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        setLocationDialogObserver(sharedViewModel)

        //init spinners (dropdown selections)
        initAllSpinners()

        //init editTexts
        initAllEditTexts()

        //next button
        binding.questionNextButton.setOnClickListener {
            Timber.i("nextButton was clicked!")
//            viewModel.handleNextButtonClick()
            if (dataComplete()) {
                LocationDialogFragment(preferences, sharedViewModel).show(
                    childFragmentManager,
                    "LocationDialog"
                )
//                NavHostFragment.findNavController(this)
//                    .navigate(R.id.action_personalQuestioning_destination_to_questioning_destination)
            } else {
                Toast.makeText(context, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show()
            }
        }

        Timber.i("PersonalQuestionFragment created!")
        return binding.root
    }

    private fun dataComplete(): Boolean {
        return binding.ageEditText.text.toString() != "" && binding.childrenEditText.text.toString() != "" && binding.professionEditText.text.toString() != ""
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
     * Calls [initSpinner] for every of the four [Spinner]s with its corresponding string-array
     * and starting selection.
     * */
    private fun initAllSpinners() {
        initSpinner(
            binding.genderSpinner,
            R.array.gender_array,
            userProfile.gender
        )
        initSpinner(
            binding.martialStatusSpinner,
            R.array.marital_Status_array,
            userProfile.martialStatus
        )
        initSpinner(
            binding.educationSpinner,
            R.array.education_array,
            userProfile.education
        )
        initSpinner(
            binding.professionTypeSpinner,
            R.array.professionType_array,
            userProfile.professionType
        )
    }

    /**
     * Fills a [Spinner] with data from a string-array given in [res].
     * Sets the current selection position of the Spinner to given [selection].
     * Adds an [AdapterView.OnItemSelectedListener] to the Spinner which changes the [userProfile]
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
                        userProfile.gender = position
                        Timber.i("Gender was changed to '${userProfile.genderString}'!")
                    }
                    binding.martialStatusSpinner -> {
                        userProfile.martialStatus = position
                        Timber.i("Martial status was changed to '${userProfile.martialStatusString}'!")
                    }
                    binding.educationSpinner -> {
                        userProfile.education = position
                        Timber.i("Education was changed to '${userProfile.educationString}'!")
                    }
                    binding.professionTypeSpinner -> {
                        userProfile.professionType = position
                        Timber.i("ProfessionType was changed to '${userProfile.professionTypeString}'")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing (needed override fun)
            }
        }
    }

    /**
     * Calls [initEditText] for every of the three [EditText]s.
     * */
    private fun initAllEditTexts() {
        initEditText(binding.ageEditText)
        initEditText(binding.childrenEditText)
        initEditText(binding.professionEditText)
    }

    /**
     * Sets an Observer to [SharedViewModel.localDialogDismiss]. Navigates to [QuestioningFragment]
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

    /**
     * Changes the given [editText]s hint to the given [hint].
     * Adds a textChangeListener to the editText which shows an error message if the user writes
     * an invalid text
     * @param editText the [EditText] that shall be initialized
     * @param hint the hint that should be shown in the [editText]
     * */
    private fun initEditText(editText: EditText) {
        val ageEditText = binding.ageEditText
        val childrenEditText = binding.childrenEditText
        val professionEditText = binding.professionEditText

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
                /* checks if the changed text is valid and sets an error message if not */
                val errorMessage = when (editText) {
                    ageEditText -> viewModel.getAgeErrorMessage(s.toString())
                    childrenEditText -> viewModel.getChildrenErrorMessage(s.toString())
                    professionEditText -> viewModel.getProfessionErrorMessage(s.toString())
                    else -> ""
                }
                if (errorMessage != "") {
                    editText.error = errorMessage
                } else if (s.toString() != "") {
                    /* the personalData gets updated if the changed text is not invalid nor empty */
                    when (editText) {
                        ageEditText -> userProfile.dateOfBirth = Integer.parseInt(s.toString())
                        childrenEditText -> userProfile.children = Integer.parseInt(s.toString())
                        professionEditText -> userProfile.profession = s.toString()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //not needed
            }
        })
    }
}