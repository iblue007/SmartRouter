package com.dpzx.my;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dpzx.route_compiler.anotation.Route;

@Route(path = "/my/MainActivity")
public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }
}
