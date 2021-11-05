package name.herbers.android.highsenso.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.FragmentPrivacyBinding
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PrivacyFragment: Fragment() {

    private lateinit var binding: FragmentPrivacyBinding
    private lateinit var viewModel: PrivacyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy, container, false)
        viewModel = ViewModelProvider(this).get(PrivacyViewModel::class.java)
        binding.privacyViewModel = viewModel
        binding.lifecycleOwner = this

        //set title
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.privacy_actionBar_title)

        Timber.i("AboutFragment created!")
        return binding.root
    }

}