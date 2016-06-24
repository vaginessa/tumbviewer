package com.nutrition.express.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by huang on 5/18/16.
 */
public class HistoryUtils {
    private static ArrayList<String> histories;

    public static void add(Context context, String string) {
        if (histories == null) {
            histories = getHistories(context);
        }
        if (!histories.contains(string)) {
            histories.add(string);
            storeHistories(context);
        }
    }

    public static void remove(Context context, String string) {
        if (histories == null) {
            histories = getHistories(context);
        }
        histories.remove(string);
        storeHistories(context);
    }

    public static ArrayList<String> getHistories(Context context) {
        File filesDir = context.getFilesDir();
        File history = new File(filesDir, "blog_names.json");
        if (history.exists()) {
            try {
                Scanner scanner = new Scanner(history);
                String contents = scanner.useDelimiter("\\Z").next();
                histories = new Gson().fromJson(contents, new TypeToken<List<String>>(){}.getType());
                Collections.reverse(histories);
                scanner.close();
            } catch (FileNotFoundException e) {
            }
        } else {
            histories = new ArrayList<>();
        }
        return histories;
    }

    private static void storeHistories(Context context) {
        File filesDir = context.getFilesDir();
        File history = new File(filesDir, "blog_names.json");
        try {
            String contents = new Gson().toJson(histories);
            FileOutputStream outputStream = new FileOutputStream(history);
            outputStream.write(contents.getBytes());
        } catch (IOException e) {
        }
    }
}
