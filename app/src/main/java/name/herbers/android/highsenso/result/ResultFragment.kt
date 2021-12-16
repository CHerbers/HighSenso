package name.herbers.android.highsenso.result

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.databinding.FragmentResultBinding
import name.herbers.android.highsenso.questioning.QuestioningViewModel
import timber.log.Timber

/** [Fragment] that shows the results depending on the users question ratings.
 * Further advises on how to interpret the result and what can be done next are given.
 *
 * Every non-UI task is sourced out to the [ResultViewModel].
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private lateinit var viewModel: ResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)
        val application = requireNotNull(this.activity).application
        val sharedViewModel: SharedViewModel by activityViewModels()
        val databaseHandler = sharedViewModel.databaseHandler
        val viewModelFactory = ResultViewModelFactory(sharedViewModel, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ResultViewModel::class.java)
        binding.resultViewModel = viewModel
        binding.lifecycleOwner = this

        //set action bar (title and back button)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title =
                resources.getString(R.string.result_actionBar_title)
            actionBar.setDisplayHomeAsUpEnabled(false)
        }
        initObservers()
        binding.linkListTextView.movementMethod = LinkMovementMethod.getInstance()

//        binding.resultHspInfoTextView.visibility = View.GONE
//        setHasOptionsMenu(true)

        /* Listener for sendResultButton */
//        binding.sendResultButton.setOnClickListener {
//            Timber.i("sendResetButton clicked!")
////            SendDialogFragment(viewModel).show(childFragmentManager, SendDialogFragment.TAG)
//        }

        /* Listener for backToStartButton. Navigation back to StartFragment */
        binding.backToStartButton.setOnClickListener { view ->
            Timber.i("backToStartButton clicked!")
            Navigation.findNavController(view)
                .navigate(R.id.action_resultFragment_to_startFragment)
        }

        Timber.i("ResultFragment created!")
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
                val sharedViewModel: SharedViewModel by activityViewModels()
                sharedViewModel.backFromResult = true
                findNavController(this)
                    .navigate(R.id.action_result_destination_to_questioning_destination)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This function is initiating every needed [Observer] of [LiveData] from the [ResultViewModel].
     * */
    private fun initObservers() {
        viewModel.disableHelpTextViews.observe(viewLifecycleOwner, { disable ->
            if (disable) {
                Timber.i("User negative! TextViews visibility set to 'gone'!")
//                binding.resultConditionalTextView.visibility = View.GONE
//                binding.resultPersonalTextView.visibility = View.GONE
            }
        })
        showMessageObserver(viewModel.hasEnthusiasm, binding.resultEnthusiasmTextView)
        showMessageObserver(
            viewModel.isEmotionalVulnerable,
            binding.resultEmotionalVulnerabilityTextView
        )
        showMessageObserver(viewModel.hasSelfDoubt, binding.resultSelfDoubtTextView)
        showMessageObserver(viewModel.hasWorldPain, binding.resultWorldPainTextView)
        showMessageObserver(viewModel.hasLingeringEmotions, binding.resultLingeringEmotionsTextView)
        showMessageObserver(viewModel.isSuffering, binding.sufferingMessageTextView)
        showMessageObserver(viewModel.hasWorkplaceProblem, binding.resultWorkplaceTextView)
    }


    /**
     * Generic function to add an [Observer] to a given [LiveData] of [Boolean] type, that makes
     * a given [TextView] visible if true.
     *
     * @param liveData is the [LiveData] the [Observer] is added to
     * @param textView is the [TextView] that is set to visible if observed LiveData is set to true
     * */
    private fun showMessageObserver(liveData: LiveData<Boolean>, textView: TextView) {
        liveData.observe(viewLifecycleOwner, { isTrue ->
            if (isTrue) {
                textView.visibility = View.VISIBLE
            }
        })
    }

}