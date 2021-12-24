package com.example.contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        checkPermission();
    }
    //Hàm event khi người dùng đang lựa chọn cho phép or từ chối ở trên dialog xin quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                    && grantResults[5] == PackageManager.PERMISSION_GRANTED
                    && grantResults[6] == PackageManager.PERMISSION_GRANTED
                    && grantResults[7] == PackageManager.PERMISSION_GRANTED
            ) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(IntroActivity.this, MainActivity.class));
                        finish();
                    }
                }, 150);
            } else {
                Toast.makeText(this, "Bạn đã từ chối quyền", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void checkPermission() {
        //Đòi quyền khi mà quyền đó chưa được cho phép
        ActivityCompat.requestPermissions(IntroActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS
                        , Manifest.permission.READ_CALL_LOG
                        , Manifest.permission.CALL_PHONE
                        , Manifest.permission.SEND_SMS
                        , Manifest.permission.WRITE_CONTACTS
                        , Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_PHONE_STATE}, 1);
        //Check xem nếu các quyền đều được cho phép r rồi hay chưa
        if(ContextCompat.checkSelfPermission(IntroActivity.this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(IntroActivity.this,
                Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(IntroActivity.this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(IntroActivity.this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(IntroActivity.this,
                Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(IntroActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(IntroActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(IntroActivity.this,
            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                    finish();
                }
            }, 150);
        }
    }
}
