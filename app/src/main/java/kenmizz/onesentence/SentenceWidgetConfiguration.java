package kenmizz.onesentence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class SentenceWidgetConfiguration extends AppCompatActivity {

    private ArrayList<SentenceItem> sentencesList = new ArrayList<>();

    public static final String SHARED_PREFS = "sentencesPref";
    public static final String WIDGET_PREFS = "widgetsPref";
    public static final String SENTENCE_TEXT = "句";

    private int themeOptions = MainActivity.NIGHTMODE.DEFAULT.ordinal();
    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences config = getSharedPreferences(MainActivity.CONFIG_PREFS, MODE_PRIVATE);
        if(!config.contains("themeOptions")) {
            SharedPreferences.Editor configedit = config.edit();
            configedit.putInt("themeOptions", themeOptions);
            configedit.apply();
        }
        themeOptions = config.getInt("themeOptions", MainActivity.NIGHTMODE.DEFAULT.ordinal());
        switch(themeOptions) {
            case 0:
                switch(getUiMode()) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        setTheme(R.style.AppThemeGrey);
                        break;

                    case Configuration.UI_MODE_NIGHT_NO:
                        setTheme(R.style.AppTheme);
                }
                break;
            case 1:
                setTheme(R.style.AppTheme);
                break;

            case 2:
                setTheme(R.style.AppThemeGrey);
                break;

            case 3:
                setTheme(R.style.AppThemeDark);
        }
        setContentView(R.layout.activity_sentence_widget_configuration);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_CANCELED, resultValue);

        if(widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        loadSentencesList();
        setUpSentencesView();
    }

    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public void loadSentencesList() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Map<String, ?> Sentences = sharedPreferences.getAll();
        if(!Sentences.isEmpty()) {
            for (Map.Entry<String, ?> Sentence : Sentences.entrySet()) {
                sentencesList.add(new SentenceItem(Sentence.getValue().toString()));
            }
            TextView emptyView = findViewById(R.id.configure_emptyView);
            if(emptyView.getVisibility() == View.VISIBLE) {
                emptyView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setUpSentencesView() {
        RecyclerView mRecylerView = findViewById(R.id.RecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        SentenceItemAdapter mAdapter = new SentenceItemAdapter(sentencesList, true, this, widgetId, this);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(mAdapter);
    }
}