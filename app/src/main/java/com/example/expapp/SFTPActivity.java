package com.example.expapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPActivity extends Activity {

    static final String HOST = "";
    static final String USER = "";
    static final String PASS = "*";
    static final int PORT = 22;

    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    String TAG = "SFTPLog";

    String accFileName = "acc.csv";
    String gyroFileName = "gyro.csv";
    String lightFileName = "light.csv";
    String surveyFileName = "surveyData.txt";
    String userName = null;
    String time = null;
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String playerName = MainActivity.Companion.getUserName();
        String timeline = MainActivity.Companion.getLight();

        System.out.println("playerName : " + playerName);
        userName = playerName;
        time = timeline;

        Thread uploading = new Thread(this::uploadFile);
        uploading.start();

        try {
            uploading.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (MainActivity.Companion.getTrial() >= MainActivity.Companion.getTotal()) {
            moveTaskToBack(true);
            finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        intent = new Intent(this, SubActivity.class);
        intent.putExtra("userName", playerName);
        startActivity(intent);
    }

    public void uploadFile() {
        String dirName = (MainActivity.Companion.getUserName()) + "_" + (MainActivity.Companion.getLight()) + "_" + (MainActivity.Companion.getTrial());
        String privateKey = "";
        String publicKey = "";

        JSch jsch = new JSch();
        try {
            jsch.addIdentity(USER, privateKey.getBytes(),publicKey.getBytes(),PASS.getBytes());
            session = jsch.getSession(USER, HOST, PORT);
            session.setPassword(PASS);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            System.out.println("\n\n\nStart Connection");
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("End Connection\n\n\n");

        } catch (JSchException e) {
            e.printStackTrace();
        }

        channelSftp = (ChannelSftp) channel;
        FileInputStream in = null;
        String fileName = "";
        boolean result = true;
        String files[] = {accFileName, gyroFileName, lightFileName, surveyFileName};

        try {
            channelSftp.cd("ftp");
            channelSftp.mkdir(dirName);
            channelSftp.cd(dirName);
            for (int i = 0; i < 4; i++) {
                File uploadFile = new File("/data/data/com.example.expapp/files/", files[i]);
                fileName = uploadFile.getName();
                in = new FileInputStream(uploadFile);
                channelSftp.put(in, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        channelSftp.quit();
    }
}
