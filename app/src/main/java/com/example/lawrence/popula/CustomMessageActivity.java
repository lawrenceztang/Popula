package com.example.lawrence.popula;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomMessageActivity extends AppCompatActivity implements View.OnTouchListener{

    LinearLayout layoutList;
    ArrayList<TextView> namesTextViews;
    ArrayList<TextView> messagesTextViews;
    final int MESSAGE_ACTIVITY_RESULT = 0;
    ArrayList<LinearLayout> messageCombo;
    final float SLIDE_DISTANCE_FOR_DELETE = 300;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_message);
        setTitle("Custom Messages");
        if(getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        layoutList = (LinearLayout)findViewById(R.id.messagesList);
        namesTextViews = new ArrayList<>();
        messagesTextViews = new ArrayList<>();
        messageCombo = new ArrayList<>();
        inflater = getLayoutInflater();
        loadPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.add_message:
                Intent intent = new Intent(this, EnterMessage.class);
                startActivityForResult(intent, MESSAGE_ACTIVITY_RESULT);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MESSAGE_ACTIVITY_RESULT) {
            if (resultCode == RESULT_OK) {
               drawGroup(messagesTextViews.size(), data.getStringExtra("customMessageInput"), data.getStringExtra("customNameInput"));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.hideKeyboard(this);
    }

    float downX;
    float downY;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                if (downX - event.getX() > SLIDE_DISTANCE_FOR_DELETE) {
                    int index = 0;
                    for(int i = 0; i < messageCombo.size(); i++) {
                        if(view == messageCombo.get(i)) {
                            index = i;
                        }
                    }
                    layoutList.removeView(view);
                    messagesTextViews.remove(index);
                    namesTextViews.remove(index);
                    messageCombo.remove(index);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                view.setX(getLimitedX(event.getX() - downX));
                break;
        }
        return false;
    }

    private void savePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i = 0; i < namesTextViews.size(); i++) {
            editor.putString("name" + i, namesTextViews.get(i).getText().toString());
            editor.putString("message" + i, messagesTextViews.get(i).getText().toString());
        }
        editor.putInt("messagesSize", messagesTextViews.size());
        editor.apply();
    }

    private void loadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        for(int i = 0; i < sharedPreferences.getInt("messagesSize", 0); i++) {
            drawGroup(i, sharedPreferences.getString("message" + i, ""), sharedPreferences.getString("name" + i, ""));
        }
    }

    @Override
    public void onBackPressed() {
        savePreferences();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        savePreferences();
        Util.hideKeyboard(this);
        super.onPause();
    }

    public float getLimitedX(float in) {
        if(in < - SLIDE_DISTANCE_FOR_DELETE) {
            return - SLIDE_DISTANCE_FOR_DELETE;
        }
        else if(in > 0) {
            return 0;
        }
        return 0;
    }

    public void drawGroup(int num, String message, String name) {
        namesTextViews.add(new TextView(this));
        namesTextViews.get(num).setText(name);
        namesTextViews.get(num).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.large_text));

        messagesTextViews.add(new TextView(this));
        messagesTextViews.get(num).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_text));
        messagesTextViews.get(num).setText(message);

        messageCombo.add(new LinearLayout(this));
        messageCombo.get(num).setOrientation(LinearLayout.VERTICAL);
        messageCombo.get(num).addView(namesTextViews.get(num));
        messageCombo.get(num).addView(messagesTextViews.get(num));
        messageCombo.get(num).addView(inflater.inflate(R.layout.line_view, layoutList, false));

        layoutList.addView(messageCombo.get(num));

        messageCombo.get(num).setOnTouchListener(this);
    }

}
