package com.apps.yo.mlkitvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Main2Activity extends AppCompatActivity implements MyDialog.MyDialogListener {

    ListView lv;
    TextView tv;
    List<Bill> bills;
    boolean[] arrSelected;
    ListAdapterClass adapter;

    float amt,reverseAmt;
    int reversePos;

    SoundPool soundPool;
    int kaching;

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

        arrSelected = new boolean[bills.size()];

        adapter = new ListAdapterClass(this, bills,arrSelected);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("CLICKED","Price: " + bills.get(position).price);
                soundPool.play(kaching, 0.75f, 0.75f, 0, 0, 1);

                float amt1 = bills.get(position).price;
                amt+=amt1;
                tv.setText(String.valueOf(amt));
                arrSelected[position] = true;
                resetAdapter();

                reverseAmt=amt1;
                reversePos = position;
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                float splitAmt = bills.get(position).price;
                reversePos =position;
                arrSelected[position] = true;
                resetAdapter();
                openDialog(splitAmt);
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//for api v21+
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)//see extra options for games etc etc
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)// again many options on the way u use the sound
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        kaching = soundPool.load(this, R.raw.kaching, 1);
    }

    private void openDialog(float splitAmt) {
        Bundle args = new Bundle();
        args.putFloat("amt", splitAmt);

        MyDialog exampleDialog = new MyDialog();
        exampleDialog.setArguments(args);
        exampleDialog.show(getSupportFragmentManager(), "XXXXX");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    @Override
    public void sendDiv(float amt1) {
        soundPool.play(kaching, 0.75f, 0.75f, 0, 0, 1);
        amt+=amt1;
        tv.setText(String.valueOf(amt));
        reverseAmt=amt1;
    }

    public void reset(){
        for(int i=0; i<arrSelected.length; i++){arrSelected[i]=false;}
        resetAdapter();

        amt =0.0f;
        tv.setText("0.0");

        reverseAmt=0.0f;
        reversePos =0;
    }

    public void rollBack(){
        arrSelected[reversePos] = false;
        resetAdapter();

        amt-=reverseAmt;
        tv.setText(String.valueOf(amt));

        reverseAmt=0.0f;
        reversePos =0;
    }
    
    public void resetAdapter(){
        adapter = new ListAdapterClass(this, bills,arrSelected);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                reset();
                Toast.makeText(this, "Start Again", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.reverse:
                rollBack();
                Toast.makeText(this, "Last Entry Reversed", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("Option Menu()", "111111111111 ");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }




}
