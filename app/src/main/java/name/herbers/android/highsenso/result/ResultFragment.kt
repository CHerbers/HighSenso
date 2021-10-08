package name.herbers.android.highsenso.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.ResultFragmentBinding
import name.herbers.android.highsenso.start.SharedViewModel
import timber.log.Timber

/** [Fragment] that shows the results depending on the users question ratings.
 * Further advises on how to interpret the result and what can be done next are given.
 * */
class ResultFragment : Fragment() {

    private lateinit var binding: ResultFragmentBinding
    private lateinit var viewModel: ResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.result_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val sharedViewModel: SharedViewModel by activityViewModels()
        val databaseHandler = sharedViewModel.databaseHandler
        val viewModelFactory = ResultViewModelFactory(databaseHandler, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ResultViewModel::class.java)
        binding.resultViewModel = viewModel
        binding.lifecycleOwner = this

        binding.sendResultButton.setOnClickListener { view ->
            Timber.i("sendResetButton clicked!")
            Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show()
            //TODO new fragment or overlay and asking for gender and age (and country?)
        }


        Timber.i("ResultFragment created!")
        return binding.root
    }
}