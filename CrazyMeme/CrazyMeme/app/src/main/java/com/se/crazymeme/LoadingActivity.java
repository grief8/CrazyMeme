package com.se.crazymeme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoadingActivity extends AppCompatActivity {
    private Button mBtnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mBtnButton=findViewById(R.id.btn_start);
        mBtnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //跳转到TextView演示界面
                Intent intent=new Intent(LoadingActivity.this,LogActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
