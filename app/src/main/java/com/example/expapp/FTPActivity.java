package com.example.expapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class FTPActivity extends Activity{

    static final String FTP_HOST = 
    static final String FTP_USER = 
    static final String FTP_PASS = 
    static final int FTP_PORT = 

    String TAG="FTPLog";

    String accFileName = "acc.csv";
    String gyroFileName = "gyro.csv";
    String lightFileName = "light.csv";
    String surveyFileName = "surveyData.txt";
    String userName = null;
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String playerName = MainActivity.Companion.getUserName();

        System.out.println("playerName : "+playerName);
        userName = playerName;

        NThread nThread = new NThread();
        nThread.start();
        try {
            nThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(MainActivity.Companion.getTrial()>=MainActivity.Companion.getTotal())
        {
            moveTaskToBack(true);
            finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        intent = new Intent(this, SubActivity.class);
        intent.putExtra("userName", playerName);
        startActivity(intent);
        finish();
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
            String files[] = {accFileName,gyroFileName,lightFileName,surveyFileName};
            String dirName = userName+"_"+(MainActivity.Companion.getTrial());

            FTPClient client = new FTPClient();
            client.connect(FTP_HOST,FTP_PORT);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.setPassive(true);

            Log.d(TAG,"log-in");
            client.changeDirectory("ftp/upload");
            try{
                client.createDirectory(dirName);
            } catch(Exception e){

            }
            client.changeDirectory(dirName);
            System.out.println( "dirName " + dirName);

            for(int i = 0; i<4; i++)
            {
                File accFile = new File("/data/data/com.example.expapp/files/", files[i]);
                Log.d(TAG, accFile.toString());
                uploadFile(client, accFile);
            }
            client.disconnect(true);

        }
    }

    public void uploadFile(FTPClient client, File file){
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
