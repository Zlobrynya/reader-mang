package com.zlobrynya.project.readermang.decode;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.decoder.ImageDecoder;

import org.greenrobot.eventbus.EventBus;
import magick.ColorspaceType;
import magick.ImageInfo;
import magick.MagickImage;
import magick.util.MagickBitmap;
import rapid.decoder.BitmapDecoder;

/**
 * A very simple implementation of {@link com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder}
 * using the RapidDecoder library (https://github.com/suckgamony/RapidDecoder). For PNGs, this can
 * give more reliable decoding and better performance. For JPGs, it is slower and can run out of
 * memory with large images, but has better support for grayscale and CMYK images.
 *
 */
public class MyImageDecoder implements ImageDecoder {

    @Override
    public Bitmap decode(Context context, Uri uri) throws Exception {
        Bitmap bitmap;
        Log.i("Decode: ","Decode norm");
        bitmap = BitmapDecoder.from(context, uri).useBuiltInDecoder(false).config(Bitmap.Config.RGB_565).decode();
      //  String[] stringss = uri.getPath().split("/");
      //  Log.i("DecideFail",stringss[stringss.length-1]);

        if (bitmap == null){
            try {
                Log.i("Decode: ","Decode magic");
                ImageInfo info = new ImageInfo(uri.getPath());
                MagickImage image = new MagickImage(info);

                if(image.getColorspace() == ColorspaceType.CMYKColorspace) {
                    image.transformRgbImage(ColorspaceType.CMYKColorspace);
                }

                bitmap = BitmapDecoder.from(MagickBitmap.ToBitmap(image)).useBuiltInDecoder(false).config(Bitmap.Config.RGB_565).decode();

            } catch (Exception e) {
                //Полсылаем сигнал, что декодирование сфейлилось и надо бы перезаписать
                String[] strings = uri.getPath().split("/");
                EventBus.getDefault().post("DecodeFail/"+strings[strings.length-1]);
                Log.i("DecideFail","PostEvent");
            }
        }
        //Log.i("ImageDecoder", String.valueOf(bitmap.getHeight()));
        return bitmap;
    }

}