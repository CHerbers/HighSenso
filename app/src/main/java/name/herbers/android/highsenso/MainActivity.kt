package name.herbers.android.highsenso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import name.herbers.android.highsenso.database.Question
import timber.log.Timber

class MainActivity : AppCompatActivity() {

//    @ExperimentalSerializationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.i("onCreate called")

        //TODO if anything saved in @onSaveInstanceState it needs to be restored here. Like:
        // variable = savedInstanceState.getInt(KEY)

//        val question0 = Question(0, "Frage1", "funktioniert das?", "nur ne test frage")
//        val question1 = Question(1, "Frage2", "funktioniert das hier auch?", "nur ne test frage")
//        val question2 = Question(2, "Frage3", "funktioniert das immer noch?", "nur ne test frage")
//
//        var questionList = listOf(question0, question1, question2)
//
//        Timber.i("this is the current dir: " + System.getProperty("user.dir"))
//
//        val questionSerializer = QuestionSerializer()
//        val path = FileSystems.getDefault().getPath(
//            "",
//            "questions.txt"
//        )//Paths.get("").toAbsolutePath().toString() + "questions.txt"
//        Timber.i("The current path is $path")
//        questionSerializer.saveAll(questionList, path.toString(), applicationContext)
//        questionList = questionSerializer.loadAll(path.toString())

    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        //TODO save everything that needs to be saved if app is stopped (put in the background) OR
        // even rotated! Like:
        // outState.putInt(KEY, variable)

        super.onSaveInstanceState(outState, outPersistentState)
        Timber.i("onSceInstanceState called")
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