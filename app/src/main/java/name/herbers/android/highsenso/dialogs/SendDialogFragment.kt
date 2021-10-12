package name.herbers.android.highsenso.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.result.ResultViewModel
import timber.log.Timber

class SendDialogFragment(private val resultViewModel: ResultViewModel) : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)

            val genderListViewList = resources.getStringArray(R.array.gender)
            val inflater = requireActivity().layoutInflater
//            val listView: ListView = activity?.findViewById(R.id.gender_listView)!!
//            val adapter = ArrayAdapter(this, R.layout.dialog_send, genderListViewList)
            builder.setView(inflater.inflate(R.layout.dialog_send, null))
            builder.setTitle(R.string.reset_dialog_title)
//            builder.setMessage(R.string.reset_dialog_message)


            builder.setPositiveButton(R.string.accept_button) { _, _ ->
                resultViewModel.handleSendResult()
                Timber.i("Reset Dialog was answered positive!")
                dismiss()
            }
            builder.setNegativeButton(R.string.cancel_button) { _, _ ->
                //nothing to do if negative button is clicked
                Timber.i("Reset Dialog was answered negative!")
                dismiss()
            }
            builder.create()
        }
    }

    companion object {
        const val TAG = "SendDialog"
    }
}