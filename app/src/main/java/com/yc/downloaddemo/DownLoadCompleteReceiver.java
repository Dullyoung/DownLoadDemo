package com.yc.downloaddemo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.EventLog;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by YuShuangPing on 2018/9/4.
 */
public class DownLoadCompleteReceiver extends BroadcastReceiver {
    String TAG="aaaa";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            //在广播中取出下载任务的id
            long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(myDwonloadID);

            Cursor cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                int reason = cursor.getInt(columnReason);

                switch (status) {
                    case DownloadManager.STATUS_FAILED:
                        Log.i(TAG, "STATUS_FAILED: ");
                        break;
                    case DownloadManager.STATUS_PAUSED:
                        Log.i(TAG, "STATUS_PAUSED: ");
                        break;
                    case DownloadManager.STATUS_PENDING:
                        Log.i(TAG, "STATUS_PENDING: ");
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        Log.i(TAG, "STATUS_RUNNING: ");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        Log.i(TAG, "STATUS_SUCCESSFUL: ");
                        EventBus.getDefault().post("complete");
                        break;
                }


            }
        }
    }
}
