package name.herbers.android.highsenso.questioning

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.data.Question

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class QuestionAdapter: RecyclerView.Adapter<QuestionViewHolder>() {

    var data = listOf<Question>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val item = data[position]

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.fragment_baseline_questioning, parent, false) as TextView
        return QuestionViewHolder(view)
    }
}