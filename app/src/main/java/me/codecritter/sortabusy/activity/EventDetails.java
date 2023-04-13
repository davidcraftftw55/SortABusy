package me.codecritter.sortabusy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import me.codecritter.sortabusy.R;

/**
 * Dialog activity for editing the details of an existing TimeBlock
 */
public class EventDetails extends Activity {

    private int timeBlockIndex;
    private boolean newEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        if (intent != null) {
            int index = intent.getIntExtra("EVENT_INDEX", -1);
            if (index != -1) {
                timeBlockIndex = index;
            }
            newEvent = intent.hasExtra("EVENT_NEW");
            String title = intent.getStringExtra("EVENT_TITLE");
            if (title != null) {
                ((EditText) findViewById(R.id.titleField)).setText(title);
            }
            setResult(RESULT_CANCELED, intent);
        }

        findViewById(R.id.saveButton).setOnClickListener(view -> {
            String title = String.valueOf(((EditText) findViewById(R.id.titleField)).getText());
            Intent result = new Intent();
            result.putExtra("EVENT_INDEX", timeBlockIndex);
            result.putExtra("EVENT_TITLE", title);
            if (newEvent) {
                result.putExtra("EVENT_NEW", true);
            }
            setResult(Activity.RESULT_OK, result);
            finish();
        });

        findViewById(R.id.deleteButton).setOnClickListener(view -> {
            Intent result = new Intent();
            result.putExtra("EVENT_INDEX", timeBlockIndex);
            result.putExtra("EVENT_DELETED", true);
            if (newEvent) {
                result.putExtra("EVENT_NEW", true);
            }
            setResult(Activity.RESULT_OK, result);
            finish();
        });
    }
}