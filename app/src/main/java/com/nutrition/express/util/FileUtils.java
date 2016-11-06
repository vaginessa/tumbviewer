package com.nutrition.express.util;

import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by huang on 11/6/16.
 */

public class FileUtils {
    public static File getVideoDir() {
        return new File(Environment.getExternalStorageDirectory(), "tumblr/video");
    }

    public static File getImageDir() {
        return new File(Environment.getExternalStorageDirectory(), "tumblr/image");
    }

    public static boolean imageSaved(Uri uri) {
        String name = uri.getLastPathSegment();
        if (null == name) {
            name = uri.toString();
        }
        return new File(FileUtils.getImageDir(), name).isFile();
    }

    public static File createImageFile(Uri uri) {
        File dir = getImageDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = uri.getLastPathSegment();
        if (null == name) {
            name = uri.toString();
        }
        return new File(FileUtils.getImageDir(), name);
    }

     public static boolean videoSaved(Uri uri) {
        String name = uri.getLastPathSegment();
        if (null == name) {
            name = uri.toString();
        }
        return new File(FileUtils.getVideoDir(), name).isFile();
    }

    public static File createVideoFile(Uri uri) {
        File dir = getImageDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = uri.getLastPathSegment();
        if (null == name) {
            name = uri.toString();
        }
        return new File(FileUtils.getVideoDir(), name);
    }

}
