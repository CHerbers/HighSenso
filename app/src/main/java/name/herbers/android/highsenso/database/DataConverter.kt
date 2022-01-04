package name.herbers.android.highsenso.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import name.herbers.android.highsenso.data.Answer
import name.herbers.android.highsenso.data.Client
import name.herbers.android.highsenso.data.Question
import name.herbers.android.highsenso.data.SensorData

/**
 *
 *@project HighSenso
 *@author Herbers
 */

class DataConverter

class QuestionConverter {
    @TypeConverter
    fun toQuestionList(json: String): List<Question> {
        val type = object : TypeToken<List<Question>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(questions: List<Question>): String {
        val type = object : TypeToken<List<Question>>() {}.type
        return Gson().toJson(questions, type)
    }
}

class AnswerConverter {
    private val type = object : TypeToken<List<Answer>>() {}.type

    @TypeConverter
    fun toAnswer(json: String): List<Answer> {
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(answers: List<Answer>): String {
        return Gson().toJson(answers, type)
    }
}

class SensorDataConverter {
    private val type = object : TypeToken<List<SensorData>>() {}.type

    @TypeConverter
    fun toSensorData(json: String): List<SensorData> {
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(sensorData: List<SensorData>): String {
        return Gson().toJson(sensorData, type)
    }
}

class ClientConverter {
    private val type = object : TypeToken<Client>() {}.type

    @TypeConverter
    fun toClient(json: String): Client {
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(client: Client): String {
        return Gson().toJson(client, type)
    }
}