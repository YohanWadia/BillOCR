package com.apps.yo.mlkitvision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class MainActivity extends AppCompatActivity {
    private Button getBill, startOCR;
    private ImageView imageView;
    private TextView txtView;
    private Bitmap imageBitmap;

    List<Rect> arrRectsW,arrRectsL;
    List<Integer> arrTop;
    List<String> arrItem, arrPrice;
    Map<String, String> itemMap,priceMap;

    int activityW,activityH, imgBitmapW;
    int avgLineH;
    float wRatio;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getBill = findViewById(R.id.snapBtn);
        startOCR = findViewById(R.id.detectBtn);
        imageView = findViewById(R.id.imageView);
        //txtView = findViewById(R.id.txtView);

        arrRectsL = new ArrayList<>();
        arrRectsW = new ArrayList<>();
        arrTop = new ArrayList<>();
        arrPrice = new ArrayList<>();
        arrItem = new ArrayList<>();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        activityW = metrics.widthPixels;
        activityH = metrics.heightPixels;
        Log.e("X|Y", activityW +"|"+ activityH);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        int downx = (int)event.getX();
                        int downy = (int)event.getY();
                        Log.e("ONTOUCH", downx +"|"+ downy);
                        Log.e("IMG TOUCH","Left value: " + downx*wRatio );

                        break;
                    //we dont need any of the other actions
                    default:
                        break;
                }
                return true;
            }
        });

    }//onCreate




    public void getImage(View v) {
        arrRectsL.clear();
        arrRectsW.clear();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri PhotoUri = data.getData();
            //imageView.setImageURI(PhotoUri);
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), PhotoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            makeBoxes(false);

        }
    }

    private void makeBoxes(boolean b) {
    //Create a new image bitmap and attach a brand new canvas to it
        Bitmap tempBitmap = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
    //Draw the image bitmap into the cavas
        tempCanvas.drawBitmap(imageBitmap, 0, 0, null);

        if(b) {//only make boxes if the call has come from recognition
            Paint linePaint,wordPaint;
            linePaint= new Paint(Paint.ANTI_ALIAS_FLAG);wordPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
            linePaint.setStrokeWidth(4);wordPaint.setStrokeWidth(4);
            linePaint.setStyle(Paint.Style.STROKE);wordPaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(Color.BLUE);wordPaint.setColor(Color.RED);
            for (Rect r : arrRectsW) {
                tempCanvas.drawRect(r, wordPaint);
            }
            for (Rect r : arrRectsL) {
                tempCanvas.drawRect(r, linePaint);
            }
        }

    //Attach the canvas to the ImageView
        imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        imgBitmapW = imageBitmap.getWidth();
        Log.e("DEMENSIONS", imgBitmapW +"|"+ imageBitmap.getHeight());
        wRatio = imageBitmap.getWidth()/((float)activityW);
        Log.e( "W_Ratio","ratio: " + wRatio );

        for(String s : arrItem){ Log.e("ArrLIST", s);}
        for(String s : arrPrice){ Log.e("ArrLIST",arrPrice.size() + s);}
    }

    public void detectText(View v) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void processTxt(FirebaseVisionText text) {
        List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(MainActivity.this, "No Text :(", Toast.LENGTH_LONG).show();
            return;
        }

        arrPrice.clear();arrItem.clear();arrTop.clear();

        for (FirebaseVisionText.TextBlock block: text.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            //Log.e("BLOCK",blockText);
            //Log.e("Points",blockCornerPoints[0].toString() +"|"+ blockCornerPoints[1].toString());
            //Log.e("Frame",blockFrame.left +"|"+ blockFrame.top +"|"+blockFrame.right +"|"+blockFrame.bottom);
            //Float blockConfidence = block.getConfidence();
            //List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            int i = 0;
            for (FirebaseVisionText.Line line: block.getLines()) {
                String lineText = line.getText();
                //Float lineConfidence = line.getConfidence();
                //List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                //Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                arrRectsL.add(lineFrame);
                Log.e("LINE",lineText);
                Log.e("Frame",lineFrame.left +"|"+ lineFrame.top +"|"+lineFrame.right +"|"+lineFrame.bottom + "\n");

                avgLineH = lineFrame.bottom - lineFrame.top;
                Log.e("LINE-Height","lineH: " + avgLineH);

                if((lineFrame.left< (imgBitmapW * 0.1)) && (lineFrame.right> (imgBitmapW * 0.9)) ){//from left - right
                   String ans = workOnLast2(line);
                        arrTop.add(lineFrame.top);
                        arrPrice.add(ans);

                        int x = lineText.indexOf(ans);
                        String ans1 = lineText.substring(0, x);
                        arrItem.add(ans1);

                }
                else if((lineFrame.left< (imgBitmapW * 0.1)) && (lineFrame.right > (imgBitmapW * 0.1))){//only particulars on left
                    if(arrTop.isEmpty()){//this means he is first to be reg
                        arrTop.add(lineFrame.top);
                        arrItem.add(lineText);
                    }
                    else{
                        if(arrItem.size()>=arrPrice.size()){
                            arrTop.add(lineFrame.top);//only if itemsize >= Pricesize can this be the new entry for a new Top
                        }
                        arrItem.add(lineText);
                    }
                }
                else if((lineFrame.right > (imgBitmapW * 0.9))){
                    String ans = workOnLast2(line);
                    if(arrTop.isEmpty()){//luckily we dont need to check a blank space above here, CZ 100% an item will come to pair with this price
                        arrTop.add(lineFrame.top);
                        arrPrice.add(ans);Log.e("SAVED","111111111111");
                    }
                    else{
                        if(arrPrice.size()==arrItem.size()){
                            arrTop.add(lineFrame.top);
                        }
                        arrPrice.add(ans);Log.e("SAVED","22222222222");
                    }

                }



                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    //Float elementConfidence = element.getConfidence();
                    //List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                    arrRectsW.add(elementFrame);
                    Log.e("ELEMENT",elementText);
                }
            }
        }
     makeBoxes(true);
    }



    private String workOnLast2(FirebaseVisionText.Line line) {
        String ans=null;
        boolean isNum=false;
        int howmany = line.getElements().size();

        if(howmany>1){
            isNum = checkNaN(line.getElements().get(howmany-2));//means secondLast
            if(isNum){
                ans = line.getElements().get(howmany-2).getText();
            }
            else {
                isNum = checkNaN(line.getElements().get(howmany-1));//means now we can check the last
                if(isNum){
                    ans = line.getElements().get(howmany-1).getText();
                }
            }
        }
        else if(howmany==1){
            isNum = checkNaN(line.getElements().get(0));
            if(isNum){
                ans = line.getElements().get(0).getText();
            }
        }

        return ans;
    }

    private boolean checkNaN(FirebaseVisionText.Element element) {
        boolean b=true;
        String str = element.getText();
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c))
            {
                if (!(c=='.' | c==',' | c=='-' )){//so only if its NOT a num and then NOT , - . //else it will return true
                   b=false; break;
                }
            }
        }

        return b;
    }


}
