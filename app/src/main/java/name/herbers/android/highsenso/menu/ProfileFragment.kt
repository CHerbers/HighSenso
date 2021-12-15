package name.herbers.android.highsenso.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.FragmentProfileBinding
import name.herbers.android.highsenso.start.StartFragment
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ProfileViewModel()
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = this

        //set title
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title = resources.getString(R.string.profile_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        Timber.i("ProfileFragment created!")
        return binding.root
    }

    /**
     * Override fun to define an action when actionBar back button is clicked.
     * If the button is clicked, the app navigated back to the [StartFragment]
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Timber.i("actionBar back button clicked!")
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_profileFragment_to_start_destination)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}