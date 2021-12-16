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
import name.herbers.android.highsenso.data.AmbientAudioSensorData
import name.herbers.android.highsenso.data.AmbientLightSensorData
import name.herbers.android.highsenso.data.AmbientTemperatureSensorData
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
    private var askPermission = true

    //ViewModel shared with ResetDialogFragment and StartFragment
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var database: QuestionDatabase

    //launcher to ask for mic permission
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    //handler for repeating tasks (gathering of sensor data)
    private lateinit var repTaskHandler: Handler

    private lateinit var mSensorManager: SensorManager
    private lateinit var sensorList: List<Sensor>

    private var lastTempSensorMeasurement = 0L
    private var lastLightSensorMeasurement = 0L

    companion object {
        private const val SERVER_URL = "https://www.google.com" //TODO insert right URL
        private const val AUDIO_SENSOR_MEASURING_DURATION = 8
        private const val SENSOR_MEASURING_INTERVAL = 10000L
        private const val HSP_QUESTIONNAIRE = "HSPScala"
        private const val DEAL_WITH_HS_QUESTIONNAIRE = "DealWithHS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = requireNotNull(this).application
//        deleteDatabaseInAppData()             //development only
//        resetFirstCallPreferencesKey()        //development only
        database = QuestionDatabase.getInstance(application)!!
        val dataSource = database.questionDatabaseDao
        val databaseHandler = DatabaseHandler(dataSource)
        val res = applicationContext.resources

        preferences = this.getPreferences(Context.MODE_PRIVATE)

        //init personalData with string-arrays as param
        val userProfile = UserProfile(
            locationList(),
            res.getStringArray(R.array.gender_array).toList(),
            res.getStringArray(R.array.marital_Status_array).toList(),
            res.getStringArray(R.array.education_array).toList(),
            res.getStringArray(R.array.professionType_array).toList()
        )

        /* HTTP Connection */
        val serverCommunicationHandler =
            ServerCommunicationHandler(SERVER_URL, this)

        //init sharedViewModel TODO maybe look for saved questionnaires/ answerSheets
        val sharedViewModelFactory =
            SharedViewModelFactory(
                databaseHandler,
                userProfile,
                null,
                null,
                preferences,
                application,
                serverCommunicationHandler
            )
        sharedViewModel = ViewModelProvider(
            this,
            sharedViewModelFactory
        ).get(SharedViewModel::class.java)

        repTaskHandler = Handler()

        /* Login check and load questions and answerSheets if logged in */
        val token = preferences.getString(getString(R.string.login_data_token), null)
        val username = preferences.getString(getString(R.string.login_data_username_key), null)
        val password = preferences.getString(getString(R.string.login_data_pw_key), null)
        when {
            sharedViewModel.tokenIsValid() && token != null -> {
                sharedViewModel.changeLoginStatus(true)
                serverCommunicationHandler.getAllQuestionnaires(token, sharedViewModel)
            }
            username != null && password != null -> {
                Timber.i("Login data is available! Login Request will be sent!")
                sharedViewModel.changeLoginStatus(false)
                serverCommunicationHandler.sendLoginRequest(
                    username,
                    password,
                    sharedViewModel,
                    null
                )
            }
            else -> {
                sharedViewModel.changeLoginStatus(true) //TODO set to false!
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
        addGatheringSensorDataObserver()

        Timber.i("onCreate called!")
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

    /**
     * Checks permission for recording audio with the microphone.
     * If permission is already granted, start the [AmbientAudioRecorder].
     * Else, start the [ActivityResultLauncher] to ask for recording permission
     * */
    private fun audioRecording() {
        val permissionToRecord =
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
        if (permissionToRecord == PackageManager.PERMISSION_GRANTED) {
            Timber.i("Permission to record audio granted! Recorder will be started!")
            val ambientAudioRecorder = AmbientAudioRecorder()
            val recordedAudio =
                ambientAudioRecorder.getAverageAmbientAudio(AUDIO_SENSOR_MEASURING_DURATION)
            when (sharedViewModel.questionnaireName.value) {
                HSP_QUESTIONNAIRE -> sharedViewModel.sensorDataHSP.add(
                    AmbientAudioSensorData(
                        getString(R.string.ambient_audio_sd_field),
                        Date().time,
                        recordedAudio.toFloat()
                    )
                )  //TODO multiple data (like it is now) or just one average value
                DEAL_WITH_HS_QUESTIONNAIRE -> sharedViewModel.sensorDataDWHS.add(
                    AmbientAudioSensorData(
                        getString(R.string.ambient_audio_sd_field),
                        Date().time,
                        recordedAudio.toFloat()
                    )
                )
            }
        } else {
            if (askPermission) {
                requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                askPermission = false
            }
        }
    }

    /**
     * Creates a [List] of needed [Sensor]s.
     * That is the temperature and the light sensor.
     *
     * @return a [List] of [Sensor]s
     * */
    private fun getSensors(): List<Sensor> {
        return listOf<Sensor>(
            mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
            mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        )
    }

    /**
     * Adds an [Observer] to [SharedViewModel.gatherSensorData].
     * If changed to true, starts gathering of sensor data.
     * Else, stops gathering of sensor data.
     * */
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

    /**
     * Register sensor listener in [sensorList].
     * */
    private fun registerSensorListeners() {
        for (sensor in sensorList) {
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    /**
     * Unregister sensor listener in [sensorList].
     * */
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
        val currentTime = Date().time

        if (event != null) {
            when (event.sensor) {
                mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) -> {
                    val measuredTemp = event.values[0]
                    if (lastTempSensorMeasurement == 0L) {
                        saveTempSensorData(measuredTemp, currentTime)
                    } else if (currentTime > lastTempSensorMeasurement + SENSOR_MEASURING_INTERVAL) {
                        saveTempSensorData(measuredTemp, currentTime)
                    }
                }
                mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) -> {
                    val measuredLight = event.values[0]
                    if (lastLightSensorMeasurement == 0L) {
                        saveLightSensorData(measuredLight, currentTime)
                    } else if (currentTime > lastLightSensorMeasurement + SENSOR_MEASURING_INTERVAL) {
                        saveLightSensorData(measuredLight, currentTime)
                    }
                }
            }
        }
    }

    private fun saveTempSensorData(measuredTemp: Float, currentTime: Long) {
        Timber.i("Event on ambient temperature sensor: $measuredTemp °C")
        lastTempSensorMeasurement = currentTime
        val tempData = AmbientTemperatureSensorData(
            getString(R.string.ambient_temp_sd_field),
            Date().time,
            measuredTemp
        )
        when (sharedViewModel.questionnaireName.value) {
            HSP_QUESTIONNAIRE -> sharedViewModel.sensorDataHSP.add(tempData)
            DEAL_WITH_HS_QUESTIONNAIRE -> sharedViewModel.sensorDataDWHS.add(tempData)
        }
    }

    private fun saveLightSensorData(measuredLight: Float, currentTime: Long) {
        Timber.i("Event on light sensor: $measuredLight lux")
        lastLightSensorMeasurement = currentTime
        val lightData = AmbientLightSensorData(
            getString(R.string.ambient_light_sd_field),
            Date().time,
            measuredLight
        )
        when (sharedViewModel.questionnaireName.value) {
            HSP_QUESTIONNAIRE -> sharedViewModel.sensorDataHSP.add(lightData)
            DEAL_WITH_HS_QUESTIONNAIRE -> sharedViewModel.sensorDataDWHS.add(lightData)
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
}