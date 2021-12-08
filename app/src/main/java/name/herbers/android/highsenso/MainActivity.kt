package name.herbers.android.highsenso

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.PersonalData
import name.herbers.android.highsenso.database.QuestionDatabase
import name.herbers.android.highsenso.sensor.AmbientAudioRecorder
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

    //launcher to ask for mic permission
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    //handler for repeating tasks (gathering of sensor data)
    private lateinit var repTaskHandler: Handler

    private lateinit var mSensorManager: SensorManager

    private val audioSensorMeasuringDuration = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = requireNotNull(this).application
//        deleteDatabaseInAppData()             //development only
//        resetFirstCallPreferencesKey()        //development only
        database = QuestionDatabase.getInstance(application)
        val dataSource = database.questionDatabaseDao
        val databaseHandler = DatabaseHandler(dataSource)
        val res = applicationContext.resources

        addGatheringSensorDataObserver()

        //init ActivityResultLauncher for permission stuff
//        audioRecording()
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Timber.i("Permission to record audio granted!")
                    audioRecording()
                } else {
                    Timber.i("Permission to record audio NOT granted!")
                }
            }

        //init personalData with string-arrays as param
        val personalData = PersonalData(
            locationList(),
            res.getStringArray(R.array.gender_array).toList(),
            res.getStringArray(R.array.marital_Status_array).toList(),
            res.getStringArray(R.array.education_array).toList(),
            res.getStringArray(R.array.professionType_array).toList()
        )

        //init sharedViewModel
        val sharedViewModelFactory =
            SharedViewModelFactory(databaseHandler, personalData)
        sharedViewModel = ViewModelProvider(
            this,
            sharedViewModelFactory
        ).get(SharedViewModel::class.java)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        Timber.i("onCreate called!")
    }

    private val gatherAudioPeriodically = object: Runnable {
        override fun run() {
            audioRecording()
            repTaskHandler.postDelayed(this, 5000)
        }
    }

    private fun startGatherAudioPeriodically(){
        Timber.i("Starting repeating task to gather audio data!")
        gatherAudioPeriodically.run()
    }

    private fun stopGatherAudioPeriodically() {
        Timber.i("Stopping repeating task to gather audio data!")
        repTaskHandler.removeCallbacks(gatherAudioPeriodically)
    }

    //TODO delete this, it is just to test how to open an url with the app
    fun openChatBot(uri: String) {
        val chatBotIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(chatBotIntent)
    }

    /**
     * Deletes the database files in the data folder on the device.
     * This is only for development purpose.
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

    /**
     * Resets the privacy_setting_first_call_key (sets it to true).
     * This is only for development purpose.
     * */
    private fun resetFirstCallPreferencesKey() {
        val preferences = this.getPreferences(Context.MODE_PRIVATE)
        preferences.edit().putBoolean(
            getString(R.string.privacy_setting_first_call_key),
            true
        ).apply()
        Timber.i("First call set to false!")
    }

    /**
     * Creates a [List] with all strings that represent a location.
     * */
    private fun locationList(): List<String> {
        return mutableListOf(
            getString(R.string.location_dialog_option_home),
            getString(R.string.location_dialog_option_work),
            getString(R.string.location_dialog_option_outside),
            getString(R.string.location_dialog_option_else)
        )
    }

    private fun audioRecording() {
        val permissionToRecord =
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
        if (permissionToRecord == PackageManager.PERMISSION_GRANTED) {
            Timber.i("Permission to record Audio granted! Recorder will be started!")
            val ambientAudioRecorder = AmbientAudioRecorder()
            ambientAudioRecorder.getAverageAmbientAudio(audioSensorMeasuringDuration)
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun getSensors(): List<Sensor> {
        return listOf<Sensor>(
            mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
            mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        )
    }

    private fun addGatheringSensorDataObserver(){
        sharedViewModel.gatherSensorData.observe(this, { toStart ->
            if (toStart){
                startGatherAudioPeriodically()
            }else{
                stopGatherAudioPeriodically()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //TODO pause data gathering?! and same in onPause
//        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
        stopGatherAudioPeriodically()
    }

    //TODO: Datenschutzerklärung (inkl. Abfrage nach Datensammlung bei erstem Appstart + speichern in einer config datei)
    //TODO: Weiterführende Links zum Thema HSP
}