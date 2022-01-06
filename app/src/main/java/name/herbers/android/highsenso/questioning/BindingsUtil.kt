package name.herbers.android.highsenso.questioning

import android.R
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import name.herbers.android.highsenso.data.Question

/**
 *
 *@project HighSenso
 *@author Herbers
 */
@BindingAdapter("questionTitle")
fun TextView.setQuestionTitle(question: Question?) {
    question?.let {
        text = question.translations[0].question
    }
}

@BindingAdapter("spinnerContent")
fun Spinner.setUpContent(question: Question?) {
    question?.let {
        ArrayAdapter(
            context,
            R.layout.simple_spinner_item,
            try {
                question.translations[0].answers as List<String>
            } catch (e: TypeCastException) {
                listOf()
            }
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            adapter = arrayAdapter
        }
        setSelection(0)
    }
}