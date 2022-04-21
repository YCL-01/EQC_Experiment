package com.example.expapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.app.Activity
import kotlin.properties.Delegates


// Main - Player(+Listener) - Info

class MainActivity : AppCompatActivity() {

    //Check Trial
    companion object{
        public var Trial: Int = 0
    }

    //Check Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE: Int = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyWriteStoragePermissions(this)
        verifyReadStoragePermissions(this)


        btn_start.setOnClickListener{
            var userName = nameInput.text.toString()
            var playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra("name", userName)
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