package name.herbers.android.highsenso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.PersonalData
import name.herbers.android.highsenso.database.QuestionDatabase
import timber.log.Timber
import java.io.File

/**
 * This is the main (and only) activity of the HighSenso App.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class MainActivity : AppCompatActivity() {

    //ViewModel shared with ResetDialogFragment and StartFragment
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var database: QuestionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = requireNotNull(this).application
//        deleteDatabaseInAppData()
        database = QuestionDatabase.getInstance(application)
        val dataSource = database.questionDatabaseDao
        val databaseHandler = DatabaseHandler(dataSource)
        val res = applicationContext.resources
        val personalData = PersonalData(
            res.getStringArray(R.array.gender_array).toList(),
            res.getStringArray(R.array.marital_Status_array).toList(),
            res.getStringArray(R.array.education_array).toList()
        )
        val sharedViewModelFactory =
            SharedViewModelFactory(databaseHandler, personalData)
        sharedViewModel = ViewModelProvider(
            this,
            sharedViewModelFactory
        ).get(SharedViewModel::class.java)

        Timber.i("onCreate called!")
    }

    //TODO delete this, it is just to test how to open an url with the app
    fun openChatBot(uri: String) {
        val chatBotIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(chatBotIntent)
    }

    /**
     * Deletes the database files in the data folder on the device
     * */
    private fun deleteDatabaseInAppData() {
        //create a Files from an existing question_database (-shm, -wal)
        val roomDbPath =
            Environment.getDataDirectory().path + "/data/name.herbers.android.highsenso/databases/questions_database"
        val roomDb = File(roomDbPath)
        val roomDbShm = File("$roomDbPath-shm")
        val roomDbWal = File("$roomDbPath-wal")

        //delete Files if existing
        if (roomDb.exists()) {
            Timber.i("Db does exist and will be deleted!")
            roomDb.delete()
        }
        if (roomDbShm.exists()) {
            Timber.i("Db-smh does exist and will be deleted!")
            roomDbShm.delete()
        }
        if (roomDbWal.exists()) {
            Timber.i("Db-wal does exist and will be deleted!")
            roomDbWal.delete()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }

    //TODO: Impressum (Appname, Version, copyright + Name)
    //TODO: Datenschutzerklärung (inkl. Abfrage nach Datensammlung bei erstem Appstart + speichern in einer config datei)
    //TODO: mögl. Ergänzung von Feedbackmöglichkeit (E-Mail)
    //TODO: Weiterführende Links zum Thema HSP
}