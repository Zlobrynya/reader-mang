package com.example.nikita.progectmangaread.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nikita on 03.08.2016.
 */
public class MoveFile extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String out = intent.getStringExtra("out");
        String inp = intent.getStringExtra("inp");
        AsyncMoveFile Task = new AsyncMoveFile();
        Task.execute(out, inp);
        return super.onStartCommand(intent, flags, startId);
    }

    public class AsyncMoveFile extends AsyncTask<String,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        //params[0] - output, params[1]-input
        protected Void doInBackground(String... params) {
            File internalPath = new File(params[0]);
            File[] dirs= internalPath.listFiles();
            for(File dir: dirs){
                File newDir = new File(params[1]+"/"+dir.getName());
                if (!newDir.exists()){
                    newDir.mkdir();
                }
                File chapters[] = dir.listFiles();
                for(File chapter: chapters){
                    File newChapter = new File(params[1]+"/"+dir.getName()+"/"+chapter.getName());
                    if (!newChapter.exists()){
                        newChapter.mkdir();
                    }
                    File imgs[] = chapter.listFiles();
                    try{
                        for(File img: imgs) {
                            moveFile(img.getPath(),newChapter.getPath()+"/"+img.getName());
                            img.delete();
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    chapter.delete();
                }
                dir.delete();
            }
            return null;
        }

        private void moveFile(String inputPath, String outputPath) {
            InputStream in = null;
            OutputStream out = null;
            try {

                in = new FileInputStream(inputPath);
                out = new FileOutputStream(outputPath);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file
                out.flush();
                out.close();
                out = null;
            }
            catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result){

        }
    }

}