package name.herbers.android.highsenso.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.StartFragmentBinding
import timber.log.Timber


class StartFragment : Fragment() {

    private lateinit var viewModel: StartViewModel
    private lateinit var binding: StartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Timber.i("StartFragment created")

        binding = DataBindingUtil.inflate(inflater, R.layout.start_fragment, container, false)

        viewModel = ViewModelProvider(this).get(StartViewModel::class.java)

        binding.startViewModel = viewModel
        binding.lifecycleOwner = this

        binding.startButton.setOnClickListener {
            Timber.i("Listen!")
            println("Bitte!")
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}