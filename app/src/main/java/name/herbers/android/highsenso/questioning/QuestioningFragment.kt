package name.herbers.android.highsenso.questioning

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
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

        //init the DataBinding and QuestioningViewModel
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_questioning,
            container,
            false
        )
        val sharedViewModel: SharedViewModel by activityViewModels()
        val databaseHandler = sharedViewModel.databaseHandler
        val viewModelFactory =
            QuestioningViewModelFactory(databaseHandler)
        viewModel = ViewModelProvider(this, viewModelFactory).get(QuestioningViewModel::class.java)
        binding.questioningViewModel = viewModel
        binding.lifecycleOwner = this

        /**
         * Observed isFirstQuestion is true if the shown question is the first question.
         * If isFirstQuestion is false after backButton is clicked, the Fragment changes
         * to [StartFragment]
         * */
        viewModel.navBackToStartFrag.observe(viewLifecycleOwner, { isFirstQuestion ->
            if (isFirstQuestion) {
                findNavController(this)
                    .navigate(R.id.action_questioning_destination_to_start_destination)
            }
        })

        /**
         * Observed isFinished becomes true after the nextButton is clicked while the last question
         * was shown.
         * If isFinished is true, Fragment changes to [ResultFragment]
         * */
        viewModel.isFinished.observe(viewLifecycleOwner, { isFinished ->
            if (isFinished) {
                findNavController(this)
                    .navigate(R.id.action_questioningFragment_to_resultFragment)
            }
        })

        /**
         * Observed changeSeekBar becomes true if the current [Question] is changed.
         * The [SeekBar] is set to the saved rating for the currently shown [Question] (in default
         * position if no rating is saved at this moment)
         * */
        viewModel.changeSeekBar.observe(viewLifecycleOwner, { change ->
            if (change) {
                binding.seekBar.setProgress(viewModel.getRatingToSetProgress(), false)
            }
        })

        /**
         * If questionCount changes, the ActionBars title will be changed
         * */
        viewModel.questionCount.observe(viewLifecycleOwner, { count ->
            (activity as AppCompatActivity).supportActionBar?.title =
                resources.getString(R.string.questioning_actionBar_title) + " $count"
        })

        /**
         * Listener for nextButton, which progresses to next [Question] or
         * to [ResultFragment]
         * */
        binding.nextButton.setOnClickListener {
            Timber.i("nextButton was clicked!")
            viewModel.handleNextButtonClick(binding.seekBar.progress)
        }

        /**
         * Listener for backButton, which navigates back to the previous [Question] or
         * to [StartFragment]
         * */
        binding.backButton.setOnClickListener {
            Timber.i("backButton was clicked!")
            viewModel.handleBackButtonClick(binding.seekBar.progress)
        }

        /**
         * If [SeekBar] is progressed the current value is highlighted (bold and bigger size).
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

            //on change the progressed value is set to 'bold' and a bigger size
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
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //this override method is not needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //this override method is not needed
            }
        })
        Timber.i("QuestionFragment created!")
        return binding.root
    }
}

