package com.example.lawrence.popula;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EnterMessage extends Activity implements View.OnClickListener, TextView.OnEditorActionListener{

    Button okButton;
    Button cancelButton;
    EditText customMessageEditText;
    EditText customNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_message_activity);

        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        customMessageEditText = findViewById(R.id.customMessageInput);
        customNameEditText = findViewById(R.id.customNameInput);
        customNameEditText.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = this.getIntent();
        switch (view.getId()) {
            case R.id.okButton:
                if(customMessageEditText.getText() != null) {
                    intent.putExtra("customMessageInput", customMessageEditText.getText().toString());
                }
                else {
                    customNameEditText.setHint("Empty Field!");
                    return;
                }
                if(customNameEditText.getText() != null) {
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
        boolean handled = false;
        switch (v.getId()) {
            case R.id.customNameInput:
                customNameEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;
        }
        return true;
    }
}
