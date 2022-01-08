package name.herbers.android.highsenso.questioning

import android.R
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import name.herbers.android.highsenso.data.Answer
import name.herbers.android.highsenso.data.Question

/**
 * This holds [BindingAdapter]s used by elements of the [BaselineQuestioningFragment]s
 * [QuestionAdapter].
 *
 *@project HighSenso
 *@author Herbers
 */

/**
 * This is the [BindingAdapter] for all [TextView]s that represent question titles.
 *
 * @param question the [Question] that holds the shown question title
 * */
@BindingAdapter("questionTitle")
fun TextView.setQuestionTitle(question: Question?) {
    question?.let {
        text = question.translations[0].question
    }
}

/**
 * This is the [BindingAdapter] for all [Spinner]s
 *
 * @param question the [Question] which [Answer]s will fill the [Spinner]
 * */
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
            arrayAdapter.setDropDownViewResource(name.herbers.android.highsenso.R.layout.multiline_spinner_dropdown_item)
            adapter = arrayAdapter
        }
        setSelection(0)
    }
}