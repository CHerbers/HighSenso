package name.herbers.android.highsenso.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.FragmentAboutBinding
import name.herbers.android.highsenso.start.StartFragment
import timber.log.Timber

/**
 * The [AboutFragment] holds information about this app, its purpose and the author.
 *
 * This Fragment is accessible via the overflow menu in the StartFragment.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding
    private lateinit var viewModel: AboutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        viewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
        binding.aboutViewModel = viewModel
        binding.lifecycleOwner = this

        //set title
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title = resources.getString(R.string.about_actionBar_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        Timber.i("AboutFragment created!")
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
                    .navigate(R.id.action_about_destination_to_start_destination)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}