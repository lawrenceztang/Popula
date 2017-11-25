package com.example.lawrence.popula;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomMessageActivity extends Activity {

    LinearLayout layoutList;
    ArrayList<TextView> namesTextViews;
    ArrayList<TextView> messagesTextViews;
    final int MESSAGE_ACTIVITY_RESULT = 0;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_message);
        setTitle("Custom Messages");
        if(getActionBar() != null) { getActionBar().setDisplayHomeAsUpEnabled(true); }
        layoutList = findViewById(R.id.messagesList);
        namesTextViews = new ArrayList<>();
        messagesTextViews = new ArrayList<>();
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
                namesTextViews.add(new TextView(this));
                namesTextViews.get(namesTextViews.size() - 1).setText(data.getStringExtra("customNameInput"));
                namesTextViews.get(namesTextViews.size() - 1).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.large_text));
                layoutList.addView(namesTextViews.get(namesTextViews.size() - 1));
                messagesTextViews.add(new TextView(this));
                messagesTextViews.get(messagesTextViews.size() - 1).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_text));
                messagesTextViews.get(messagesTextViews.size() - 1).setText(data.getStringExtra("customMessageInput"));
                layoutList.addView(messagesTextViews.get(messagesTextViews.size() - 1));
                layoutList.addView(inflater.inflate(R.layout.line_view, layoutList, false));

            }
        }
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

            namesTextViews.add(new TextView(this));
            namesTextViews.get(i).setText(sharedPreferences.getString("name" + i, ""));
            namesTextViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.large_text));
            layoutList.addView(namesTextViews.get(i));

            messagesTextViews.add(new TextView(this));
            messagesTextViews.get(i).setText(sharedPreferences.getString("message" + i, ""));
            messagesTextViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_text));
            layoutList.addView(messagesTextViews.get(i));

            layoutList.addView(inflater.inflate(R.layout.line_view, layoutList, false));
        }
    }

    @Override
    public void onBackPressed() {
        savePreferences();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savePreferences();
    }
}
