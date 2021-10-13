package name.herbers.android.highsenso.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.DialogSendBinding
import name.herbers.android.highsenso.result.ResultViewModel
import timber.log.Timber

/**
 * This Dialog asks the user for his age and gender in order to anonymously send this information
 * together with the test result.
 * @param resultViewModel the corresponding [ViewModel] which holds the logic for validating input
 * */
class SendDialogFragment(private val resultViewModel: ResultViewModel) : DialogFragment() {
    private lateinit var binding: DialogSendBinding

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            //init binding and builder and inflate
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_send, null, false
            )
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)

            //set title and Buttons
            builder.setTitle(R.string.send_dialog_title)
            builder.setPositiveButton(R.string.accept_button) { _, _ -> }
            builder.setNegativeButton(R.string.cancel_button) { _, _ ->
                //nothing to do if negative button is clicked
                Timber.i("Reset Dialog was answered negative!")
                dismiss()
            }

            /* EditText shows realtime error if input is invalid */
            val editText = binding.ageEditText
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //not needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null) return
                    val errorMessage = resultViewModel.getErrorMessage(s.toString())
                    if (errorMessage != "") {
                        editText.error = errorMessage
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    //not needed
                }
            })

            /* Create ClickListener for positive Button which calls methods to check input and
            * initiates data sending if input is correct or does nothing if input is invalid
            *  */
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
                    val checkedButtonId = binding.genderRadioGroup.checkedRadioButtonId
                    val ageString = editText.text.toString()
                    if (ageString != "") {
                        val age = ageString.toInt()
                        if (checkedButtonId != -1
                            && resultViewModel.checkSendResultInput(age)
                        ) {
                            val fb = binding.genderFemaleRadioButton
                            val mb = binding.genderMaleRadioButton
                            val nbb = binding.genderNonBinaryRadioButton

                            //check witch gender is selected
                            val gender: String = when (checkedButtonId) {
                                fb.id -> fb.text.toString()
                                mb.id -> mb.text.toString()
                                nbb.id -> nbb.text.toString()
                                else -> "invalid input"
                            }
                            Timber.i("Reset Dialog was answered positive! With age: $age and gender: $gender!")

                            //actually send the results
                            resultViewModel.handleSendResult(age, gender)

                            //toast
                            Toast.makeText(
                                context,
                                getString(R.string.send_dialog_toast_message),
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                            return@setOnClickListener
                        }
                    }
                    Timber.i("Reset Dialog was answered with invalid input!")
                }
            }
            //returning dialog
            dialog
        }
    }

    companion object {
        const val TAG = "SendDialog"
    }
}