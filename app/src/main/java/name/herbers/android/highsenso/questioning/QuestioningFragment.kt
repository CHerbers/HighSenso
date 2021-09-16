package name.herbers.android.highsenso.questioning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.QuestioningFragmentBinding
import timber.log.Timber

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

        // listener for nextButton, which progresses to next question or to result
        binding.nextButton.setOnClickListener {view: View ->
            Timber.i("nextButton was clicked!")
            Navigation.findNavController(view).navigate(R.id.action_questioningFragment_to_resultFragment)
        }

        Timber.i("QuestionFragment created!")
        return binding.root
    }


    //TODO: Calculate what question should be shown and save question order

    //TODO: Show question, rating bar and navigation buttons (previous, next)

    //TODO: Save rating if next is pressed

}

