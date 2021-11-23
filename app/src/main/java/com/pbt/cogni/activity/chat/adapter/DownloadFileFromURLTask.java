package com.pbt.cogni.activity.chat.adapter;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

class DownloadFileFromURfLTask extends
        AsyncTask<String, String, String> {

    /**
     * Downloading file in background thread
     * */
    @SuppressLint("SdCardPath")
    @Override
    protected String doInBackground(String... f_url) {
        Log.d("##ChatAdapter", "do in background");
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);
            File myObj = new File(Environment.getExternalStorageDirectory()+"/"+"filename.txt");
            // Output stream to write file
            OutputStream output = new FileOutputStream(myObj.toString());

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage

        Log.d("##DownloadFile",progress.toString());




    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/

    @Override
    protected void onPostExecute(String file_url) {
        Log.i("##ChatAdapter", "on post excute!");


    }


}
