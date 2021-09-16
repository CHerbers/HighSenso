package name.herbers.android.highsenso.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import timber.log.Timber

class AboutFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("AboutFragment created!")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

}