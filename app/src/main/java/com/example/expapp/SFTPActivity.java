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
        String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "Proc-Type: 4,ENCRYPTED\n" +
                "DEK-Info: DES-EDE3-CBC,9A3639918F48AFF8\n" +
                "\n" +
                "ucweG9othtnzGlilwsO2WImSi7O5INNe8ZjHMpJFdlie4ZFqTpLxdDWByKYTQ39d\n" +
                "nlfOJaYFg4vq3dZYda4htijAenux3Yp/47lRpMjfoG4PgCSO/dLAgl9Rl9OUkWAR\n" +
                "QvE39JopgPksWhWN99pfVncm9+nUWU4D14rLik4gqQeSf4zr9LwaBe4f3KpJMg9o\n" +
                "KKRlysHfgWvYM1qMI3p0pi4cqfz8AAu7w7TT/MWbX6KeTGpWcMSiTdZFJcYwExfz\n" +
                "4Fbj3edcehSts4Aq4k7GJ+xVLLrwsQ4shyeO0iWvGL3mcQXCnaVdGz+wcpT9zNVC\n" +
                "5y87ivUufgBG5iAdJCQTJDpIQ0NA+Lym/H8H/oZ4u/yHXcSPtruFcSFEO39dt8al\n" +
                "UdAc+dBMtL7n8I00ISXO/oF7xCiTFzUWaoLMFjBMuKW9k0GEu7Xt6LOkbNJl0nKZ\n" +
                "kSH5QoHJq7k5ufQvAF8bJDBZCOrCeBy3SEbIODIZt6fClYY3R00Ac5U+YSw4Vfv9\n" +
                "GxN4HMJbNtfI+i9M4xwiRsSIvVwi7Lnklx8d4eWd3D39iZn28j9HYFtw1pZZe934\n" +
                "wH0cCQALyQEN7/jh+oDxLMhyOYkf+beLNt2kaRYLYF/2SK3WkQPS3rOhy33CI+zU\n" +
                "nIk40ZfOXsDV1CMR9WyEUWkdfxxhBaSe9PFenFZlYTokTCMOqG7Br0RTdPD6O1mp\n" +
                "BCCmNGcpC57XGeF/HO6Pql2CuTe60MF2k4QVr6UAq4mszTpSimZ14Id0DH/lMOUI\n" +
                "r5CSkY1w4XIlClPeHHsaxRa0df91R6nVXgpPiRcQZCirP+JmGkatChy0+nHehBN0\n" +
                "y0Bv/kjbODrnkRyMIto1/8ihO74MpYm7BmJi66jeSFS8FHip6hYwrb0J3eEHYsa1\n" +
                "/THSFR/SxfhwDeC68CYHcuQp8RLiwg5arfpWsbtiCnKKS1l8sJWwF58iCpywe+qd\n" +
                "xBySERByC8m4aQlyxjf0BY9Jba54k97OO+PTWFcvhZi/uCStorjYymhtybH1motM\n" +
                "9imB3pBovsMdR06tk2jGsICO2wcOaHsHomdedwn2GTPPS53r3yLSDuZK0pl/gnzH\n" +
                "dxBT9OA2PbL3PnQhgtiYNDIn3E0GPTngvJlR1VGYDCSMcaQ4mZ+tvJ6EeRlBznzu\n" +
                "DTviKLwKZt+IebHy7q5quWfo9x7Cj6zA4xykM8fDdRy6BhqQQHxUQ4zH/XWLlHNh\n" +
                "S1xGXp7mig+xz70onbzZMSS6NgEMXyfpaTPpwnlidypHJ13bN+1mBwMx1yGuoTPc\n" +
                "OaqHoOiE0mcLgPYkGzjUg0W9WSQ6OhBTMwTzFNtIIa3oGtiViwVdJxPHPDq6tZOb\n" +
                "UIAj7KSVmb/EtnQVirOFFQ18byyo+4DtCOAYrZ+Pk7U/3DJrRFQajJtCfxR+suR6\n" +
                "EyawLEunUNeSJ3MHWghqwtMu65M1UjjaxeFVwcytVdHrSNGcg+X0wSwC3xU26A47\n" +
                "4UkGXRlAM2yEnqO+dpznbEDU2g11ZWTqMCgKvnZLHma6tbaEH15+QN/kbYfW2TV1\n" +
                "J411T+N0CdBM/1F5mWGiJIymxSSgUufvu68Wz4yzes3d7sMfO27xHw==\n" +
                "-----END RSA PRIVATE KEY-----";
        String publicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDy9G7WCnZkX3hej" +
                "F/FPHwKFNII6qlVRb4pGuT/v+Na8DNBYEKgX6D6MAsVxha5rsJA/KNt8edB7bKda" +
                "NdaXJ7WZKHW23iXPaSoj4sihSERH7cOjUSvKrAt1jSZTO8y9U9W1mjih9/RcOau6" +
                "DlVgN590TdFFoRrpeZevzuWnKkxglg/oX/f6yMwMS08ERK7bHmsZeRBiBI9mBBEo" +
                "Ky7xdH2m8+jpesRsDJbHsjgs+LHmcJOqb6FcknjhUMtFJtdQmhA2LXG+hPgwbpt3" +
                "A0+TaT1BUopOnz+hz5s0wx6FlloVL4K/qEA0J2owBgSzlHUIbSngKn+7gQw2AmSP" +
                "pTrXMFp youngchanlim95";

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
