package name.herbers.android.highsenso.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.ResultFragmentBinding

/** Fragment that shows the results depending on the users question ratings.
 * Further advises on how to interpret the result and what can be done next are given.
 * */
class ResultFragment: Fragment() {

    private lateinit var binding: ResultFragmentBinding
    private lateinit var viewModel: ResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.questioning_fragment, container, false)

        viewModel = ViewModelProvider(this).get(ResultViewModel::class.java)

        binding.resultViewModel = viewModel
        binding.lifecycleOwner = this

        return super.onCreateView(inflater, container, savedInstanceState)
    }

//TODO: Show disclaimer that this is not a diagnosis

    //TODO: Calculate and show result

    //TODO: Possibility to save result? In order to redo the test or let others do the test

}