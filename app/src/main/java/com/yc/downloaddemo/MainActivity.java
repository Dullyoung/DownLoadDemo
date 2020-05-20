package com.yc.downloaddemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (checkSelfPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
        }
        findViewById(R.id.b1).setOnClickListener(v -> {
            down();
        });
        findViewById(R.id.b2).setOnClickListener(v -> {
            cancel(id);
        });
        button = findViewById(R.id.b1);
    }

    Button button;
    long id = 0;
    String TAG = "aaaa";


    void down() {
//检测是否有安装应用权限，没有的话跳转过去。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                Toast.makeText(this, "请允许本应用的安装权限", Toast.LENGTH_SHORT).show();
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                button.postDelayed(() -> startActivity(intent), 1000);

            }
        }

        String url = "https://down.qq.com/qqweb/QQlite/Android_apk/qqlite_4.0.1.1060_537064364.apk";
        
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("王者农药");
        request.setDescription(" 一起来！");
        request.setMimeType("application/vnd.android.package-archive");
        // 下载过程和下载完成后通知栏有通知消息。
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 指定下载文件地址
        File f = new File(getExternalCacheDir() + "/wzry.apk");

        //这里的URI只认file：// 7.0以上用ContentProvider返回的都是content：//  所以 自己拼
        request.setDestinationUri(Uri.parse("file://" + f.getAbsolutePath()));
        request.allowScanningByMediaScanner();
        id = downloadManager.enqueue(request);
    }

    void cancel(long a) {


        downloadManager.remove(a);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage(String s) {
        if (s.equals("complete")) {
            install();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void install() {
        File f = new File(getExternalCacheDir() + "/wzry.apk");
        Intent install = new Intent();
        install.setAction(Intent.ACTION_VIEW);
        install.addCategory(Intent.CATEGORY_DEFAULT);
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //安装需要传文件的uri，7.0用FileProvider获取。
        //8.0需要额外管理允许本应用安装应用程序 在上面down的时候检测安装权限 没权限可能不会跳转到安装页
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", f);//在AndroidManifest中的android:authorities值
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            install.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
        }

        startActivity(install);

    }


}
