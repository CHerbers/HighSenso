package name.herbers.android.highsenso.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class LogoutDialog(val sharedViewModel: SharedViewModel) : DialogFragment() {

    companion object {
        const val TAG = "LogoutDialog"
        const val LOGGING_MESSAGE = "Logout Dialog was answered "
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.i("LogoutDialog created!")
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.logout_dialog_title)
            builder.setMessage(R.string.logout_dialog_message)
            builder.setPositiveButton(R.string.positive_button) { _, _ ->
                Timber.i(LOGGING_MESSAGE + "positive!")
                Toast.makeText(
                    context,
                    R.string.logout_dialog_toast_message,
                    Toast.LENGTH_SHORT
                ).show()
                sharedViewModel.handleLogoutButtonClick()
                dismiss()
            }
            builder.setNegativeButton(R.string.negative_button) { _, _ ->
                Timber.i(LOGGING_MESSAGE + "negative!")
                dismiss()
            }
            builder.create()
        }
    }

    override fun onDestroy() {
        Timber.i("LogoutDialog destroyed!")
        super.onDestroy()
    }
}