package me.codecritter.sortabusy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * The Main Activity for this app is the Schedule Maker, where user will be able to edit their
 * schedule on a given day; they should also be able to navigate to the other pages from this activity
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        ImageView view = findViewById(R.id.displayBackground);
        findViewById(R.id.displayBackground).post(() -> {
            int width = view.getWidth();
            int height = view.getHeight();
            ScrollView scrollView = findViewById(R.id.scrollView);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint border = new Paint();
            border.setColor(Color.BLACK);
            border.setStyle(Paint.Style.STROKE);
            border.setStrokeWidth(4);
            canvas.drawLine(0F, 0F, width, 0F, border); // top border
            canvas.drawLine(0F, 0F, 0F, height, border); // left border
            canvas.drawLine(width, 0F, width, height, border); // right border
            canvas.drawLine(0F, height, width, height, border); // bottom border

            // draw hour lines
            Paint lines = new Paint();
            lines.setColor(Color.BLACK);
            lines.setAlpha(100);
            lines.setStyle(Paint.Style.STROKE);
            lines.setStrokeWidth(8);
            lines.setAntiAlias(true);
            canvas.drawLine(100F, 16F, width, 16F, lines);
            for (int i = 1; i < 13; i++) {
                canvas.drawLine(100F, 240F * i + 10, width, 240F * i + 10, lines);
            }

            // type hour labels
            Paint text = new Paint();
            text.setTextSize(32);
            text.setTextAlign(Paint.Align.RIGHT);
            text.setColor(Color.BLACK);
            text.setStyle(Paint.Style.FILL);
            canvas.drawText("9am", 80F, 26F, text);
            int i;
            for (i = 1; i < 3; i++) {
                canvas.drawText((i + 9) + "am", 80F, 240F * i + 20, text);
            }
            canvas.drawText("12pm", 80F, 740F, text);
            for (i = 4; i < 13; i++) {
                canvas.drawText((i - 3) + "pm", 80F, 240F * i + 20, text);
            }

            // put all above changes in activity
            view.setImageBitmap(bitmap);



            DraggableButton button = new DraggableButton(this, scrollView, width, height, 60);
            button.setLayoutParams(new RelativeLayout.LayoutParams(width - 100, 120));
            button.setTextSize(10);
            button.setPadding(50, 10, 0, 0);
            button.setText("Hanging w Riya");
            button.setGravity(Gravity.TOP | Gravity.START);
            button.setOnClickListener(clicked -> ((Button) clicked).setText("Gotta love it"));
            button.setAllCaps(false);
            button.setX(100);
            button.setY(10);
            ((RelativeLayout) findViewById(R.id.scheduleDisplay)).addView(button);
        });
    }
}