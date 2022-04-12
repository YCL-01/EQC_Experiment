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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import com.example.expapp.SurveyActivity;
import com.example.expapp.R;

import java.util.Random;

public class FTPActivity extends Activity{

    SurveyActivity mainActivity;
    PlayerActivity PlayerActivity;

    static final String FTP_HOST= "115.85.180.227";
    static final String FTP_USER = "ftpuser";
    static final String FTP_PASS  ="ftpuser";
    String TAG="FTPLog";
    String accFileName = "acc.csv";
    String gyroFileName = "gyro.csv";
    String lightFileName = "light.csv";
    String surveyFileName = "surveyData.txt";


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
            try {
                upload();
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FTPException e) {
                e.printStackTrace();
            }
        }
        public void upload() throws FTPIllegalReplyException, IOException, FTPException {

            int tmp = (int)(Math.random()*2100000000);
            String dirName = Integer.toString(tmp);

            FTPClient client = new FTPClient();
            client.connect(FTP_HOST,21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.setPassive(true);

            Log.d(TAG,"log-in");
            client.changeDirectory("/home/ftpuser");
            client.createDirectory(dirName);
            dirName = "/home/ftpuser/"+dirName;
            client.changeDirectory(dirName);

            File accFile = new File("/data/data/com.example.expapp/files/", accFileName);
            Log.d(TAG, accFile.toString());
            uploadFile(client, accFile, dirName);

            File gyroFile = new File("/data/data/com.example.expapp/files/", gyroFileName);
            Log.d(TAG, gyroFile.toString());
            uploadFile(client, gyroFile, dirName);

            File lightFile = new File("/data/data/com.example.expapp/files/", lightFileName);
            Log.d(TAG, lightFile.toString());
            uploadFile(client, lightFile, dirName);

            File surveyFile = new File("/data/user/0/com.example.expapp/files/", surveyFileName);
            Log.d(TAG, surveyFile.toString());
            uploadFile(client, surveyFile, dirName);
        }
    }

    public void uploadFile(FTPClient client, File file, String dirName){
        try {
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
                    Log.d(TAG, " Failed");
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
