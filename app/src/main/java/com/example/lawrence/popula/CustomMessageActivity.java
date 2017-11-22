package com.example.lawrence.popula;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomMessageActivity extends Activity {

    LinearLayout messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texts_from);
        setTitle("Custom Messages");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        messagesList = findViewById(R.id.messagesList);

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
                startActivity(intent);
                TextView textView = new TextView(this);
                textView.setText("h");
                messagesList.addView(textView);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
