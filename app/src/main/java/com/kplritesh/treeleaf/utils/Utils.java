package com.kplritesh.treeleaf.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.kplritesh.treeleaf.R;

public class Utils {

    public boolean isNotNOE(String str){
        //NOTE: If null or empty returns false else returns true
        return str != null && !str.isEmpty();
    }

    private TextInputLayout prevTil;
    public void errorOnTIL(TextInputLayout til, EditText tv, String msg){
        if (prevTil != null){ prevTil.setError(null); }
        if(isNotNOE(msg)){
            til.setError(msg);
            tv.requestFocus();
            this.prevTil = til;
        } else {
            til.setError(null);
        }
    }
    public void removeErrorUIOnTextChange(EditText editText, TextInputLayout til){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0) {
                    til.setError(null);
                    til.setBoxStrokeColor(editText.getContext().getResources().getColor(R.color.success, null));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
    private static Bitmap convertString64ToImage(String b64) {
        byte[] decodedString = Base64.decode(b64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static Bitmap convertStringToBitmap(String b64) {
        String finalImage = b64.substring(b64.indexOf(",") + 1);
        return convertString64ToImage(finalImage);
    }

}
