package name.herbers.android.highsenso

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
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
import name.herbers.android.highsenso.connection.ServerCommunicationHandler
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.QuestionDatabase
import name.herbers.android.highsenso.database.UserProfile
import name.herbers.android.highsenso.sensor.AmbientAudioRecorder
import timber.log.Timber
import java.io.File
import java.util.*

/**
 * This is the main (and only) activity of the HighSenso App.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var preferences: SharedPreferences

    //ViewModel shared with ResetDialogFragment and StartFragment
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var database: QuestionDatabase

    //launcher to ask for mic permission
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    //handler for repeating tasks (gathering of sensor data)
    private lateinit var repTaskHandler: Handler

    private lateinit var mSensorManager: SensorManager
    private lateinit var sensorList: List<Sensor>

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

        preferences = this.getPreferences(Context.MODE_PRIVATE)

        //init personalData with string-arrays as param
        val personalData = UserProfile(
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

        repTaskHandler = Handler()

        /* HTTP Connection */
        val serverCommunicationHandler = ServerCommunicationHandler("https://www.google.com", this)
//        httpConnection.sendGETRequest()

        /* Login check and load questions and answerSheets if logged in */
        val token = preferences.getString(getString(R.string.login_data_token), null)
        when {
            tokenIsValid() && token != null -> {
                serverCommunicationHandler.getQuestionnaire(token)
            }
            loginDataAvailable() -> {
                sendLoginFromKeyData()
            }
            else -> {
                sharedViewModel.changeLoginStatus(false)
            }
        }

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





        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorList = getSensors()
//        for (sensor in sensorList) {
//            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
//        }
        addGatheringSensorDataObserver()

        Timber.i("onCreate called!")
    }

    private fun tokenIsValid(): Boolean {
        val tokenExpirationDate =
            preferences.getLong(getString(R.string.login_data_token_expiration_date), 0)
        val isValid = tokenExpirationDate != 0L && Date().before(Date(tokenExpirationDate))
        Timber.i("Token is valid: $isValid")
        sharedViewModel.changeLoginStatus(isValid)
        return isValid
    }

    private fun loginDataAvailable(): Boolean {
        val username = preferences.getString(getString(R.string.login_data_username_key), null)
        val password = preferences.getString(getString(R.string.login_data_pw_key), null)
        val dataAvailable = username != null && password != null
        Timber.i("Login data is available: $dataAvailable")
        sharedViewModel.changeLoginStatus(false)
        return dataAvailable
    }

    private fun sendLoginFromKeyData() {
        //TODO create the http request body from key data and call serverCommunicationHandler
    }

    private val gatherAudioPeriodically = object : Runnable {
        override fun run() {
            audioRecording()
            repTaskHandler.postDelayed(this, 5000)
        }
    }

    private fun startGatherAudioPeriodically() {
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
            val recordedAudio =
                ambientAudioRecorder.getAverageAmbientAudio(audioSensorMeasuringDuration)
            //TODO save recorded audio in list
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

    private fun addGatheringSensorDataObserver() {
        sharedViewModel.gatherSensorData.observe(this, { toStart ->
            if (toStart) {
                startGatherAudioPeriodically()
                registerSensorListeners()
            } else {
                stopGatherAudioPeriodically()
                unregisterSensorListeners()
            }
        })
    }

    private fun registerSensorListeners() {
        for (sensor in sensorList) {
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun unregisterSensorListeners() {
        for (sensor in sensorList) {
            mSensorManager.unregisterListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (sharedViewModel.gatherSensorData.value == true) startGatherAudioPeriodically()
        registerSensorListeners()
        Timber.i("onResume called!")
    }

    override fun onPause() {
        super.onPause()
        stopGatherAudioPeriodically()
        unregisterSensorListeners()
        Timber.i("onPause called!")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        if (event != null) {
            when (event.sensor) {
                mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) -> {
                    val measuredTemp = event.values[0]
                    Timber.i("Event on ambient temperature sensor: $measuredTemp °C")
                    //TODO save this value
                }
                mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) -> {
                    val measuredLight = event.values[0]
                    Timber.i("Event on light sensor: $measuredLight lux")
                    //TODO save this value
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //nothing to do here
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
        stopGatherAudioPeriodically()
    }


    //TODO: Datenschutzerklärung (inkl. Abfrage nach Datensammlung bei erstem Appstart + speichern in einer config datei)
    //TODO: Weiterführende Links zum Thema HSP
}