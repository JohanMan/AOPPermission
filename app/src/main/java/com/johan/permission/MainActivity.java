package com.johan.permission;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.johan.aop.permisssion.Permission;
import com.johan.aop.permisssion.PermissionRefuse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    @Permission(value = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, refuse = MyRefuse.class)
    private void takePicture() {
        System.err.println("ok, you can take a picture !!!");
    }

    public static class MyRefuse implements PermissionRefuse {

        @Override
        public void onRefuse() {
            System.err.println("you refuse permission, can not take picture !!!");
        }

    }

}
