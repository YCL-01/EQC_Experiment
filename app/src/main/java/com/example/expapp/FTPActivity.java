package com.example.expapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import com.example.expapp.SurveyActivity;
import com.example.expapp.R;


public class FTPActivity extends Activity{

    SurveyActivity mainActivity;

    static final String FTP_HOST= "115.85.180.227";
    static final String FTP_USER = "ftpuser";
    static final String FTP_PASS  ="ftpuser";
    String TAG="FTPLog";
    String fileName = "Tears_720_accelerometer_0.csv";

    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NThread nThread = new NThread();
        nThread.start();
    }

    class NThread extends Thread{
        public NThread() {
        }
        @Override
        public void run() {
            upload();
        }
        public void upload(){
            /*
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "Tears_720_accelerometer_0.csv";
            String filePath = baseDir + File.separator + fileName;
            filePath = "/data/data/com.example.expapp/files/Tears_240_accelerometer_0.csv";
            File accFile = new File(filePath);
            */

            String fileName = "Tears_240_accelerometer_0.csv";
            File accFile = new File("/data/user/0/com.example.expapp/files/", fileName);
            Log.d(TAG, accFile.toString());
            uploadFile(accFile);
            fileName = "Tears_240_gyro_0.csv";
            accFile = new File("/data/user/0/com.example.expapp/files/", fileName);
            Log.d(TAG, accFile.toString());
            uploadFile(accFile);
            fileName = "Tears_240_light_0.csv";
            accFile = new File("/data/user/0/com.example.expapp/files/", fileName);
            Log.d(TAG, accFile.toString());
            uploadFile(accFile);
        }
    }

    public void uploadFile(File file){
        FTPClient client = new FTPClient();
        try {
            client.connect(FTP_HOST,21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.setPassive(true);
            Log.d(TAG,"log-in");
            client.changeDirectory("/home/ftpuser");
            client.upload(file, new MyTransferListener());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG," Success");
                }
            });
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG," Failed");
                }
            });
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

    /*******  Used to file upload and show progress  **********/
    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG," Upload Started ...");
                }
            });
        }

        public void transferred(int length) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    // Yet other length bytes has been transferred since the last time this
                    // method was called
                    //Toast.makeText(getBaseContext(), " transferred ...", Toast.LENGTH_SHORT).show();
                    Log.d(TAG," transferred ..." + length);
                }
            });
        }

        public void completed() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG," completed ..." );
                }
            });
        }

        public void aborted() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG," aborted ..." );
                }
            });
        }

        public void failed() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG," failed ..." );
                }
            });
        }

    }
}
