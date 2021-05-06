package com.kelly.controller;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

public class ACAduserEnglishEditText extends AppCompatEditText {
    private boolean sign;
    private EditText txtView;
    private String val;

    public ACAduserEnglishEditText(Context content, boolean sign, String val) {
        super(content);
        this.sign = sign;
        this.val = val;
        setGravity(3);
        setEnabled(this.sign);
        setBackgroundColor(-4210753);
        if (sign) {
            //setTextColor(ViewCompat.MEASURED_STATE_MASK);
            setKeyListener(new DigitsKeyListener(false, true));
            setFilters(new InputFilter[]{new LengthFilter(8)});
        } else {
            //setTextColor(ViewCompat.MEASURED_STATE_MASK);
        }
        setText(this.val);
        setFocusableInTouchMode(true);
    }

    public int getEditValue() {
        String txtStr = this.txtView.getText().toString().trim();
        if (txtStr.matches("\\d{1,}")) {
            return Integer.parseInt(txtStr);
        }
        return 0;
    }
}
