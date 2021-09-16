package name.herbers.android.highsenso.start

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.StartFragmentBinding
import name.herbers.android.highsenso.menu.AboutFragment
import timber.log.Timber

/**The [StartFragment] is the starting [Fragment] of the HighSenso app.
 * This Fragment introduces the user to this App and provides useful information on how to use this
 * App and what this App can do and can't do
 * From this Fragment the user can navigate via the menu to the [AboutFragment] and can start the
 * questioning.
 * */
class StartFragment : Fragment() {

    private lateinit var viewModel: StartViewModel
    private lateinit var binding: StartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Timber.i("StartFragment created!")

        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.start_fragment, container, false)
        viewModel = ViewModelProvider(this).get(StartViewModel::class.java)
        binding.startViewModel = viewModel
        binding.lifecycleOwner = this

        //activate menu in this fragment
        setHasOptionsMenu(true)

        //listener for startButton which starts the questioning
        binding.startButton.setOnClickListener { view: View ->
            Timber.i("startButton was clicked!")
            Navigation.findNavController(view).navigate(R.id.action_startFragment_to_questioningFragment)
        }

        // Inflate the layout for this fragment
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
        Timber.i("menu item \"${item.title}\" is selected")
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}