package name.herbers.android.highsenso.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import name.herbers.android.highsenso.data.Client

/**
 * Converter to convert [Client]s for [HighSensoDatabase].
 *
 *@project HighSenso
 *@author Herbers
 */
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