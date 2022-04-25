package com.example.expapp


import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.expapp.MainActivity.Companion.Trial
import com.example.expapp.sensors.AccelerometerListener
import com.example.expapp.sensors.GyroscopeListener
import com.example.expapp.sensors.LightSensorListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_player.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Random
import java.io.IOException
import java.net.URL


class PlayerActivity : AppCompatActivity(){
    //Init Var
    private var resVal = 0
    private var trial = 0
    private var vidNum = 0
    private var vidName = ""
    private var userName = ""

    //Sensor file names
    var ACCELEROMETER_SENSOR_FILE_NAME: String= "acc.csv"
    var GYRO_SENSOR_FILE_NAME: String= "gyro.csv"
    var LIGHT_SENSOR_FILE_NAME: String= "light.csv"

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
    private var dashURL = ""

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

        // Video initialize
        userName = intent.getStringExtra("name").toString()// MainActivity에서 받음
        trial = Trial //intent.getIntExtra("trial", 0)// FTPActivity에서 받음
        //vidNum = sendPost(userName) // node server 통신
        var (url, vidInfo, res) = getUrl(trial) // vidNum에 따라서 url, resolution 정보 구분
        dashURL = url
        vidName = vidInfo
        resVal = res
        context = this;

        //Initialize files
        ACCELEROMETER_SENSOR_FILE_NAME  = "acc.csv"
        GYRO_SENSOR_FILE_NAME = "gyro.csv"
        LIGHT_SENSOR_FILE_NAME = "light.csv"
        val fileList = listOf<String>(ACCELEROMETER_SENSOR_FILE_NAME,GYRO_SENSOR_FILE_NAME,LIGHT_SENSOR_FILE_NAME)
        val baseDir = "/data/data/com.example.expapp/files"
        for(i in fileList){
            var file = File(baseDir + File.separator + i)
            if(file.exists()){
                println("file exists!")
                file.delete()
            }else{
                println("file does not exist!")
                continue
            }
        }

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
        sensorManager!!.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(gyroscopeListener,gyroscope,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        initPlayer()
        val eventListener : Player.Listener
        eventListener = object: Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playBackState: Int) {
                if (playBackState == Player.STATE_ENDED) {
                    onStop()
                }
            }
        }
        player?.addListener(eventListener)
    }

    override fun onResume() {
        super.onResume()
        hasStartedWriting=true
    }

    override fun onPause() {
        super.onPause()
        hasStartedWriting = false;
        sensorManager!!.unregisterListener(accelerometerListener)
        sensorManager!!.unregisterListener(gyroscopeListener)
        sensorManager!!.unregisterListener(lightSensorListener)
    }


    override fun onStop() {
        super.onStop()
        hasStartedWriting= false

        sensorManager!!.unregisterListener(accelerometerListener)
        sensorManager!!.unregisterListener(gyroscopeListener)
        sensorManager!!.unregisterListener(lightSensorListener)

        playerView.player = null
        player?.playWhenReady = false
        releasePlayer()

        val goToSurvey = Intent(this, SurveyActivity::class.java)
        goToSurvey.putExtra("resVal", resVal)
        goToSurvey.putExtra("vidType", vidName)
        goToSurvey.putExtra("trial", trial)
        goToSurvey.putExtra("name", userName)
        startActivity(goToSurvey)
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
            240 -> paramList = intArrayOf(320, 240, 500000, 320, 240, 800000)
            480 -> paramList = intArrayOf(640, 480, 2000000, 640, 480, 3500000)
            720 -> paramList = intArrayOf(1280, 720, 7000000, 1280, 720, 11000000)
            1080 -> paramList = intArrayOf(1920, 1080, 11100000, 1920, 1080, 20000000)
        }
        return paramList
    }
    /* Has Done
     * sendGet 함수 작성 완료
     * TODO
     * 영상 아무거나 대충 40개 경로 맞춰서 넣고 테스트 해야함
     */
    /*
    private fun sendPost(userName: String): Int {
        val client = OkHttpClient()
        val url = URL("http://101.250.30.99:5000/count")
        val postBody = userName.trimMargin()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        var tmpVidNum = 0
        val response = client.newCall(request).enqueue( object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error")
            }

            override fun onResponse(call: Call, response: Response) {
                tmpVidNum = (response?.body?.string()?.toInt() ?: Int) as Int
            }

        })
        return tmpVidNum
    }*/

    private fun getUrl(trial: Int): Triple<String, String, Int> {
        var vidPathUrl = ""
        val range = (0..3)
        var vid = ""
        if(trial%2 == 0){
            vidPathUrl += "static/"
            if(trial == 2){
                vidPathUrl += "static_0/static.mpd"
                vid = "static_0"
            }else if(trial == 4){
                vidPathUrl += "static_1/static.mpd"
                vid = "static_1"
            }else if(trial == 6){
                vidPathUrl += "static_2/static.mpd"
                vid = "static_2"
            }else if(trial == 8){
                vidPathUrl += "static_3/static.mpd"
                vid = "static_3"
            }else if(trial == 10){
                vidPathUrl += "static_4/static.mpd"
                vid = "static_4"
            }
        }else{
            vidPathUrl += "dynamic/"
            if(trial == 1){
                vidPathUrl += "dynamic_0/dynamic.mpd"
                vid = "dynamic_0"
            }else if(trial == 3){
                vidPathUrl += "dynamic_1/dynamic.mpd"
                vid = "dynamic_1"
            }else if(trial == 5){
                vidPathUrl += "dynamic_2/dynamic.mpd"
                vid = "dynamic_0"
            }else if(trial == 7){
                vidPathUrl += "dynamic_3/dynamic.mpd"
                vid = "dynamic_0"
            }else if(trial == 9){
                vidPathUrl += "dynamic_4/dynamic.mpd/"
                vid = "dynamic_0"
            }
        }
        var resNum = range.random()
        var resType = 0
        when(resNum){
            0 -> resType = 240
            1 -> resType = 480
            2 -> resType = 720
            3 -> resType = 1080
        }

        return Triple(vidPathUrl, vid, resType)
    }
}
