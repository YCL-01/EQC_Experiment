package com.example.expapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btn_start
import kotlinx.android.synthetic.main.activity_sub.*


// Main - Player(+Listener) - Info

class SubActivity : AppCompatActivity() {

    //Check Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE: Int = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var trial: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        verifyWriteStoragePermissions(this)
        verifyReadStoragePermissions(this)

        trial = findViewById(R.id.trial_show)
        var trial_text = "Trial: " + MainActivity.Trial.toString() + " / 10"
        trial?.text = trial_text

        println("Trial : "+ MainActivity.Trial)
        println("userName : "+ MainActivity.userName)

        btn_start.setOnClickListener{
            var playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra("name", MainActivity.userName)
            MainActivity.Trial++
            startActivity(playerIntent)
        }
    }


    fun verifyWriteStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
    fun verifyReadStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}