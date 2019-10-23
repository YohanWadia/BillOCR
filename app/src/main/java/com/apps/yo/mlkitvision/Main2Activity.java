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

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements MyDialog.MyDialogListener,MyEditDialog.MyEditDialogListener {

    ListView lv;
    TextView tv;
    List<Bill> bills, shareBill;
    boolean[] arrSelected;
    ListAdapterClass adapter;

    float amt,reverseAmt;
    int reversePos, editPos;
    boolean editing;

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
        /*bills=new ArrayList<>();
        fillListwithBILLS();*/

        arrSelected = new boolean[bills.size()];

        adapter = new ListAdapterClass(this, bills,arrSelected);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("CLICKED","Price: " + bills.get(position).price);
                if(editing){
                    editPos = position;
                    openEditDialog();
                    return ;//it doesnt do nything else if in edit mode
                }

                soundPool.play(kaching, 0.75f, 0.75f, 0, 0, 1);

                float amt1 = bills.get(position).price;
                amt+=amt1;
                tv.setText(String.valueOf(amt));
                arrSelected[position] = true;
                resetAdapter();

                reverseAmt=amt1;
                reversePos = position;
                shareBill.add(bills.get(position));
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                float splitAmt = bills.get(position).price;
                reversePos =position;
                arrSelected[position] = true;
                resetAdapter();

                shareBill.add(bills.get(position));

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

        shareBill = new ArrayList<>();
        kaching = soundPool.load(this, R.raw.kaching, 1);
    }


    private void fillListwithBILLS() {
        Bill b = new Bill("Hamburger",2.7f);
        bills.add(b);
        b = new Bill("Noodles",5.4f);
        bills.add(b);
        b = new Bill("Pizza",7.0f);
        bills.add(b);
        b = new Bill("extra Bread",1.75f);
        bills.add(b);
        b = new Bill("Pancakes",12.6f);
        bills.add(b);
        b = new Bill("Coca Cola",2.7f);
        bills.add(b);
        b = new Bill("ChickenNuggets",8.0f);
        bills.add(b);

    }

    private void openDialog(float splitAmt) {
        Bundle args = new Bundle();
        args.putFloat("amt", splitAmt);

        MyDialog exampleDialog = new MyDialog();
        exampleDialog.setArguments(args);
        exampleDialog.show(getSupportFragmentManager(), "XXXXX");

    }
    private void openEditDialog() {
        MyEditDialog editDialog = new MyEditDialog();
        editDialog.show(getSupportFragmentManager(), "abc");
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

        int lastentry = shareBill.size()-1;
        Bill b = new Bill(shareBill.get(lastentry).item,amt1);
        shareBill.set(lastentry,b);
    }

    @Override
    public void applyChange(float newVal) {
        Bill b = new Bill(bills.get(editPos).item,newVal);
        bills.set(editPos,b);
        resetAdapter();

        editing=false;
    }

    public void reset(){
        for(int i=0; i<arrSelected.length; i++){arrSelected[i]=false;}
        resetAdapter();

        amt =0.0f;
        tv.setText("0.0");

        reverseAmt=0.0f;
        reversePos =0;
        shareBill.clear();
    }

    public void rollBack(){
        arrSelected[reversePos] = false;
        resetAdapter();

        amt-=reverseAmt;
        tv.setText(String.valueOf(amt));

        reverseAmt=0.0f;
        reversePos =0;

        shareBill.remove(shareBill.size()-1);
    }

    public void share(){
        StringBuilder txt= new StringBuilder();
        txt.append("***************\nYour Bill Split\n\n");
        for(Bill b : shareBill){
            txt.append(b.item).append("\t").append(b.price).append("\n");
        }
        txt.append("***************");
        String toShare = txt.toString();
        Log.e("SHARING",toShare);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, toShare);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
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

            case R.id.share:
                share();
                Toast.makeText(this, "Sharing via Messenger", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.edit:
                editing=true;
                Toast.makeText(this, "Select Price to Edit", Toast.LENGTH_SHORT).show();
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
