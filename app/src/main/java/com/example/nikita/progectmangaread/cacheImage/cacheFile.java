package com.example.nikita.progectmangaread.cacheImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.nikita.progectmangaread.AsyncTaskLisen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nikita on 13.03.2016.
 * Class work with cache
 * Класс работы с кэшем
 */
public class cacheFile {
    File dirFile;
    AsyncTaskLisen as;

    public cacheFile(File dirFile, String nameDir,AsyncTaskLisen as){
        this.dirFile = new File(dirFile,nameDir);
        if (!this.dirFile.exists()){
            this.dirFile.mkdir();
        }
        this.as = as;
    }
    public cacheFile(File dirFile, String nameDir){
        this.dirFile = new File(dirFile,nameDir);
        if (!this.dirFile.exists()){
            this.dirFile.mkdir();
        }
        this.as = null;
    }
    //download image and cache it
    public void loadAndCache(String url, int number){
        class downlandImage extends AsyncTask<String,Void,Void> {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    //продумать поименование файлов
                    File f = new File(dirFile, params[1]);
                    if (!f.exists()){
                        URL imageUrl = new URL(params[0]);
                        HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                        conn.setConnectTimeout(30000);
                        conn.setReadTimeout(30000);
                        conn.setInstanceFollowRedirects(true);
                        InputStream is=conn.getInputStream();

                        if (params[0].contains("gif")){
                            Bitmap bmp = BitmapFactory.decodeStream(is);
                            FileOutputStream  out = new FileOutputStream(f);
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                        }else {
                            OutputStream os = new FileOutputStream(f);
                            CopyStream(is, os);
                            os.close();
                        }
                        conn.disconnect();
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            //Copy inputStream in OutputStream
            private void CopyStream(InputStream is, OutputStream os)
            {
                final int buffer_size=1024*40;
                try
                {
                    byte[] bytes=new byte[buffer_size];
                    for(;;)
                    {
                        int count=is.read(bytes, 0, buffer_size);
                        if(count==-1)
                            break;
                        os.write(bytes, 0, count);
                    }
                }
                catch(Exception ex){}
            }

            @Override
            protected void onPostExecute(Void result){
              as.onEnd();
            }
        }

        downlandImage Task = new downlandImage();
        Task.execute(url, String.valueOf(number));
    }

    public String getFile(String nameCache) throws FileNotFoundException {
        File f = new File(dirFile, nameCache);
        //InputStream in = new FileInputStream(f);
        return f.getPath();
    }

    public void clearCache(){
        File[] files=dirFile.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
}
