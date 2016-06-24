package com.nutrition.express.main;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nutrition.express.R;
import com.nutrition.express.util.HistoryUtils;

import java.util.List;

/**
 * Created by huang on 6/9/16.
 */
public class MainRVAdapter extends RecyclerView.Adapter<SimpleTextVH> {
    private MainActivity mainActivity;
    private List<String> strings;

    public MainRVAdapter(MainActivity mainActivity, List<String> strings) {
        this.mainActivity = mainActivity;
        this.strings = strings;
    }

    @Override
    public SimpleTextVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text, parent, false);
        return new SimpleTextVH(view, onTextClickListener);
    }

    @Override
    public void onBindViewHolder(SimpleTextVH holder, int position) {
        holder.setText(strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    private SimpleTextVH.OnTextClickListener onTextClickListener =
            new SimpleTextVH.OnTextClickListener() {
                @Override
                public void onTextClick(String text) {
                    mainActivity.openPostsVideo(text);
                }
                @Override
                public void onLongTextClick(String text, int position) {
                    showDeleteDialog(text, position);
                }
            };

    private void showDeleteDialog(final  String text, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setMessage("删除记录" + text + "?");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HistoryUtils.remove(mainActivity, text);
                notifyItemRemoved(position);
            }
        });
        builder.create().show();
    }

}
