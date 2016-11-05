package com.example.nikita.progectmangaread.decode;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.decoder.ImageDecoder;

import org.greenrobot.eventbus.EventBus;
import magick.ColorspaceType;
import magick.ImageInfo;
import magick.MagickException;
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
        if (bitmap == null){
            try {
                Log.i("Decode: ","Decode magic");
                ImageInfo info = new ImageInfo(uri.getPath());
                MagickImage image = new MagickImage(info);

                if(image.getColorspace() == ColorspaceType.CMYKColorspace) {
                    image.transformRgbImage(ColorspaceType.CMYKColorspace);
                }

                bitmap = BitmapDecoder.from(MagickBitmap.ToBitmap(image)).useBuiltInDecoder(false).config(Bitmap.Config.RGB_565).decode();

            } catch (MagickException e) {
                Log.e("MagickException", e.getMessage());
            } catch (NullPointerException e){
                //Полсылаем сигнал, что декодирование сфейлилось и надо бы перезаписать
                String[] strings = uri.getPath().split("/");
                EventBus.getDefault().post("DecodeFail/"+strings[strings.length]);
            }
        }
        //Log.i("ImageDecoder", String.valueOf(bitmap.getHeight()));
        return bitmap;
    }

}