package com.example.expapp.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.example.expapp.PlayerActivity;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AccelerometerListener implements SensorEventListener {

    String TAG="accelerometerLog";
    PlayerActivity mainActivity;

    public AccelerometerListener(PlayerActivity mainActivity)
    {
        this.mainActivity=mainActivity;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)  {
        //Log.d(TAG, "acceleration change: X:"+sensorEvent.values[0]+"  Y:"+sensorEvent.values[1]+"Z:  "+sensorEvent.values[2]);
        if(mainActivity.isHasStartedWriting())
        {
            String baseDir = "/data/data/com.example.expapp/files";
            String fileName = mainActivity.getACCELEROMETER_SENSOR_FILE_NAME();
            String filePath = baseDir + File.separator + fileName;
            Log.d(TAG,filePath.toString());

            File file = new File(filePath);
            CSVWriter writer;
            FileWriter mFileWriter;
            String[] entries = "time,x,y,z".split(",");

            try{
                // File exist
                if(file.exists()&&!file.isDirectory())
                {
                    mFileWriter = new FileWriter(filePath, true);
                    writer = new CSVWriter(mFileWriter);
                }
                else
                {
                    file.createNewFile();
                    writer = new CSVWriter(new FileWriter(filePath));
                    writer.writeNext(entries);
                }

                float[] sensorValues = (sensorEvent.values);
                String[] data=new String[sensorValues.length+1];
                SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
                jdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
                data[0] = jdf.format(new Date(System.currentTimeMillis()));
                for(int i=0;i<sensorValues.length;i++)
                {
                    data[i+1]=String.valueOf(sensorValues[i]);
                }
                writer.writeNext(data);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
