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
    RatingBar.OnRatingBarChangeListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        context = this;
        resVal = intent.getIntExtra("resVal", 720)
        vidType = intent.getStringExtra("vidType").toString()
        trial = intent.getIntExtra("trial", 0)
        userName = MainActivity.userName //intent.getStringExtra("name").toString()
        age = MainActivity.age
        sex = MainActivity.sex

        //Extract Rating
        val ratingBar = findViewById<View>(R.id.ratingBar) as RatingBar
        ratingBar.setOnRatingBarChangeListener(this)

        // Send to Server
        val finish = findViewById<View>(R.id.finishButton) as Button
        finish.setOnClickListener(this)
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

    override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
        score = ratingBar.rating.toDouble()
        println("Rating: $score")
    }


}