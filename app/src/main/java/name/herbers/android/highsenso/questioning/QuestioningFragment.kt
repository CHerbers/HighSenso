package name.herbers.android.highsenso.questioning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

        binding = DataBindingUtil.inflate(inflater, R.layout.questioning_fragment, container, false)

        viewModel = ViewModelProvider(this).get(QuestioningViewModel::class.java)

        binding.questioningViewModel = viewModel
        binding.lifecycleOwner = this

        Timber.i("QuestionFragment created!")
        return binding.root
    }


    //TODO: Calculate what question should be shown and save question order

    //TODO: Show question, rating bar and navigation buttons (previous, next)

    //TODO: Save rating if next is pressed

}

