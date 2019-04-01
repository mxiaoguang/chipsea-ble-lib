package com.mchen.myapplication.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.chipsea.utils.L;
import com.mchen.myapplication.dialog.SaveProgressDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



/**
 * Created by Suzy on 2016/7/26.
 */
public class SaveDataTask extends AsyncTask<Void, Integer, String> {
    private final static String TAG = "SaveDataTask";

    private Context context;
    private Cursor cursor;

    private String fileName;

    private OnSaveDataListener listener;

    public interface OnSaveDataListener {
        void onProgressChanged(int progress);

        void onSaveState(String result);
    }

    public void setOnSaveDataListener(OnSaveDataListener listener) {
        this.listener = listener;
    }

    public SaveDataTask(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.context = context;
        fileName = "WeightDemo.csv";
    }

    @Override
    protected String doInBackground(Void... voids) {
        int rowCount;
        int colCount;
        FileWriter fw;
        BufferedWriter bfw;
        File saveFile = new File(Configs.getSDPath(), fileName);
        Configs.makeDir(saveFile);
        switch (Configs.deleteFile(Configs.getSDPath() + fileName)) {
            case 0:
                L.e("该文件不存在");
                break;
            case 1:
                L.e("文件删除成功：" + Configs.getSDPath() + fileName);
                break;
            case 2:
                L.e("文件删除失败：" + Configs.getSDPath() + fileName);
                break;
        }
        try {
            rowCount = this.cursor.getCount();
            colCount = this.cursor.getColumnCount();
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            if (rowCount > 0) {
                this.cursor.moveToFirst();
                // 写入表头
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bfw.write(this.cursor.getColumnName(i) + ',');
                    else
                        bfw.write(this.cursor.getColumnName(i));
                }
                // 写好表头后换行
                bfw.newLine();
                // 写入数据
                for (int i = 0; i < rowCount; i++) {
                    this.cursor.moveToPosition(i);
                    publishProgress((int) (((i + 1) / (float) rowCount) * 100));
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1) {
                            String str = this.cursor.getString(j) + ",";
                            bfw.write(str);
                        } else {
                            String str = this.cursor.getString(j);
                            bfw.write(str);
                        }
                    }
                    // 写好每条记录后换行
                    bfw.newLine();
                }
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            return saveFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            this.cursor.close();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        L.e(TAG, "onCancelled");
        Configs.deleteFile(Configs.getSDPath() + fileName);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (TextUtils.isEmpty(s)) {
            L.e(TAG, "保存失败");
        } else {
            L.e(TAG, "保存成功，s = " + s);
        }
        listener.onSaveState(s);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        new SaveProgressDialog(context, this).show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        L.e(TAG, "value = " + values[0]);
        /*for (Integer value : values) {
            L.e(TAG, "value = " + value);
        }*/
        listener.onProgressChanged(values[0]);
    }
}
