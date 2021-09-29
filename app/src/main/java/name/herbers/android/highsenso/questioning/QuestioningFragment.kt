package name.herbers.android.highsenso.questioning

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.database.QuestionDatabase
import name.herbers.android.highsenso.databinding.QuestioningFragmentBinding
import name.herbers.android.highsenso.result.ResultFragment
import name.herbers.android.highsenso.result.ResultViewModel
import name.herbers.android.highsenso.start.StartFragment
import timber.log.Timber

/**In the [QuestioningFragment] the user is asked to rate questions. The result if the user is an
 * HSP will be calculated later on in the [ResultViewModel] depending on the users rating.
 * After rating a question there will be asked another question until the questioning is completed.
 * */
class QuestioningFragment : Fragment() {

    private lateinit var binding: QuestioningFragmentBinding
    private lateinit var viewModel: QuestioningViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.questioning_fragment,
            container,
            false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = QuestionDatabase.getInstance(application).questionDatabaseDao
        val viewModelFactory = QuestioningViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(QuestioningViewModel::class.java)
        binding.questioningViewModel = viewModel
        binding.lifecycleOwner = this

        /**
         * Observed isFirstQuestion is true if the shown question is the first question.
         * If isFirstQuestion is false after backButton is clicked, the Fragment changes
         * to [StartFragment]
         * */
        viewModel.isFirstQuestion.observe(viewLifecycleOwner, Observer { isFirstQuestion ->
            if (!isFirstQuestion) {
                findNavController(this)
                    .navigate(R.id.action_questioning_destination_to_start_destination)
            }
        })

        /**
         * Observed isFinished becomes true after the nextButton is clicked while the last question
         * was shown.
         * If isFinished is true, Fragment changes to [ResultFragment]
         * */
        viewModel.isFinished.observe(viewLifecycleOwner, Observer { isFinished ->
            if (isFinished) {
                findNavController(this)
                    .navigate(R.id.action_questioningFragment_to_resultFragment)
            }
        })

        // listener for nextButton, which progresses to next question or to result
        binding.nextButton.setOnClickListener {
            Timber.i("nextButton was clicked!")
            viewModel.handleNextButtonClick()
        }

        // listener for backButton, which navigates back to the previous question or to the title fragment
        binding.backButton.setOnClickListener {
            Timber.i("backButton was clicked!")
            viewModel.handleBackButtonClick()
        }

        /**
         * If SeekBar is progressed the current value is highlighted (bold and bigger size).
         * Every other value is set to standard size and default typeface
         */
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            //list of the TextViews that represent the SeekBar values
            val progressTextViews: MutableList<TextView> = mutableListOf(
                binding.sbLegend0,
                binding.sbLegend1,
                binding.sbLegend2,
                binding.sbLegend3,
                binding.sbLegend4
            )

            //on change the progressed value is set to "bold" and a higher size
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Timber.i("SeekBar changed to $progress!")

                progressTextViews.forEach { textView: TextView ->
                    textView.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        resources.getDimension(R.dimen.sb_legend_standard_textSize)
                    )
                    textView.typeface = Typeface.DEFAULT
                }

                val progressedTextView = progressTextViews[progress]
                progressedTextView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.sb_legend_selected_textSize)
                )
                progressedTextView.typeface = Typeface.DEFAULT_BOLD

                viewModel.handleSeekBarChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        Timber.i("QuestionFragment created!")
        return binding.root
    }
    //TODO: Show question, rating bar
}

