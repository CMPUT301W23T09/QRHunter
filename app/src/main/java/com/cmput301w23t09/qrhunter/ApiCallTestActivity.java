package com.cmput301w23t09.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class ApiCallTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_call_test);

//        ImageView imageView = findViewById(R.id.api_image_test);
//
//        ImageApiCallTask task = new ImageApiCallTask();
//        task.execute("https://api.dicebear.com/5.x/pixel-art/svg?seed=Bob");
    }
}