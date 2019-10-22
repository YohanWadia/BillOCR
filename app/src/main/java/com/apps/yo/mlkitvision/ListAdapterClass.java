package com.apps.yo.mlkitvision;

import android.app.Activity;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapterClass extends ArrayAdapter<Bill> {


    Activity context;
    List<Bill> bills;
    boolean[] arrSelected;

    public ListAdapterClass(Activity context, List<Bill> bills, boolean[] arrSelected){
        super(context,R.layout.list_layout, bills);//have to pass all this to super with the Layout that will be used
        this.context = context;//passing the activity as a parameter... not sure if it is the same as passing Context??
        this.bills = bills;
        this.arrSelected = arrSelected;
    }

    @NonNull
    @Override// this method seems to get called just before the ActualList(in MainAcitvity) is displayed & after the Adapter has been created all the data
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        //LinearLayout layout = listViewItem.findViewById(R.id.linearLayout1);
        TextView textViewItem = (TextView) listViewItem.findViewById(R.id.textViewItem);
        TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.textViewPrice);

        Bill billInQuestion = bills.get(position);  // this returns an entire data row of the Movie at that position
        textViewItem.setText(billInQuestion.item);
        textViewPrice.setText(String.valueOf(billInQuestion.price));

        Log.e("ADAPTER","***********" + position+"*************" + arrSelected[position]);
        if(arrSelected[position]){listViewItem.setBackgroundColor(Color.LTGRAY);}
        else{listViewItem.setBackgroundColor(Color.WHITE);}

        return listViewItem;

    }
}

