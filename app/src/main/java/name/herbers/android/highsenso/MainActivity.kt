package name.herbers.android.highsenso

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.connection.ServerCommunicationHandler
import name.herbers.android.highsenso.data.AmbientAudioSensorData
import name.herbers.android.highsenso.data.AmbientLightSensorData
import name.herbers.android.highsenso.data.AmbientTempSensorData
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.HighSensoDatabase
import name.herbers.android.highsenso.sensor.AmbientAudioRecorder
import timber.log.Timber
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
    private lateinit var database: HighSensoDatabase

    //launcher to ask for mic permission
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    //handler for repeating tasks (gathering of sensor data)
    private lateinit var repTaskHandler: Handler

    private lateinit var mSensorManager: SensorManager
    private lateinit var sensorList: List<Sensor>

    private var lastTempSensorMeasurement = 0L
    private var lastLightSensorMeasurement = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = requireNotNull(this).application
        if (Constants.FIRST_START) resetFirstCallPreferencesKey()           //development only
        database = HighSensoDatabase.getInstance(application)
        val databaseHandler = DatabaseHandler(database.highSensoDatabaseDao)

        preferences = this.getPreferences(Context.MODE_PRIVATE)

        /* HTTP Connection */
        val serverCommunicationHandler =
            ServerCommunicationHandler(Constants.SERVER_URL, this)

        //init sharedViewModel
        val sharedViewModelFactory =
            SharedViewModelFactory(
                databaseHandler,
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

        repTaskHandler = Handler(Looper.getMainLooper())

        /* Login check and load questions and answerSheets if logged in */
        val token = preferences.getString(getString(R.string.login_data_token), null)
        val username = preferences.getString(getString(R.string.login_data_username_key), null)
        val password = preferences.getString(getString(R.string.login_data_pw_key), null)
        when {
            sharedViewModel.tokenIsValid() && token != null -> {
                sharedViewModel.changeLoginStatus(true)
                serverCommunicationHandler.getAllQuestionnaires(token, sharedViewModel)
                serverCommunicationHandler.getAllAnswerSheets(token, sharedViewModel)
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
                sharedViewModel.changeLoginStatus(Constants.OFFLINE_MODE)
            }
        }

        //init ActivityResultLauncher for permission stuff
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
        addAllObservers()

        Timber.i("onCreate called!")
    }

    private val gatherAudioPeriodically = object : Runnable {
        override fun run() {
            audioRecording()
            val sensorMeasuringIntervalMillis = Constants.SENSOR_MEASURING_INTERVAL_NANOS / 1000000L
            repTaskHandler.postDelayed(this, sensorMeasuringIntervalMillis)
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
                ambientAudioRecorder.getAverageAmbientAudio(Constants.AUDIO_SENSOR_MEASURING_DURATION)
            when (sharedViewModel.currentQuestionnaireName) {
                Constants.HSP_SCALE_QUESTIONNAIRE -> sharedViewModel.sensorDataHSP.add(
                    AmbientAudioSensorData(
                        Date().time,
                        recordedAudio.toFloat()
                    )
                )
                Constants.DEAL_WITH_HS_QUESTIONNAIRE -> sharedViewModel.sensorDataDWHS.add(
                    AmbientAudioSensorData(
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
     * Calls methods to add all needed Observers.
     * */
    private fun addAllObservers() {
        addGatheringSensorDataObserver()
        addShowResetPasswordSuccessionToast()
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
     * Adds an [Observer] to [SharedViewModel.showResetPasswordSuccessionToast].
     * If value changed to a not-empty string, a [Toast] is shown with the new value as message.
     * Else, no Toast is shown
     * */
    private fun addShowResetPasswordSuccessionToast() {
        sharedViewModel.showResetPasswordSuccessionToast.observe(this, { toastMessage ->
            if (toastMessage != "") {
                Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_SHORT
                ).show()
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
        //null check
        val eventTime = event?.timestamp ?: return
        when (event.sensor) {
            mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) -> {
                val measuredTemp = event.values[0]
                if (lastTempSensorMeasurement == 0L || eventTime > lastTempSensorMeasurement + Constants.SENSOR_MEASURING_INTERVAL_NANOS) {
                    Timber.i("eventTime: $eventTime; last: $lastTempSensorMeasurement")
                    saveTempSensorData(measuredTemp, eventTime)
                }
            }
            mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) -> {
                val measuredLight = event.values[0]
                if (lastLightSensorMeasurement == 0L || eventTime > lastLightSensorMeasurement + Constants.SENSOR_MEASURING_INTERVAL_NANOS) {
                    saveLightSensorData(measuredLight, eventTime)
                }
            }
        }
    }

    /**
     * This function saves the given temperature as [AmbientTempSensorData] in an List to later on
     * send it in an [AnswerSheet].
     *
     * @param measuredTemp is the temperature measured by the sensor
     * @param eventTime is the time, the temperature was measured at
     * */
    private fun saveTempSensorData(measuredTemp: Float, eventTime: Long) {
        Timber.i("Event on ambient temperature sensor: $measuredTemp °C")
        lastTempSensorMeasurement = eventTime
        val tempData = AmbientTempSensorData(
            Date().time,
            measuredTemp
        )
        when (sharedViewModel.currentQuestionnaireName) {
            Constants.HSP_SCALE_QUESTIONNAIRE -> sharedViewModel.sensorDataHSP.add(tempData)
            Constants.DEAL_WITH_HS_QUESTIONNAIRE -> sharedViewModel.sensorDataDWHS.add(tempData)
        }
    }

    /**
     * This function saves the given temperature as [AmbientLightSensorData] in an List to later on
     * send it in an [AnswerSheet].
     *
     * @param measuredLight is the light measured by the sensor
     * @param eventTime is the time, the light was measured at
     * */
    private fun saveLightSensorData(measuredLight: Float, eventTime: Long) {
        Timber.i("Event on light sensor: $measuredLight lux")
        lastLightSensorMeasurement = eventTime
        val lightData = AmbientLightSensorData(
            Date().time,
            measuredLight
        )
        when (sharedViewModel.currentQuestionnaireName) {
            Constants.HSP_SCALE_QUESTIONNAIRE -> sharedViewModel.sensorDataHSP.add(lightData)
            Constants.DEAL_WITH_HS_QUESTIONNAIRE -> sharedViewModel.sensorDataDWHS.add(lightData)
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