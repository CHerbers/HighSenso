package name.herbers.android.highsenso.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

/** Fragment that shows the results depending on the users question ratings.
 * Further advises on how to interpret the result and what can be done next are given.
 * */
class ResultFragment: Fragment() {

    private lateinit var viewModel: ResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ResultViewModel::class.java)

    }

    //TODO: Show disclaimer that this is not a diagnosis

    //TODO: Calculate and show result

    //TODO: Possibility to save result? In order to redo the test or let others do the test

}