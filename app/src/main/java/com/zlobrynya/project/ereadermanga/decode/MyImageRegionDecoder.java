package com.zlobrynya.project.ereadermanga.decode;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder;

import rapid.decoder.BitmapDecoder;

/**
 * Created by Nikita on 01.04.2016.
 */
public class MyImageRegionDecoder implements ImageRegionDecoder {

    private BitmapDecoder decoder;

    @Override
    public Point init(Context context, Uri uri) throws Exception {
        decoder = BitmapDecoder.from(context, uri);
        decoder.useBuiltInDecoder(true);
        return new Point(decoder.sourceWidth(), decoder.sourceHeight());
    }

    @Override
    public synchronized Bitmap decodeRegion(Rect sRect, int sampleSize) {
        try {
            Log.i("ImageDecoderRegion ", String.valueOf(sampleSize));
            decoder.reset()
                    .region(sRect)
                    .scale(sRect.width()/sampleSize, sRect.height()/sampleSize);
            return decoder.decode();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isReady() {
        return decoder != null;
    }

    @Override
    public void recycle() {
        BitmapDecoder.destroyMemoryCache();
        BitmapDecoder.destroyDiskCache();
        decoder.reset();
        decoder = null;
    }
}