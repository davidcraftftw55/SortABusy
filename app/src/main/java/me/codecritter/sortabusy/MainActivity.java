package me.codecritter.sortabusy;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The Main Activity for this app is the Schedule Maker, where user will be able to edit their
 * schedule on a given day; they should also be able to navigate to the other pages from this activity
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setX(350);
        button.setOnClickListener(view ->
                CalendarHelper.getInstance(this).debugMethod(this, findViewById(R.id.textView)));
    }
}