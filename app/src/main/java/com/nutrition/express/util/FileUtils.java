package com.nutrition.express.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.nutrition.express.application.ExpressApplication;

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

    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            deleteDirFile(file);
        } else {
            if (file.delete()) {
                scanMedia(file);
            }
        }
    }

    private static void deleteDirFile(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                deleteDirFile(file);
            } else {
                if (file.delete()) {
                    scanMedia(file);
                }
            }
        }
        dir.delete();
    }

    private static void scanMedia(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        ExpressApplication.getApplication().sendBroadcast(mediaScanIntent);
    }

}
