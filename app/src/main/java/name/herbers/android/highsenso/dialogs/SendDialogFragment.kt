package name.herbers.android.highsenso.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.DialogSendBinding
import name.herbers.android.highsenso.result.ResultViewModel
import timber.log.Timber

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

            builder.setTitle(R.string.reset_dialog_title)
            builder.setPositiveButton(R.string.accept_button) { _, _ -> }
            builder.setNegativeButton(R.string.cancel_button) { _, _ ->
                //nothing to do if negative button is clicked
                Timber.i("Reset Dialog was answered negative!")
                dismiss()
            }

            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
                    val checkedButtonId = binding.genderRadioGroup.checkedRadioButtonId
                    Timber.i(checkedButtonId.toString())
                    val age = binding.ageEditText.text.toString()

                    if (checkedButtonId != -1
                        && resultViewModel.checkSendResultInput(age)
                    ) {
                        val fb = binding.genderFemaleRadioButton
                        val mb = binding.genderMaleRadioButton
                        val nbb = binding.genderNonBinaryRadioButton
                        //check with gender is selected
                        val gender: String = when (checkedButtonId) {
                            fb.id -> fb.text.toString()
                            mb.id -> mb.text.toString()
                            nbb.id -> nbb.text.toString()
                            else -> "invalid input"
                        }
                        Timber.i("Reset Dialog was answered positive! With age: $age and gender: $gender!")
                        resultViewModel.handleSendResult(age, gender)
                        Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        //TODO user feedback
                        Timber.i("Reset Dialog was answered with invalid input!")
                    }
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