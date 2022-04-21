package com.example.expapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_survey.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileOutputStream
import java.net.URL

class SurveyActivity : AppCompatActivity(), View.OnClickListener,
    RatingBar.OnRatingBarChangeListener, AdapterView.OnItemSelectedListener {

    //Survey Data
    private var age: String? = null
    private var sex: String? = null
    private var score = 0.0
    private var resVal = 0
    private var vidType = ""
    private var trial = 0
    private var userName = ""

    //Context
    private lateinit var context: Context

    //Participant Number
    private var playerActivity: PlayerActivity = PlayerActivity()

    //Sensor file names
    var ACCELEROMETER_SENSOR_FILE_NAME: String = playerActivity.ACCELEROMETER_SENSOR_FILE_NAME
    var GYRO_SENSOR_FILE_NAME: String = playerActivity.GYRO_SENSOR_FILE_NAME
    var LIGHT_SENSOR_FILE_NAME: String = playerActivity.LIGHT_SENSOR_FILE_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        context = this;
        resVal = intent.getIntExtra("resVal", 720)
        vidType = intent.getStringExtra("vidType").toString()
        trial = intent.getIntExtra("trial", 0)
        userName = SubActivity.userName //intent.getStringExtra("name").toString()


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
        var fileName: String = "surveyData.txt"
        var dataToWrite: String = "Age: $age\nSex: $sex\nRating: $score\nResolution: $resVal\nVideo: $vidType"
        var outputFile : FileOutputStream = openFileOutput(fileName, MODE_PRIVATE)
        outputFile.write(dataToWrite.toByteArray())	//memo : String DATA
        outputFile.close()

        val nextIntent = Intent(this, FTPActivity::class.java)
        nextIntent.putExtra("name", userName)
        nextIntent.putExtra("trial", userName)
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


}