package com.yb.bsdiff;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yb.lib.bsdiff.BsdiffUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PERMISSION = 100;
    public static final int REQUEST_CODE_PATCH = 101;

    TextView mTvPatch;
    String patchPath;
    String oldPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView version = findViewById(R.id.version);
        version.setText("version: \n" + BuildConfig.VERSION_NAME);

        TextView old = findViewById(R.id.old_apk);
        oldPath = BsdiffUtil.getOldApkPath(this);
        old.setText("old apk: \n" + oldPath);

        TextView oldMD5 = findViewById(R.id.old_apk_md5);
        oldMD5.setText("old adk md5: \n" + BsdiffUtil.hash(oldPath));

        mTvPatch = findViewById(R.id.patch);
        patchPath = "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            if (REQUEST_CODE_PATCH == requestCode) {
                patchSelect(data);
            }
        }
    }

    //region 选择patch
    public void selectPatch(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        startActivityForResult(intent, REQUEST_CODE_PATCH);
    }

    private void patchSelect(Intent data) {
        patchPath = getRealFilePath(this, data.getData());
        mTvPatch.setText("patchPath:" + patchPath);
        Log.e("test", "path: " + patchPath);
    }

    private String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return "";
        final String scheme = uri.getScheme();
        String data = "";
        try {
            if (scheme == null)
                data = uri.getPath();
            else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                data = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Files.FileColumns.DATA}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                        if (index > -1) {
                            data = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            }
        } catch (Throwable t) {

        }
        return data;
    }
    //endregion

    //region 更新
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE_PERMISSION != requestCode) {
            return;
        }
        if (grantResults != null) {
            boolean isGrant = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGrant = false;
                    break;
                }
            }
            if (isGrant) {
                start();
            } else {
                Toast.makeText(this, "需要权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void update(View view) {
        if (checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            start();
        } else {
            String[] permissionArray = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,
                    permissionArray,
                    REQUEST_CODE_PERMISSION);
        }
    }

    private void start() {
        if (TextUtils.isEmpty(patchPath)) {
            Toast.makeText(this, "请先选择patch地址", Toast.LENGTH_SHORT).show();
            return;
        }
        final String old = oldPath;
        final String patch = patchPath;
        final String newPath = Environment.getExternalStorageDirectory() + File.separator + "new.apk";
        new AsyncTask<Void, Void, File>() {

            @Override
            protected File doInBackground(Void... voids) {
                boolean isSuccess = false;
                try {
                    isSuccess = BsdiffUtil.merge(old, patch, newPath, BuildConfig.DEBUG);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return isSuccess ? new File(newPath) : null;
            }

            @Override
            protected void onPostExecute(File file) {
                if (file == null) {
                    Toast.makeText(MainActivity.this, "增量更新失败", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String packageName = getApplication().getPackageName();
                        Uri contentUri = FileProvider.getUriForFile(MainActivity.this, packageName + ".fileProvider", file);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    }
                    startActivity(intent);
                }
            }
        }.execute();
    }
    //endregion

    private boolean checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
