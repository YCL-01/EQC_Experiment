package com.example.expapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

class SurveyActivity : AppCompatActivity(), View.OnClickListener,
    RatingBar.OnRatingBarChangeListener, AdapterView.OnItemSelectedListener {

    //Survey Data
    private var age: String? = null
    private var sex: String? = null
    private var score = 0.0

    //Context
    private lateinit var context: Context

    //Participant Number
    private var participant = 0
    private var playerActivity: PlayerActivity = PlayerActivity()

    //Sensor file names
    var ACCELEROMETER_SENSOR_FILE_NAME: String = playerActivity.ACCELEROMETER_SENSOR_FILE_NAME
    var GYRO_SENSOR_FILE_NAME: String = playerActivity.GYRO_SENSOR_FILE_NAME
    var LIGHT_SENSOR_FILE_NAME: String = playerActivity.LIGHT_SENSOR_FILE_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        context = this;

        //Select Age
        val spinner = findViewById<View>(R.id.spinner) as Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.ages, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setOnItemSelectedListener(this)

        //Extract Rating
        val ratingBar = findViewById<View>(R.id.ratingBar) as RatingBar
        ratingBar.setOnRatingBarChangeListener(this)

        // Send Email
        val finish = findViewById<View>(R.id.finishButton) as Button
        finish.setOnClickListener(this)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        age = parent.getItemAtPosition(pos).toString()
        println("age: $age")
    }

    override fun onClick(v: View?) {
        //Send email
        //println("Send the final email")
        //sendEmail("Participant Number: $participant\nAge: $age\nSex: $sex\nRating: $score")
        participant++
        //ftpFileUpload("Participant Number: $participant\nAge: $age\nSex: $sex\nRating: $score")
        //uploadFiles("Participant Number: $participant\nAge: $age\nSex: $sex\nRating: $score")
        val nextIntent = Intent(this, FTPActivity::class.java)
        startActivity(nextIntent)

    }

    fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.maleButton -> if (checked) sex = "Male"
            R.id.femalebutton -> if (checked) sex = "Female"
        }
        println("Sex: $sex")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
        score = ratingBar.rating.toDouble()
        println("Rating: $score")
    }

    fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        onRadioButtonClicked(group)
    }

    fun ftpFileUpload(message: String) {
        Log.d("Into uploadFile:", message)

    }

    /*
    private fun uploadFiles(message: String){
        //val gyroFile = File(Environment.getExternalStorageDirectory().absolutePath + "/" + GYRO_SENSOR_FILE_NAME)
        //val lightFile = File(Environment.getExternalStorageDirectory().absolutePath + "/" + LIGHT_SENSOR_FILE_NAME)
        Log.d("Into uploadFile:", message)
        val serverUrl = "http://115.85.180.227:10050/uploads"
        val accFile = File(accessFile())

        val fileUploadThread = Thread{
            try{
                val fileBody = accFile.asRequestBody("text/csv".toMediaTypeOrNull())
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("userfile",ACCELEROMETER_SENSOR_FILE_NAME,fileBody)
                    .build()

                val request = Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build()

                val httpBuilder = OkHttpClient.Builder()
                val okHttpClient = httpBuilder
                    .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20,java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseStr = response.body?.string()
                if (responseStr != null) {
                    println("Request:$responseStr")
                }else{
                    println("Request: No request have arrived")
                }
            } catch (e: Exception){}
        }
        fileUploadThread.join()
        Log.d("Thread:", "Thread is done")
    }
    */
}