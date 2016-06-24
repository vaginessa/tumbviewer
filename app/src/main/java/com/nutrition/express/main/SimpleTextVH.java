package com.nutrition.express.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nutrition.express.R;

/**
 * Created by huang on 6/9/16.
 */
public class SimpleTextVH extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener {
    private TextView textView;
    private OnTextClickListener listener;

    public SimpleTextVH(View itemView, OnTextClickListener listener) {
        super(itemView);
        this.listener = listener;
        textView = (TextView) itemView.findViewById(R.id.text);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.onTextClick(textView.getText().toString());
    }

    @Override
    public boolean onLongClick(View view) {
        listener.onLongTextClick(textView.getText().toString(), getAdapterPosition());
        return true;
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public interface OnTextClickListener {
        void onTextClick(String text);
        void onLongTextClick(String text, int position);
    }
}
