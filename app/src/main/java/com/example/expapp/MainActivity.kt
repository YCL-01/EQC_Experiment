package com.example.expapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.app.Activity
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_survey.*
import java.io.File
import java.util.Random




// Main - Player(+Listener) - Info

class MainActivity : AppCompatActivity() {

    // Count
    companion object{

        var Count : Int = 0
    }
    // User Info
    var name: String = ""

    //Sample video
    private var resolution : Int = 0
    private var vidType : String = ""

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

        //OPTION 1

        resolGp.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId==R.id.chk_240) {
                resolution = 240
            }else if(checkedId==R.id.chk_480) {
                resolution = 480
            }else if(checkedId==R.id.chk_720) {
                resolution = 720
            }else if(checkedId==R.id.chk_1080) {
                resolution = 1080
            }
        }
        TypeGp.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId==R.id.chk_dynamic) {
                vidType = "dynamic"
                var playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("value", resolution)
                playerIntent.putExtra("type", vidType)
                btn_start.setOnClickListener{
                    setName()
                    startActivity(playerIntent)
                }
            }else if(checkedId==R.id.chk_static) {
                vidType = "static"
                var playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("value", resolution)
                playerIntent.putExtra("type", vidType)
                btn_start.setOnClickListener {
                    setName()
                    startActivity(playerIntent)
                }
            }
        }

    }
    fun setName(){
        name = name3.text.toString()
    }
    fun retName(): String {
        return name
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