package name.herbers.android.highsenso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.i("onCreate called")

        //TODO if anything saved in @onSaveInstanceState it needs to be restored here. Like:
        // variable = savedInstanceState.getInt(KEY)

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        //TODO save everything that needs to be saved if app is stopped (put in the background) OR
        // even rotated! Like:
        // outState.putInt(KEY, variable)

        super.onSaveInstanceState(outState, outPersistentState)
        Timber.i("onSceInstanceState called")
    }

    //TODO delete this, it is just to test how to open an url with the app
    fun openChatBot(){
        val chatBotIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
        startActivity(chatBotIntent)
    }


    //TODO: Impressum (Appname, Version, copyright + Name)
    //TODO: Datenschutzerklärung (inkl. Abfrage nach Datensammlung bei erstem Appstart + speichern in einer config datei)
    //TODO: mögl. Ergänzung von Feedbackmöglichkeit (E-Mail)
    //TODO: Weiterführende Links zum Thema HSP
}