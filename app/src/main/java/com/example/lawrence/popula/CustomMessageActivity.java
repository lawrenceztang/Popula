package com.example.lawrence.popula;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomMessageActivity extends AppCompatActivity implements View.OnTouchListener{

    LinearLayout layoutList;
    ArrayList<TextView> namesTextViews;
    ArrayList<TextView> messagesTextViews;
    ArrayList<Integer> percentNums;
    final int MESSAGE_ACTIVITY_RESULT = 0;
    float SLIDE_DISTANCE_FOR_DELETE;
    int width;
    int height;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_message);
        setTitle("Custom Messages");
        if(getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        layoutList = findViewById(R.id.messagesList);
        namesTextViews = new ArrayList<>();
        messagesTextViews = new ArrayList<>();
        percentNums = new ArrayList<>();
        inflater = getLayoutInflater();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        loadPreferences();
        SLIDE_DISTANCE_FOR_DELETE = getResources().getDimensionPixelSize(R.dimen.remove_width);
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
                percentNums.add(data.getIntExtra("percent", 0));
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
                    for(int i = 0; i < messagesTextViews.size(); i++) {
                        if(view == layoutList.getChildAt(i)) {
                            layoutList.removeView(view);
                            messagesTextViews.remove(i);
                            namesTextViews.remove(i);
                        }
                    }

                }
                else {
                    view.scrollTo(0, view.getLeft());
                }
                return true;
            case MotionEvent.ACTION_MOVE:

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
            editor.putInt("percent" + i, percentNums.get(i));
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

    public void drawGroup(int num, String message, String name) {
        namesTextViews.add(new TextView(this));
        namesTextViews.get(num).setText(name);
        namesTextViews.get(num).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.large_text));

        messagesTextViews.add(new TextView(this));
        messagesTextViews.get(num).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_text));
        messagesTextViews.get(num).setText(message);

        LinearLayout verticalLayout = new LinearLayout(this);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        verticalLayout.addView(namesTextViews.get(num));
        verticalLayout.addView(messagesTextViews.get(num));
        verticalLayout.addView(inflater.inflate(R.layout.line_view, layoutList, false));

        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.addView(verticalLayout);
        horizontalLayout.addView(inflater.inflate(R.layout.remove_button, layoutList, false));
        verticalLayout.getLayoutParams().width = width;

        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        scrollView.addView(horizontalLayout);
        scrollView.setHorizontalScrollBarEnabled(false);

        layoutList.addView(scrollView);
        scrollView.setOnTouchListener(this);
    }
    }
