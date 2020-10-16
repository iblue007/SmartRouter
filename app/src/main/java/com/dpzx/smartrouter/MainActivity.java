package com.dpzx.smartrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dpzx.route_compiler.anotation.Route;
import com.dpzx.router_core.Router;

@Route(path = "/main/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                    Class clazz = Class.forName("com.dpzx.my.MyActivity");
//                    Intent intent = new Intent(MainActivity.this, clazz);
//                    startActivity(intent);
                    Router.getInstance().buile("/my/MainActivity").navigetion(MainActivity.this);
                } catch (Exception e) {
                    Log.e("zhuang", "未集成，无法跳转");
                }
            }
        });
    }
}