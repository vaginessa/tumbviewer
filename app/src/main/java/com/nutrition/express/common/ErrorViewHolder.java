package com.nutrition.express.common;

import android.view.View;
import android.widget.TextView;

import com.nutrition.express.R;

/**
 * Created by huang on 11/9/16.
 */

public class ErrorViewHolder extends CommonViewHolder<String> {
    public ErrorViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(String s) {
        super.bindView(s);
        if (itemView instanceof TextView) {
            s = s == null ? itemView.getContext().getString(R.string.load_failed) : s;
            ((TextView) itemView).setText(s);
        }
    }
}
