package com.example.expapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.example.expapp.PlayerActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.activity_survey.*
import java.io.File
import java.util.ArrayList

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
        println("Send the final email")

       sendEmail("Participant Number: $participant\nAge: $age\nSex: $sex\nRating: $score")
        participant++
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

    fun sendEmail(message:String) {
        val to = "youngchan.lim@stonybrook.edu "
        val subject = "SUNY Test"
        val message = message


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
}