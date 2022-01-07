package name.herbers.android.highsenso.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import timber.log.Timber

/**
 * This [DialogFragment] is used to tell the user that a confirmation mail was sent after a
 * reset password request.
 *
 *@project HighSenso
 *@author Herbers
 */
class ConfirmationMailSentDialog(): DialogFragment() {

    companion object{
        const val TAG = "ConfirmationMailSentDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.confirmation_mail_sent_dialog_title)
            builder.setMessage(R.string.confirmation_mail_sent_dialog_message)
            builder.setPositiveButton(R.string.confirmation_mail_sent_dialog_button) { _, _ ->
                Timber.i("Confirmation mail sent Dialog was answered!")
                dismiss()
            }
            builder.create()
        }
    }
}