package com.example.expapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.app.Activity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import kotlin.properties.Delegates


// Main - Player(+Listener) - Info

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //Check Trial & Servey Data
    companion object{
        public var Trial: Int = 0
        var userName: String = ""
        var age: String? = null
        var sex: String? = null
        var Total: Int = 3 // 반복할 횟수
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

        val spinner = findViewById<View>(R.id.spinner) as Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.ages, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setOnItemSelectedListener(this)

        println("MainActivity Trial : "+ Trial)
        btn_start.setOnClickListener{
            userName = nameInput.text.toString()
            var playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra("name", userName)
            Trial++
            startActivity(playerIntent)
        }
    }
    fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.maleButton -> if (checked) sex = "Male"
            R.id.femalebutton -> if (checked) sex = "Female"
        }
        println("Sex: $sex")
    }
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        age = parent.getItemAtPosition(pos).toString()
        println("age: $age")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

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