package name.herbers.android.highsenso

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import name.herbers.android.highsenso.database.Question
import timber.log.Timber
import java.io.File

class QuestionSerializer() {

    @ExperimentalSerializationApi
    fun loadAll(path: String): List<Question> {
        var questionList: List<Question> = listOf()

        val questionFile = File(path)

        if (questionFile.exists()){
            Timber.i("File '$path' exists!")
            val questionsString: String = questionFile.readText(Charsets.UTF_8)
            questionList = Json.decodeFromString(questionsString)
        }

        return questionList
    }

    @ExperimentalSerializationApi
    fun saveAll(questionList: List<Question>, path: String, context: Context) {
        val questionFile = File(path)
        val questionsString = Json.encodeToString(questionList)
        Timber.d(questionsString)

        context.openFileOutput(path, Context.MODE_PRIVATE).use{ output -> output.write(questionsString.toByteArray())}

        if (questionFile.exists()){
            Timber.i("File '$path' exists!")
        }else{
            Timber.i("File does not exist!")
        }
//        questionFile.writeText(questionsString)
    }

}

