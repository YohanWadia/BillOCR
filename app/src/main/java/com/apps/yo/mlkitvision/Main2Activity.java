package com.apps.yo.mlkitvision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class Main2Activity extends AppCompatActivity {

    ListView lv;
    TextView tv;
    List<Bill> bills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tv = findViewById(R.id.textView2);
        lv = findViewById(R.id.listView);

        Intent i = getIntent();
        bills = i.getParcelableArrayListExtra("bills");
        for(Bill bb : bills ){
            Log.e("ArrLIST", bb.item + "|" + bb.price );
        }






    }
}
