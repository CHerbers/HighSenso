package name.herbers.android.highsenso.questioning

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.data.Answer
import name.herbers.android.highsenso.data.Question
import name.herbers.android.highsenso.databinding.ItemViewDatePickerBinding
import name.herbers.android.highsenso.databinding.ItemViewEditTextNumberBinding
import name.herbers.android.highsenso.databinding.ItemViewEditTextStringBinding
import name.herbers.android.highsenso.databinding.ItemViewSpinnerBinding
import java.util.*

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class QuestionAdapter(
    val sharedViewModel: SharedViewModel,
    val viewModel: BaselineQuestioningViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_SPINNER_VIEW = 0
        const val TYPE_EDIT_STRING_VIEW = 1
        const val TYPE_EDIT_DATE_VIEW = 2
        const val TYPE_EDIT_NUMBER_VIEW = 3
    }

    var data = listOf<Question>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        val givenAnswers = getGivenAnswers()

        when (holder) {
            is SpinnerViewHolder -> holder.bind(item, viewModel)
            is EditTextStringViewHolder -> holder.bind(item, viewModel, givenAnswers)
            is DatePickerViewHolder -> holder.bind(item, givenAnswers)
            is EditTextNumberViewHolder -> holder.bind(item, viewModel, givenAnswers)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val givenAnswers = getGivenAnswers()
        return when (viewType) {
            TYPE_EDIT_STRING_VIEW -> EditTextStringViewHolder.from(parent)
            TYPE_EDIT_DATE_VIEW -> DatePickerViewHolder.from(parent)
            TYPE_EDIT_NUMBER_VIEW -> EditTextNumberViewHolder.from(parent)
            TYPE_SPINNER_VIEW -> SpinnerViewHolder.from(parent, givenAnswers)
            else -> throw ClassCastException("Unknown viewType $viewType!")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position].questiontype) {
            in Constants.QUESTION_TYPE_SINGLE_CHOICE, Constants.QUESTION_TYPE_SINGLE_CHOICE_KNOB -> TYPE_SPINNER_VIEW
            Constants.QUESTION_TYPE_TEXT_STRING -> TYPE_EDIT_STRING_VIEW
            Constants.QUESTION_TYPE_DATE -> TYPE_EDIT_DATE_VIEW
            Constants.QUESTION_TYPE_KNOB -> TYPE_EDIT_NUMBER_VIEW
            else -> -1
        }
    }

    private fun getGivenAnswers(): MutableMap<String, Answer>{
        val givenAnswers = sharedViewModel.currentAnswers[Constants.BASELINE_QUESTIONNAIRE] ?: mutableMapOf()
        sharedViewModel.currentAnswers[Constants.BASELINE_QUESTIONNAIRE] = givenAnswers
        return givenAnswers
    }

    class SpinnerViewHolder(
        val binding: ItemViewSpinnerBinding,
        val givenAnswers: MutableMap<String, Answer>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Question,
            viewModel: BaselineQuestioningViewModel
        ) {
            binding.question = item
            binding.baselineQuestioningViewModel = viewModel
            binding.executePendingBindings()
            val spinner = binding.spinnerItemViewSpinner
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val values = item.values as List<*>
                    val value = values[position].toString()
//                    val label = answers[position] as String
                    givenAnswers[item.label] = Answer(value, item.label, Date().time)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //not needed
                }
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                givenAnswers: MutableMap<String, Answer>
            ): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemViewSpinnerBinding.inflate(layoutInflater, parent, false)
                return SpinnerViewHolder(binding, givenAnswers)
            }
        }
    }

    class EditTextStringViewHolder(val binding: ItemViewEditTextStringBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Question,
            viewModel: BaselineQuestioningViewModel,
            givenAnswers: MutableMap<String, Answer>,
        ) {
            binding.question = item
            binding.executePendingBindings()
            val editText = binding.editTextItemViewEditText
            editText.addTextChangedListener(
                EditTextChangeListener(
                    viewModel,
                    givenAnswers,
                    editText,
                    Constants.QUESTION_TYPE_TEXT_STRING,
                    item
                )
            )
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemViewEditTextStringBinding.inflate(layoutInflater, parent, false)
                return EditTextStringViewHolder(binding)
            }
        }
    }

    class EditTextNumberViewHolder(val binding: ItemViewEditTextNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Question,
            viewModel: BaselineQuestioningViewModel,
            givenAnswers: MutableMap<String, Answer>,
        ) {
            binding.question = item
            binding.executePendingBindings()
            val editText = binding.editTextItemViewEditText
            editText.addTextChangedListener(
                EditTextChangeListener(
                    viewModel,
                    givenAnswers,
                    editText,
                    Constants.QUESTION_TYPE_KNOB,
                    item
                )
            )
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemViewEditTextNumberBinding.inflate(layoutInflater, parent, false)
                return EditTextNumberViewHolder(binding)
            }
        }

    }

    class DatePickerViewHolder(val binding: ItemViewDatePickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Question,
            givenAnswers: MutableMap<String, Answer>,
        ) {
            binding.question = item
            binding.executePendingBindings()
            val datePicker = binding.dateItemDatePicker
            val today = Calendar.getInstance()
            datePicker.maxDate = Date().time
            datePicker.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            ) { _, year, month, day ->
                givenAnswers[item.label] =
                    Answer("$day.${month + 1}.$year", item.label, Date().time)
            }
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemViewDatePickerBinding.inflate(layoutInflater, parent, false)
                return DatePickerViewHolder(binding)
            }
        }
    }

    /**
     * This is a [TextWatcher] used by the [EditText]s in the [RecyclerView.ViewHolder]s of this [QuestionAdapter].
     *
     * This Listener shows an error message if the input was an invalid one, else saves the input in
     * the given [MutableMap].
     *
     * @param editText the [EditText] that shall be initialized
     * @param givenAnswers a [MutableMap] of pairs of Strings and Answers, where the [EditText]s text
     * is saved in
     * @param viewModel the [BaselineQuestioningViewModel] holding functions to check input validity
     * @param contentType the content type of the EditText
     * @param question the current [Question] represented by the EditText
     * */
    class EditTextChangeListener(
        val viewModel: BaselineQuestioningViewModel,
        private val givenAnswers: MutableMap<String, Answer>,
        val editText: EditText,
        private val contentType: String,
        val question: Question
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //not needed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s == null) return
            /* checks if the changed text is valid and sets an error message if not */
            val errorMessage = when (contentType) {
                Constants.QUESTION_TYPE_TEXT_STRING -> viewModel.getProfessionErrorMessage(s.toString())
                Constants.QUESTION_TYPE_KNOB -> viewModel.getChildrenErrorMessage(s.toString())
                else -> ""
            }
            if (errorMessage != "") {
                editText.error = errorMessage
            } else if (s.toString() != "") {
                /* the answer gets updated if the changed text is not invalid nor empty */
                givenAnswers[question.label] = Answer(s.toString(), question.label, Date().time)
            }
        }

        override fun afterTextChanged(s: Editable?) {
            //not needed
        }
    }
}