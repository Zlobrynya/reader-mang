package com.example.nikita.progectmangaread.decode;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.decoder.ImageDecoder;

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
        bitmap = BitmapDecoder.from(context, uri).useBuiltInDecoder(false).config(Bitmap.Config.RGB_565).decode();
        Log.i("ImageDecoder", String.valueOf(bitmap.getHeight()));
        return bitmap;
    }

}