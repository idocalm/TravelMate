package com.idocalm.travelmate.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.idocalm.travelmate.components.explore.FlightsSearchFragment;
import com.idocalm.travelmate.components.explore.HotelsSearchFragment;
import com.idocalm.travelmate.R;

public class PersonsDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Button plus, minus, submit;
    public TextView amountText;
    public int amount = 1;
    public String type;

    public PersonsDialog(Activity a, String type) {
        super(a);
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.persons_dialog);
        minus = (Button) findViewById(R.id.minus);
        plus = (Button) findViewById(R.id.plus);
        submit = (Button) findViewById(R.id.btn_done);
        amountText = (TextView) findViewById(R.id.amount);

        String amountString = amount + "";
        amountText.setText(amountString);
        minus.setOnClickListener(this);
        plus.setOnClickListener(this);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.plus) {
            amount++;
            String amountStr = amount + "";
            amountText.setText(amountStr);
        } else if (v.getId() == R.id.minus) {
            if (amount > 1) {
                amount--;
                String amountStr = amount + "";
                amountText.setText(amountStr);
            }
        } else if (v.getId() == R.id.btn_done) {
            if (type.equals("hotels")) {
                HotelsSearchFragment.setPeopleAmount(amount);
            } else if (type.equals("flights")) {
                FlightsSearchFragment.setPeopleAmount(amount);
            }
            dismiss();
        }
    }


}