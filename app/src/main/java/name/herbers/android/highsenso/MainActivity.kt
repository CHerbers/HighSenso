package name.herbers.android.highsenso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import name.herbers.android.highsenso.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate called")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        openChatBot() //TODO delete
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