package com.innovativeutilities.lawrence.popula;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    static Random rand;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;

    ImageView startButton;
    View wholeScreen;

    EditText nameEditText;
    EditText frequencyEditText;
    EditText numTextsEditText;
    View wordComplexityButton;
    View customMessagesButton;
    View aboutButton;

    Timer timer;
    AppCompatActivity activity;

    String personName = "Bob";
    long timeBetweenMessages = 5000;
    long numTexts = 20;
    //int wordComplexity;
    boolean running;
    int notificationNum;
    ArrayList<String> customNames;
    ArrayList<String> customMessages;
    //    ArrayList<Integer> customPercents;
//    int totalPercent;
    int customWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Locale.setDefault(Locale.US);
        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(true);
        }
        rand = new Random();
        activity = this;
        timer = new Timer();

        mBuilder = new NotificationCompat.Builder(this, "0").setSmallIcon(R.drawable.notification_icon);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        frequencyEditText = findViewById(R.id.frequencyInput);
        numTextsEditText = findViewById(R.id.timesRepeatedInput);
        startButton = findViewById(R.id.startButton);
        nameEditText = findViewById(R.id.nameInput);
        wholeScreen = findViewById(R.id.totalView);
//        wordComplexityButton = findViewById(R.id.wordComplexityButton);
        customMessagesButton = findViewById(R.id.customMessagesButton);
        aboutButton = findViewById(R.id.aboutButton);

        startButton.setImageResource(R.drawable.play);
        customMessagesButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
        wholeScreen.setOnClickListener(this);
//        wordComplexityButton.setOnClickListener(this);
        nameEditText.setOnEditorActionListener(this);
        numTextsEditText.setOnEditorActionListener(this);
        frequencyEditText.setOnEditorActionListener(this);

        if (savedInstanceState != null) {
            personName = savedInstanceState.getString("personName");
            numTexts = savedInstanceState.getLong("numTexts");
            timeBetweenMessages = savedInstanceState.getLong("timeBetweenMessages");
            notificationNum = savedInstanceState.getInt("notificationNum");
        }
        customNames = new ArrayList<>();
        customMessages = new ArrayList<>();
        loadPreferences();
        nameEditText.setHint(personName);
        frequencyEditText.setHint(Integer.toString((int) (timeBetweenMessages / 1000)) + " seconds");
        numTextsEditText.setHint(Long.toString(numTexts));


//        customPercents = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.hideKeyboard(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("personName", personName);
        savedInstanceState.putLong("numTexts", numTexts);
        savedInstanceState.putLong("timeBetweenMessages", timeBetweenMessages);
        savedInstanceState.putInt("notificationNum", notificationNum);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            nameEditText.clearFocus();
            switch (v.getId()) {
                case R.id.frequencyInput:
                    setFrequency();
                    break;
                case R.id.nameInput:
                    setName();
                    break;
                case R.id.timesRepeatedInput:
                    setNumTexts();
                    break;
            }
            nameEditText.clearFocus();
            frequencyEditText.clearFocus();
            numTextsEditText.clearFocus();
            Util.hideKeyboard(this);
            handled = true;
        }
        return handled;
    }

    public void onClick(View v) {
        setFrequency();
        setNumTexts();
        setName();

        Util.hideKeyboard(this);
        Intent intent;
        switch (v.getId()) {

            case R.id.startButton:
                startButton.setOnClickListener(this);
                if (!running) {
                    running = true;
                    startButton.setImageResource(R.drawable.pause);
                    sendNotifications();
                } else {
                    startButton.setImageResource(R.drawable.play);
                    running = false;
                }
                break;
            case R.id.customMessagesButton:
                intent = new Intent(this, CustomMessageActivity.class);
                for (int i = 0; i < customNames.size(); i++) {
                    intent.putExtra("customMessage" + i, customMessages.get(i));
                    intent.putExtra("customName" + i, customNames.get(i));
//                    intent.putExtra("customPercent", customNames.size());
                }
                intent.putExtra("weight", customWeight);
                startActivityForResult(intent, 1);
                break;
//            case R.id.wordComplexityButton:
            case R.id.aboutButton:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        savePreferences();
        Util.hideKeyboard(this);
        super.onPause();
    }

    private void loadPreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        timeBetweenMessages = preferences.getLong("frequency", timeBetweenMessages);
        numTexts = preferences.getLong("numOfTexts", numTexts);
        personName = preferences.getString("name", personName);
        customWeight = (int) preferences.getLong("customWeight", customWeight);
        for (int i = 0; ; i++) {
            if (preferences.getString("customMessage" + i, "_").equals("_")) {
                break;
            }

            customNames.add(preferences.getString("customName" + i, ""));
            customMessages.add(preferences.getString("customMessage" + i, ""));
//            customPercents.add(preferences.getInt("customPercent" + i, 0));
        }
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("frequency", timeBetweenMessages);
        editor.putLong("numOfTexts", numTexts);
        editor.putString("name", personName);
        editor.putLong("customWeight", customWeight);

        for (int i = 0; i < customNames.size(); i++) {
            editor.putString("customName" + i, customNames.get(i));
            editor.putString("customMessage" + i, customMessages.get(i));
//            editor.putInt("customPercent" + i, customPercents.get(i));
        }
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                for (int i = customNames.size(); ; i++) {
                    if (data.getStringExtra("customName" + i) == (null)) {
                        break;
                    }

                    customNames.add(data.getStringExtra("customName" + i));
                    customMessages.add(data.getStringExtra("customMessage" + i));
//                    customPercents.add(data.getIntExtra("customPercent" + i, 0));
//                    totalPercent += customPercents.get(i);
                }
                customWeight = data.getIntExtra("weight", 0);
            }
        }
    }

    public void setFrequency() {
        if (!(frequencyEditText.getText().toString().equals(""))) {
            try {
                timeBetweenMessages = Long.parseLong(frequencyEditText.getText().toString()) * 1000;
            } catch (NumberFormatException e) {
                frequencyEditText.setText("");
                frequencyEditText.setHint("Numbers Only!");
                frequencyEditText.clearFocus();
                return;
            }
        }
        if (timeBetweenMessages < 100) {
            timeBetweenMessages = 100;
        }
        if (timeBetweenMessages > 134217728) {
            timeBetweenMessages = 134217728;
        }
        frequencyEditText.setText("");
        frequencyEditText.setHint(Double.toString(timeBetweenMessages / 1000));
        frequencyEditText.clearFocus();
    }


    public void setNumTexts() {
        if (!numTextsEditText.getText().toString().equals("")) {
            try {
                numTexts = Long.parseLong(numTextsEditText.getText().toString());
            } catch (NumberFormatException e) {
                numTextsEditText.setText("");
                numTextsEditText.setHint("Numbers Only!");
                numTextsEditText.clearFocus();
                return;
            }
        }
        numTextsEditText.setText("");
        numTextsEditText.setHint(Long.toString(numTexts));
        numTextsEditText.clearFocus();
    }

    public void setName() {
        if (!nameEditText.getText().toString().equals("")) {
            personName = nameEditText.getText().toString();
        }
        nameEditText.setText("");
        nameEditText.setHint(personName);
        nameEditText.clearFocus();
    }

    public void sendNotifications() {
        new Thread(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                for (i = 0; i < numTexts; i++) {
                    double wait = timeBetweenMessages * rand.nextDouble();
                    try {
                        Thread.sleep((long) (wait));
                    } catch (InterruptedException e) { //doesn't matter
                    }
                    if (!running) {
                        return;
                    }
                    String[] messageNameArray = randomSentenceName();
                    mBuilder.setContentText(messageNameArray[0]);
                    mBuilder.setContentTitle(messageNameArray[1]);
                    mNotificationManager.notify(notificationNum, mBuilder.build());
                    notificationNum++;

                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Tag");
                    if(3000 > wait) {
                        wl.acquire((long) wait);
                    }
                    wl.acquire(3000);
                    wl.release();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startButton.setImageResource(R.drawable.play);
                    }
                });
            }
        }).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("0", "Messages", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    final String ADVERB = "r";
    final String VERB = "v";
    final String NOUN = "n";
    final String ADJECTIVE = "j";

    public String[] randomSentenceName() {
        int randInt = rand.nextInt(100);
        String[] result = new String[2];
        if (randInt < customWeight && customMessages.size() != 0) {
//            int whichOne = rand.nextInt(totalPercent);
//            int total = 0;
//            for (int i = 0; i < customPercents.size(); i++) {
//                if (i < customPercents.size() + 1) {
//                    if (whichOne >= total && whichOne < total + customPercents.get(i)) {
//                        result[0] = customMessages.get(i);
//                        result[1] = customNames.get(i);
//                        return result;
//                    }
//                } else {
//                    result[0] = customMessages.get(i);
//                    result[1] = customNames.get(i);
//                    return result;
//                }
//                total = total + customPercents.get(i);
//            }
            int whichOne = rand.nextInt(customMessages.size());
            result[0] = customMessages.get(whichOne);
            result[1] = customNames.get(whichOne);
        } else {
            result[1] = randomName("person_names", activity);
            int sentenceStructure = rand.nextInt(10);
            switch (sentenceStructure) {
                case 1:
                    result[0] = "you are sooo " + randomString(ADJECTIVE);
                    break;
                case 2:
                    result[0] = randomQuestion();
                    break;
                case 3:
                    result[0] = randomGreeting();
                    break;
                case 4:
                    result[0] = "he " + randomString(VERB) + "s " + randomString(ADVERB);
                    break;
                case 5:
                    result[0] = "want to " + randomString(VERB) + " at my " + randomString(NOUN);
                    break;
                case 6:
                    result[0] = randomQuestion() + " do you " + randomString(VERB);
                    break;
                case 7:
                    result[0] = randomString(VERB) + randomPreposition() + " " + randomString(NOUN);
                    break;
                case 8:
                    result[0] = "talk to me pls";
                    break;
                case 9:
                    result[0] = randomConjunction() + " " + randomArticle() + " " + randomString(ADJECTIVE) + " " + randomString(NOUN) + " " + randomString(VERB) + "s " + randomString(ADVERB);
                    break;
            }

        }
        int addName = rand.nextInt(100);
        if(addName < 10) {
            result[0] = personName + " " + result[0];
        }
        return result;
    }

    //todo
    public String randomString(String type) {
        try {
            Scanner scan = new Scanner(getAssets().open("most_common_words.txt"));
            int lineNum = rand.nextInt(4999);
            for (int i = 0; i < lineNum; i++) {
                scan.nextLine();
            }
            for(int i = lineNum; ; i++) {
                if(i >= 5000) {
                    i = 0;
                    scan = new Scanner(getAssets().open("most_common_words.txt"));
                }
                String line = scan.nextLine();
                if(getStringType(line).equals(type)) {
                    return getWord(line);
                }
            }

        } catch (Exception e) {

        }
        return "";
    }

    public String getWord(String in) {
        int numSpaces = 0;
        for (int i = 0; ; i++) {
            if (numSpaces == 3) {
                for(int j = i; ; j++) {
                    if(in.charAt(j) == '\t') {
                        return in.substring(i, j);
                    }
                }
            }
            if (in.charAt(i) == ' ') {
                numSpaces++;
            }
        }
    }

    public String getStringType(String in) {
        int numSpaces = 0;
        for (int i = 0; ; i++) {
            if (numSpaces == 2) {
                return String.valueOf(in.charAt(i));
            }
            if (in.charAt(i) == '\t') {
                numSpaces++;
            }
        }
    }


//    public String randomString(String path) {
//        String document = getTermsString(path);
//        String word = "";
//        int beginningOfWord = 0;
//        int randomNum2 = rand.nextInt(document.length() - 5);
//        for (int p = 1; p < 30; p++) {
//            if (document.charAt(randomNum2 - p) == ' ') {
//                beginningOfWord = randomNum2 - p + 1;
//                break;
//            }
//        }
//        for (int p = 1; p < 30; p++) {
//            if (document.charAt(beginningOfWord + p) == ' ') {
//                word = document.substring(beginningOfWord, beginningOfWord + p);
//                for (int b = 0; b < word.length(); b++) {
//                    if (word.charAt(b) == '_') {
//                        word = word.substring(0, b) + " " + word.substring(b + 1, word.length());
//                    }
//                }
//                return word;
//            }
//        }
//        return word;
//    }

    public String getTermsString(String fileName) {
        StringBuilder termsString = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(fileName + ".txt")));

            String str;
            while ((str = reader.readLine()) != null) {
                termsString.append(str);
            }

            reader.close();
            return termsString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String randomName(String path, Context context) {
        int randomNum = rand.nextInt(1000);
        for (int p = 0; p < context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).length() - String.valueOf(randomNum).length(); p++) {
            if ((context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).toString().substring(p, p + String.valueOf(randomNum).length())).equals(Integer.toString(randomNum))) {
                for (int b = 1; b < 100; b++) {
                    if (p + b + String.valueOf(randomNum).length() < context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).length() && context.getText(R.string.person_names).charAt(p + b + String.valueOf(randomNum).length()) == ' ') {
                        return context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).toString().substring(p + String.valueOf(randomNum).length() + 1, p + String.valueOf(randomNum).length() + b + 1);
                    }
                }

            }
        }
        return "";
    }

    public static String randomArticle() {
        int articleNum = rand.nextInt(20);
        switch (articleNum) {
            case 0:
                return "the";
            case 1:
                return "some";
            case 2:
                return "a";
            case 3:
                return "no";
            case 4:
                return "any";
            case 5:
                return "such";
            case 6:
                return "few";
            case 7:
                return "every";
            case 8:
                return "more";
        }
        return "";
    }

    public static String randomConjunction() {
        Random rand = new Random();
        int conjunctionNum = rand.nextInt(20);
        switch (conjunctionNum) {
            case 0:
                return "for";
            case 1:
                return "and";
            case 2:
                return "nor";
            case 3:
                return "but";
            case 4:
                return "or";
            case 5:
                return "yet";
            case 6:
                return "so";
        }
        return "";
    }

    public static String randomPreposition() {
        int prepositionNum = rand.nextInt(46);
        switch (prepositionNum) {
            case 0:
                return "with";
            case 1:
                return "at";
            case 2:
                return "from";
            case 3:
                return "into";
            case 4:
                return "during";
            case 5:
                return "including                 ";
            case 6:
                return "until";
            case 7:
                return "against";
            case 8:
                return "among";
            case 9:
                return "throughout";
            case 10:
                return "despite";
            case 11:
                return "towards";
            case 12:
                return "upon";
            case 13:
                return "concerning";
            case 14:
                return "of";
            case 15:
                return "to";
            case 16:
                return "in";
            case 17:
                return "for";
            case 18:
                return "on";
            case 19:
                return "by";
            case 20:
                return "about";
            case 21:
                return "like";
            case 22:
                return "through";
            case 23:
                return "over";
            case 24:
                return "before";
            case 25:
                return "between";
            case 26:
                return "after";
            case 27:
                return "since";
            case 28:
                return "without";
            case 29:
                return "under";
            case 30:
                return "within";
            case 31:
                return "along";
            case 32:
                return "following";
            case 33:
                return "across";
            case 34:
                return "behind";
            case 35:
                return "beyond";
            case 36:
                return "plus";
            case 37:
                return " except";
            case 38:
                return "but";
            case 39:
                return "up";
            case 40:
                return "out";
            case 41:
                return "around";
            case 42:
                return "down";
            case 43:
                return "off";
            case 44:
                return "above";
            case 45:
                return " near";
        }
        return "";
    }

    public static String randomGreeting() {
        int randomGreetingNum = rand.nextInt(13);
        switch (randomGreetingNum) {
            case 0:
                return "hello";
            case 1:
                return "good morning";
            case 2:
                return "what's up?";
            case 3:
                return "good afternoon";
            case 4:
                return "good evening";
            case 5:
                return "how've you been?";
            case 6:
                return "how's it going?";
            case 7:
                return "hi";
            case 8:
                return "how are you?";
            case 9:
                return "hey";
            case 10:
                return "see you later";
            case 11:
                return "bye";
            case 12:
                return "good night";
        }
        return "";
    }

    public static String randomQuestion() {
        int randomQuestionNum = rand.nextInt(8);
        switch (randomQuestionNum) {
            case 0:
                return "why";
            case 1:
                return "how";
            case 2:
                return "where";
            case 3:
                return "when";
            case 4:
                return "how";
            case 5:
                return "which";
            case 6:
                return "who";
            case 7:
                return "what";
        }
        return "";
    }

}


