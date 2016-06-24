package com.nutrition.express.videolist;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import com.nutrition.express.R;
import com.nutrition.express.application.SystemDownload;
import com.nutrition.express.util.Utils;

/**
 * Created by huang on 5/30/16.
 */
public class VideoBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    BottomSheetBehavior behavior;
    private String url;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        url = bundle.getString("url");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.bottom_sheet_video_list, null);
        TextView textView = (TextView) view.findViewById(R.id.item_link);
        textView.setText(url);
        textView.setOnClickListener(this);
        view.findViewById(R.id.item_download).setOnClickListener(this);
        view.findViewById(R.id.item_copy).setOnClickListener(this);
        dialog.setContentView(view);
        behavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_download:
                SystemDownload.downloadVideo(getActivity(), url);
                break;
            case R.id.item_copy:
                Utils.copy2Clipboard(getActivity(), url);
                break;
            case R.id.item_link:
                break;
        }
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
