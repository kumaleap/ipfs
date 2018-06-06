package com.zoke.ipfs.demo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zoke.ipfs.api.IPFS;
import com.zoke.ipfs.http.ProgressCallback;
import com.zoke.ipfs.http.SimpleCallback;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final int PHOTO_PICKED_WITH_DATA = 100;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .start();
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPickPhotoFromGallery();
            }
        });

        findViewById(R.id.hash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("hash", "执行");
                String saveP = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
                IPFS.getInstance().download("Qmb6xCDFfMgjmM7s1SzvcSUDsaHBQZtyHucSRTqCcNakkz", saveP, new ProgressCallback<File>() {
                    @Override
                    public void onSuccess(File result) {

                    }

                    @Override
                    public void onError(String error) {

                    }

                    @Override
                    public void onWaiting() {

                    }

                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {

                    }
                });
            }
        });
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("text", "执行");
                IPFS.getInstance().postText("{''''  这是一段假的json 测试是否可以上传''''''}", new SimpleCallback() {
                    @Override
                    public void onSuccess(String result) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });
        findViewById(R.id.text_hash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("text_hash", "执行");
                IPFS.getInstance().getText("QmZzw2v8AyvRLUND6anEgxhzFGMcDyD2JJySP4DArEtVoD", new SimpleCallback() {
                    @Override
                    public void onSuccess(String result) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });


    }

    /**
     * 从相册选择图片
     */
    protected void doPickPhotoFromGallery() {
        try {
            final Intent intent = getPhotoPickIntent();
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "暂无图片",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取调用相册的Intent
     */
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_PICKED_WITH_DATA && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();
            IPFS.getInstance().postFile(path, new ProgressCallback<String>() {
                @Override
                public void onSuccess(String result) {

                }

                @Override
                public void onError(String error) {

                }

                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {

                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {

                }
            });
        }
    }
}
