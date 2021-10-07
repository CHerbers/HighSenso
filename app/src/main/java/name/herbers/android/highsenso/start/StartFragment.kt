package name.herbers.android.highsenso.start

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.StartFragmentBinding
import name.herbers.android.highsenso.dialogs.ResetDialogFragment
import name.herbers.android.highsenso.menu.AboutFragment
import timber.log.Timber

/**The [StartFragment] is the starting [Fragment] of the HighSenso app.
 * This Fragment introduces the user to this App and provides useful information on how to use this
 * App and what this App can and can't do.
 * <p>
 * From this Fragment the user can navigate via the menu to the [AboutFragment] and can start the
 * questioning.
 * */
class StartFragment : Fragment() {

    private val sharedDatabaseViewModel: SharedDatabaseViewModel by activityViewModels()
    private lateinit var binding: StartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("StartFragment created!")

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.start_fragment, container, false)
        binding.startViewModel = sharedDatabaseViewModel
        binding.lifecycleOwner = this

        //set title
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.app_name)

        //activate menu in this Fragment
        setHasOptionsMenu(true)

        /* Listener for startButton which starts the questioning */
        binding.startButton.setOnClickListener { view: View ->
            Timber.i("startButton was clicked!")
            Navigation.findNavController(view)
                .navigate(R.id.action_startFragment_to_questioningFragment)
        }

        // inflate the layout for this Fragment
        return binding.root
    }

    //inflate overflow_menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Timber.i("Overflow menu created!")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    //handle navigation on item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.i("Menu Item \"${item.title}\" was selected!")
        return when (item.itemId) {
            //reset question ratings
            R.id.reset_rating_destination -> handleResetQuestions() //TODO maybe show some pop up ('success' or something)
            //navigate to AboutFragment
            R.id.about_destination -> NavigationUI.onNavDestinationSelected(
                item,
                requireView().findNavController()
            )
            //default
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleResetQuestions(): Boolean {
        ResetDialogFragment().show(childFragmentManager, ResetDialogFragment.TAG)
        return true
    }
}