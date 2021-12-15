package com.example.expapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.app.Activity
import java.util.Random




// Main - Player(+Listener) - Info

class MainActivity : AppCompatActivity() {

    //Sample video
    private var resolution : Int = 0

    //Check Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE: Int = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyStoragePermissions(this)
        //OPTION 1
        /*
        resolGp.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId==R.id.chk_240) {
                resolution = 240
                var playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("value", resolution)
                btn_start.setOnClickListener{ startActivity(playerIntent) }
            }else if(checkedId==R.id.chk_480) {
                resolution = 480
                var playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("value", resolution)
                btn_start.setOnClickListener{ startActivity(playerIntent) }
            }else if(checkedId==R.id.chk_720) {
                resolution = 720
                var playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("value", resolution)
                btn_start.setOnClickListener{ startActivity(playerIntent) }
            }else if(checkedId==R.id.chk_1080) {
                resolution = 1080
                var playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("value", resolution)
                btn_start.setOnClickListener{ startActivity(playerIntent) }
            }
        }
        */
        //OPTION 2
        val res_candidate = listOf(240, 480, 720, 1080)
        val random = Random()
        val num = random.nextInt(3)
        resolution = res_candidate[num]
        var playerIntent = Intent(this, PlayerActivity::class.java)
        playerIntent.putExtra("value", resolution)
        btn_start.setOnClickListener{ startActivity(playerIntent) }
    }

    fun verifyStoragePermissions(activity: Activity?) {
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

}