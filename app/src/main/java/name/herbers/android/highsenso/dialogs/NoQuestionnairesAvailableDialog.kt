package name.herbers.android.highsenso.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.data.Questionnaire
import timber.log.Timber

/**
 * This [DialogFragment] is used to tell the user that no [Questionnaire]s are available.
 * This should only happen if loading the questionnaires from the webserver failed after successful
 * login and if there are additionally no old questionnaires saved on the device.
 *
 *@project HighSenso
 *@author Herbers
 */
class NoQuestionnairesAvailableDialog(): DialogFragment() {

    companion object {
        const val TAG = "NoQuestionnairesAvailableDialog"
        const val LOGGING_MESSAGE = "EndQuestioningDialog was answered!"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.no_questionnaires_available_dialog_title)
            builder.setMessage(R.string.no_questionnaires_available_dialog_message)
            builder.setPositiveButton(R.string.okay_button) { _, _ ->
                Timber.i(LOGGING_MESSAGE)
                dismiss()
            }
            builder.create()
        }
    }
}
