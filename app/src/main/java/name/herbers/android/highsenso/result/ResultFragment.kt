package name.herbers.android.highsenso.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.databinding.FragmentResultBinding
import name.herbers.android.highsenso.dialogs.SendDialogFragment
import timber.log.Timber

/** [Fragment] that shows the results depending on the users question ratings.
 * Further advises on how to interpret the result and what can be done next are given.
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
        val viewModelFactory = ResultViewModelFactory(databaseHandler, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ResultViewModel::class.java)
        binding.resultViewModel = viewModel
        binding.lifecycleOwner = this

        //set title
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.app_name)

        /* Listener for sendResultButton */
        binding.sendResultButton.setOnClickListener { view ->
            Timber.i("sendResetButton clicked!")
            SendDialogFragment(viewModel).show(childFragmentManager, SendDialogFragment.TAG)
            //TODO new fragment or overlay and asking for gender and age (and country?)

            //TODO hide button/ make is not clickable to prevent multiple sending
        }

        /* Listener for backToStartButton. Navigation back to StartFragment */
        binding.backToStartButton.setOnClickListener { view ->
            Timber.i("backToStartButton clicked!")
            Navigation.findNavController(view)
                .navigate(R.id.action_resultFragment_to_startFragment)
        }

        Timber.i("ResultFragment created!")
        return binding.root
    }
}