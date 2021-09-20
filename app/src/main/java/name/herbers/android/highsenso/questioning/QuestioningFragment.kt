package name.herbers.android.highsenso.questioning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.QuestioningFragmentBinding
import name.herbers.android.highsenso.result.ResultViewModel
import timber.log.Timber

/**In the [QuestioningFragment] the user is asked to rate questions. The result if the user is an
 * HSP will be calculated later on in the [ResultViewModel] depending on the users rating.
 * After rating a question there will be asked another question until the questioning is completed.
 * */
class QuestioningFragment: Fragment() {

    private lateinit var binding: QuestioningFragmentBinding
    private lateinit var viewModel: QuestioningViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.questioning_fragment, container, false)
        viewModel = ViewModelProvider(this).get(QuestioningViewModel::class.java)
        binding.questioningViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.isFirstQuestion.observe(viewLifecycleOwner, Observer { isFirstQuestion ->
            if (!isFirstQuestion){
                findNavController(this).navigate(R.id.action_questioning_destination_to_start_destination)
            }
        })

        // listener for nextButton, which progresses to next question or to result
        binding.nextButton.setOnClickListener {view: View ->
            Timber.i("nextButton was clicked!")
            Navigation.findNavController(view).navigate(R.id.action_questioningFragment_to_resultFragment)
        }

        binding.backButton.setOnClickListener { view: View ->
            Timber.i("backButton was clicked!")

            viewModel.handleBackButtonClick()
        }

        Timber.i("QuestionFragment created!")
        return binding.root
    }
    //TODO: Show question, rating bar
}

