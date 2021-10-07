package name.herbers.android.highsenso.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.start.SharedDatabaseViewModel
import timber.log.Timber

class ResetDialogFragment() : DialogFragment() {
    private val sharedDatabaseViewModel: SharedDatabaseViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.reset_dialog_title)
            builder.setMessage(R.string.reset_dialog_message)
            builder.setPositiveButton(R.string.positive_button) { _, _ ->
                sharedDatabaseViewModel.handleResetQuestions()
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
        const val TAG = "ResetDialog"
    }
}