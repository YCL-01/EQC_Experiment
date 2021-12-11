package com.example.expapp

import android.hardware.Sensor
import android.hardware.SensorManager

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.expapp.sensors.AccelerometerListener
import com.example.expapp.sensors.GyroscopeListener
import com.example.expapp.sensors.LightSensorListener


import kotlinx.android.synthetic.main.activity_player.*
import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import java.io.File
import java.util.ArrayList


class PlayerActivity : AppCompatActivity(){
    //Writing Frequency Counter
    private var accelerometerTimes = 0
    private var gyroTimes = 0
    private var lightTimes = 0
    private var resVal = 0

    //Sensor file names
    var ACCELEROMETER_SENSOR_FILE_NAME: String = "Tears_$resVal"+"_accelerometer_"+"$accelerometerTimes.csv"
    var GYRO_SENSOR_FILE_NAME: String = "Tears_$resVal"+"_gyro_"+"$gyroTimes.csv"
    var LIGHT_SENSOR_FILE_NAME: String = "Tears_$resVal"+"_light_"+"$lightTimes.csv"

    //Context
    private lateinit var context: Context

    //Sensor Manager and Sensors
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var lightSensor: Sensor? = null
    private var accelerometerListener: AccelerometerListener? = null
    private var gyroscopeListener: GyroscopeListener? = null
    private var lightSensorListener: LightSensorListener? = null


    //Video player variables
    private lateinit var trackSelector: DefaultTrackSelector
    private var player: ExoPlayer?= null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private val dashURL = "http://130.245.144.153:5000/video/dash_test2/tears.mpd"

    //hasStartedWriting file
    var hasStartedWriting = false

    //Writes down to CSV when sensors are on
    fun isHasStartedWriting(): Boolean {
        return hasStartedWriting
    }

    //initialize event listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        Log.d("Log", "PlayerActivity")
        var data:Int?
        resVal = intent.getIntExtra("value", 720)
        Log.d("Log", "value: " + resVal)

        context = this;

        //Initialize Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

        //Initialize Listeners
        accelerometerListener = AccelerometerListener(this)
        gyroscopeListener = GyroscopeListener(this)
        lightSensorListener = LightSensorListener(this)

    }

    override fun onStart() {
        super.onStart()
        hasStartedWriting = true
        setCounter()
        Log.d("Listener Registered", "Listeners registered")
        sensorManager!!.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(gyroscopeListener,gyroscope,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        initPlayer()
    }

    fun setCounter(){
        accelerometerTimes++
        gyroTimes++
        lightTimes++
    }

    override fun onResume() {
        super.onResume()
        hasStartedWriting=true
        setCounter()

    }

    override fun onPause() {
        super.onPause()
        hasStartedWriting = false;
        sensorManager!!.unregisterListener(accelerometerListener)
    }

    override fun onStop() {
        super.onStop()
        hasStartedWriting= false

        Log.d("Listeners unregistered", "Listeners unregistered")
        sensorManager!!.unregisterListener(accelerometerListener)
        sensorManager!!.unregisterListener(gyroscopeListener)
        sensorManager!!.unregisterListener(lightSensorListener)
        playerView.player = null

        releasePlayer()
        //sendEmail()

        val goToSurvey = Intent(this, SurveyActivity::class.java)
        startActivity(goToSurvey)
    }

    fun sendEmail() {
        val to = "example@gmail.com"
        val subject = "Some Subject"
        val message = "This is my message to you"

        val attachments = ArrayList<Uri>()

        val accelerometerFile =
            File(Environment.getExternalStorageDirectory().absolutePath + "/" + ACCELEROMETER_SENSOR_FILE_NAME)
        val accelerometerUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            accelerometerFile
        )

        val gyroFile =
            File(Environment.getExternalStorageDirectory().absolutePath + "/" + GYRO_SENSOR_FILE_NAME)
        val gyroUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            gyroFile
        )

        val lightFile =
            File(Environment.getExternalStorageDirectory().absolutePath + "/" + LIGHT_SENSOR_FILE_NAME)
        val lightUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            lightFile
        )

        attachments.add(accelerometerUri)
        attachments.add(gyroUri)
        attachments.add(lightUri)

        val email = Intent(Intent.ACTION_SEND_MULTIPLE)
        email.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(to))
        email.putExtra(Intent.EXTRA_SUBJECT, subject)
        email.putExtra(Intent.EXTRA_TEXT, message)
        email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachments)
        email.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        //need this to prompts email client only
        email.type = "message/rfc822"

        startActivity(Intent.createChooser(email, "Choose an Email client :"))
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

    private fun initPlayer(){
        var paramList: IntArray = resSelector()
        trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters()
                .setMinVideoSize(paramList[0],paramList[1])
                .setMinVideoBitrate(paramList[2])
                .setMaxVideoSize(paramList[3], paramList[4])
                .setMaxVideoBitrate(paramList[5])
            )

        }
        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
        player?.let{
            playerView.player = player
            val mediaSource = buildMediaSource(dashURL)
            it.setPlayWhenReady(playWhenReady)
            it.seekTo(currentWindow, playbackPosition)
            it.prepare(mediaSource, false, false)
        }
        playerView.player = player

    }

    private fun buildMediaSource(dashURL: String): MediaSource {
        val mediaItem = MediaItem.fromUri(Uri.parse(dashURL))
        val userAgent = Util.getUserAgent(this, "expApp")
        val factory = DefaultHttpDataSource.Factory()
        return DashMediaSource.Factory(factory).createMediaSource(mediaItem)
    }

    private fun resSelector(): IntArray {
        var paramList = IntArray(6)

        when(resVal){
            240 -> paramList = intArrayOf(320, 142, 700000, 320, 142, 800000)
            480 -> paramList = intArrayOf(854, 380, 1800000, 854, 380, 2000000)
            720 -> paramList = intArrayOf(1280, 570, 7000000, 1280, 570, 8000000)
            1080 -> paramList = intArrayOf(1920, 856, 15000000, 1920, 856, 20000000)
        }
        return paramList
    }

}
