package com.nutrition.express.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.TypedValue;
import android.widget.Toast;

import com.nutrition.express.R;

/**
 * Created by huang on 5/16/16.
 */
public class Utils {
    public static float dp2Pixels(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static void copy2Clipboard(Context context, String string) {
        ClipboardManager clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Tumblr", string);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, R.string.video_copy_url, Toast.LENGTH_SHORT).show();
    }
}
