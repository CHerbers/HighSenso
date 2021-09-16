package name.herbers.android.highsenso.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.AboutFragmentBinding
import timber.log.Timber

class AboutFragment: Fragment() {

    private lateinit var binding: AboutFragmentBinding
    private lateinit var viewModel: AboutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.about_fragment, container, false)
        viewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
        binding.aboutViewModel = viewModel
        binding.lifecycleOwner = this

        Timber.i("AboutFragment created!")
        return binding.root
    }

}