package com.example.lawrence.popula;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EnterMessage extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener{

    Button okButton;
    Button cancelButton;
    EditText customMessageEditText;
    EditText customNameEditText;
    LinearLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_message_activity);

        okButton = (Button)findViewById(R.id.okButton);
        cancelButton = (Button)findViewById(R.id.cancelButton);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        customMessageEditText = (EditText)findViewById(R.id.customMessageInput);
        customNameEditText = (EditText)findViewById(R.id.customNameInput);
        customNameEditText.setOnEditorActionListener(this);
        myLayout = (LinearLayout) this.findViewById(R.id.topLayout);
        customMessageEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    @Override
    public void onClick(View view) {
        Intent intent = this.getIntent();
        switch (view.getId()) {
            case R.id.okButton:
                if(!customMessageEditText.getText().toString().equals("")) {
                    intent.putExtra("customMessageInput", customMessageEditText.getText().toString());
                }
                else {
                    customNameEditText.setHint("Empty Field!");
                    return;
                }
                if(!customNameEditText.getText().toString().equals("")) {
                    intent.putExtra("customNameInput", customNameEditText.getText().toString());
                }
                else {
                    customNameEditText.setHint("Empty Field!");
                    return;
                }
                break;
            case R.id.cancelButton:
                setResult(RESULT_CANCELED, intent);
                finish();
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (v.getId()) {
            case R.id.customNameInput:
                customNameEditText.clearFocus();
                Util.hideKeyboard(this);
                break;
        }
        myLayout.requestFocus();
        return true;
    }

    @Override
    protected void onPause() {
        Util.hideKeyboard(this);
        super.onPause();
    }
}
