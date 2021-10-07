package name.herbers.android.highsenso.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.R
import timber.log.Timber

class ResetDialogFragment() : DialogFragment() {
    //    private lateinit var navBackStackEntry:
    private lateinit var sharedDialogViewModel: SharedDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sharedDialogViewModel =
            ViewModelProvider(requireActivity()).get(SharedDialogViewModel::class.java)

        Timber.i("ViewModel: $sharedDialogViewModel")

        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.reset_dialog_title)
            builder.setMessage(R.string.reset_dialog_message)
            builder.setPositiveButton(R.string.positive_button) { _, _ ->
                sharedDialogViewModel.answeredPositive()
                Timber.i("Reset Dialog was answered positive!")
                dismiss()
            }
            builder.setNegativeButton(R.string.negative_button) { _, _ ->
                //nothing to do if negative button is clicked
                Timber.i("Reset Dialog was answered negative!")
                dismiss()
            }
            builder.create()
        }
    }

    companion object {
//        fun newInstance(listener: DialogListener) {
//            ResetDialogFragment().apply {
//                this.listener = listener
//            }
//        }

        const val TAG = "ResetDialog"
    }
}