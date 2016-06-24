package com.nutrition.express.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.TypedValue;
import android.widget.Toast;

/**
 * Created by huang on 5/16/16.
 */
public class Utils {
    public static float dp2Pixels(Context context, int pixels) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels,
                context.getResources().getDisplayMetrics());
    }

    public static void copy2Clipboard(Context context, String string) {
        ClipboardManager clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Tumblr", string);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }
}
