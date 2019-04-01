package com.mchen.myapplication.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mchen.myapplication.R;
import com.mchen.myapplication.utils.SaveDataTask;


/**
 * Created by Suzy on 2016/2/22.
 */
public class SaveProgressDialog extends Dialog implements View.OnClickListener, SaveDataTask.OnSaveDataListener {

    private Context context;
    private SaveDataTask task;
    private ProgressBar pb_save_progress;
    private TextView tv_save_state;
    private Button btn_cancel;

    private boolean isSaveSuccess = false;

    public SaveProgressDialog(Context context, SaveDataTask task) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.task = task;
        task.setOnSaveDataListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_save_progress);
        initViews();
        initEvents();
    }

    private void initEvents() {
        btn_cancel.setOnClickListener(this);
    }

    private void initViews() {
        pb_save_progress = (ProgressBar) findViewById(R.id.pb_save_progress);
        tv_save_state = (TextView) findViewById(R.id.tv_save_state);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        tv_save_state.setText(R.string.save_ing);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                if (!isSaveSuccess) {
                    task.cancel(true);
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        pb_save_progress.setProgress(progress);
        tv_save_state.setText(String.format(context.getResources().getString(R.string.save_ing_progress), progress) + " %");
    }

    @Override
    public void onSaveState(String result) {
        if (TextUtils.isEmpty(result)) {
            tv_save_state.setText(R.string.save_failed);
        }else {
            tv_save_state.setText(String.format(context.getResources().getString(R.string.save_success), result.split("/")[result.split("/").length - 1]));
            btn_cancel.setText(R.string.query);
            isSaveSuccess = true;
        }
    }
}
