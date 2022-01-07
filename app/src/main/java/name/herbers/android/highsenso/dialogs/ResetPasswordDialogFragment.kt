package name.herbers.android.highsenso.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.databinding.DialogResetPasswordBinding
import name.herbers.android.highsenso.login.LoginViewModel
import timber.log.Timber

/**
 * This [DialogFragment] asks for a mail as input to send a reset password request to the webserver.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class ResetPasswordDialogFragment(
    private val sharedViewModel: SharedViewModel,
    private val loginViewModel: LoginViewModel
) : DialogFragment() {
    private lateinit var binding: DialogResetPasswordBinding
    private lateinit var dialog: AlertDialog

    companion object {
        const val TAG = "ResetPasswordDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.i("$TAG created!")
        return activity.let {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_reset_password,
                null,
                false
            )
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            builder.setTitle(R.string.reset_dialog_title)
            builder.setPositiveButton(R.string.positive_button) { _, _ ->
                val mailEditText = binding.resetPasswordDialogEditTextLayout.editText
                if (mailEditText != null && mailEditText.error == null && mailEditText.text.isNotEmpty()) {
                    sharedViewModel.handleResetPassword(mailEditText.text.toString())
                    Timber.i("ResetPasswordDialog was answered positive! Mail: ${mailEditText.text}")
                    dismiss()
                }
            }

            builder.setNegativeButton(R.string.cancel_button) { _, _ ->
                Timber.i("ResetPasswordDialog was answered negative!")
                dismiss()
            }

            addMailEditTextListener()

            dialog = builder.create()
            dialog
        }
    }

    /**
     * This function adds a [TextWatcher] to the mail [EditText] to check validity of the given
     * input.
     * */
    private fun addMailEditTextListener() {
        binding.resetPasswordDialogEditTextLayout.editText?.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null) return
                val errorMessage = loginViewModel.getMailErrorMessage(s.toString())
                if (errorMessage != "") {
                    binding.resetPasswordDialogMailEditText.error = errorMessage
                } else {
                    binding.resetPasswordDialogMailEditText.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //not needed
            }
        })
    }

    override fun onDestroy() {
        Timber.i("$TAG destroyed!")
        super.onDestroy()
    }
}