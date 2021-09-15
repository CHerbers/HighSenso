package name.herbers.android.highsenso.questioning

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class QuestioningFragment: Fragment() {

    private lateinit var viewModel: QuestioningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(QuestioningViewModel::class.java)
    }

    //TODO: Calculate what question should be shown and save question order

    //TODO: Show question, rating bar and navigation buttons (previous, next)

    //TODO: Save rating if next is pressed

}