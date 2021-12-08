package name.herbers.android.highsenso.sensor

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import name.herbers.android.highsenso.dispatcher.DefaultDispatcherProvider
import name.herbers.android.highsenso.dispatcher.DispatcherProvider
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class AmbientAudioRecorder(private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()) {
    //needed for coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(dispatchers.main() + viewModelJob)

    private val sampleRate = 8000
    private val channels = AudioFormat.CHANNEL_IN_MONO  //mono is guaranteed to work on all devices
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT

    init {
        Timber.i("AmbientAudioRecorder initialized!")
    }

    fun getAverageAmbientAudio(recordTime: Int): Double{
        Timber.i("getAverageAmbientAudio called!")
        var audio = 0.0
        uiScope.launch {
            audio = startRecording(recordTime)
        }
        return audio
    }

    private suspend fun startRecording(recordTime: Int): Double{
        return withContext(dispatchers.io()){
            recordingAmbientAudio(recordTime)
        }
    }

    /**
     * This method records teh ambient audio and calculates the average amplitude based on the recorded
     * values.
     *
     * @return the average ambient Audio recorded, or 0.0 in case of an error
     * */
    private fun recordingAmbientAudio(recordTime: Int): Double {
        var averageAmbientAudio = 0.0
        val minBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            channels,
            audioEncoding
        )  //the min possible buffer size
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) return averageAmbientAudio
        val bufferSize = minBufferSize * recordTime
        Timber.i("Min buffer size $minBufferSize. Buffer: $bufferSize.")
        val recordedAudio = ShortArray(bufferSize)

        val audioRecorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channels,
            audioEncoding,
            bufferSize
        )

        if (audioRecorder.state == AudioRecord.STATE_INITIALIZED) {
            audioRecorder.startRecording()
            Timber.i("Recording started!")
            audioRecorder.read(recordedAudio, 0, recordedAudio.size)
            audioRecorder.stop()
            Timber.i("Recording stopped!")

            var sumOfRecords = 0.0
            var numberOfValidRecords = 0
            for (recorded in recordedAudio) {
                if (recorded > 0) {
//                    Timber.i("Recorded Audio: $recorded.")
                    sumOfRecords += recorded
                    numberOfValidRecords++
                }
            }
            averageAmbientAudio = sumOfRecords / numberOfValidRecords
            averageAmbientAudio /= 32767f
            Timber.i("Average ambition audio: $averageAmbientAudio")
            audioRecorder.release()
        }
        return averageAmbientAudio
    }
}