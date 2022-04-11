package com.example.expapp.fileUpload;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import com.example.expapp.PlayerActivity;
import com.example.expapp.R;

public class ConnectFTP extends Activity{

    static final String FTP_HOST= "115.85.180.227";
    static final String FTP_USER = "ftpuser";
    static final String FTP_PASS  ="ftpuser";

    Handler handler = new Handler();
    PlayerActivity mainActivity;

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
            File accFile = new File(android.os.Environment.getExternalStorageDirectory() + mainActivity.getACCELEROMETER_SENSOR_FILE_NAME());
            uploadFile(accFile);
        }
    }

    public void uploadFile(File fileName){
        FTPClient client = new FTPClient();
        try {
            client.connect(FTP_HOST,21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);

            client.upload(fileName, new MyTransferListener());//업로드 시작
            handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println(" Success");
                }
            });
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println(" Failed");
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
                    //Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
                    System.out.println(" Upload Started ...");
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
                    System.out.println(" transferred ..." + length);
                }
            });
        }

        public void completed() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println(" completed ..." );
                }
            });
        }

        public void aborted() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println(" aborted ..." );
                }
            });
        }

        public void failed() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println(" failed ..." );
                }
            });
        }

    }
}
