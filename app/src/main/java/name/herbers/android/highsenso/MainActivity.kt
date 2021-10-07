package name.herbers.android.highsenso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.QuestionDatabase
import name.herbers.android.highsenso.start.SharedDatabaseViewModel
import name.herbers.android.highsenso.start.SharedDatabaseViewModelFactory
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    //ViewModel shared with ResetDialogFragment and StartFragment
    private lateinit var sharedDatabaseViewModel: SharedDatabaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = requireNotNull(this).application
        val dataSource = QuestionDatabase.getInstance(application).questionDatabaseDao
        val sharedDatabaseViewModelFactory = SharedDatabaseViewModelFactory(dataSource, application)
        sharedDatabaseViewModel = ViewModelProvider(this, sharedDatabaseViewModelFactory).get(SharedDatabaseViewModel::class.java)

        Timber.i("onCreate called!")
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Timber.i("onSceInstanceState called")
        super.onSaveInstanceState(outState, outPersistentState)
    }

    //TODO delete this, it is just to test how to open an url with the app
    fun openChatBot() {
        val chatBotIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
        startActivity(chatBotIntent)
    }


    //TODO: Impressum (Appname, Version, copyright + Name)
    //TODO: Datenschutzerklärung (inkl. Abfrage nach Datensammlung bei erstem Appstart + speichern in einer config datei)
    //TODO: mögl. Ergänzung von Feedbackmöglichkeit (E-Mail)
    //TODO: Weiterführende Links zum Thema HSP
}