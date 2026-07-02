package com.beckytech.og_artiiwwankutaa5ffaa.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.beckytech.og_artiiwwankutaa5ffaa.MyApplication;

public class AppOpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This activity is transparent and just triggers the App Open Ad from MyApplication
        if (getApplication() instanceof MyApplication) {
            ((MyApplication) getApplication()).showAdIfAvailable(this, this::finish);
        } else {
            finish();
        }
    }
}