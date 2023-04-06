package me.codecritter.sortabusy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * The Main Activity for this app is the Schedule Maker, where user will be able to edit their
 * schedule on a given day; they should also be able to navigate to the other pages from this activity
 */
public class MainActivity extends AppCompatActivity {

    private static final String[] HOURS_TEXT = {"12am", "1am", "2am", "3am", "4am", "5am", "6am",
            "7am", "8am", "9am", "10am", "11am", "12pm", "1pm", "2pm", "3pm", "4pm", "5pm", "6pm",
            "7pm", "8pm", "9pm", "10pm", "11pm"};
    private static final int HOUR_HEIGHT = 240;
    private static final int TOP_PADDING = 16;
    private static final int HOUR_TEXT_WIDTH = 100;
    private static final int HOUR_TEXT_MARGIN = 80;
    private static final int HOUR_TEXT_TOP_PADDING = 26;

    public static long convertYToTime(Context context, int y) {
        long time = (y - TOP_PADDING) * 3600000L / HOUR_HEIGHT;
        time -= TimeZone.getTimeZone(CalendarHelper.getInstance(context).getTimezone()).getOffset(time);
        return time;
    }

    private ArrayList<DraggableButton> buttons;
    private Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        buttons = new ArrayList<>();

        ImageView view = findViewById(R.id.displayBackground);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = HOUR_HEIGHT * 24;
        view.setLayoutParams(params);
        findViewById(R.id.displayBackground).post(() -> {
            int width = view.getWidth();
            int height = view.getHeight();
            drawScheduleMaker(view, width, height);

            ScrollView scrollView = findViewById(R.id.scrollView);
            schedule = new Schedule(new Date());
            displaySchedule(scrollView, width, height, schedule);
        });

        findViewById(R.id.editModeToggle).setOnClickListener(editMode -> {
            boolean isEditingNow = ((ToggleButton) editMode).isChecked();
            for (DraggableButton button : buttons) {
                button.setEditMode(isEditingNow);
            }

            if (!isEditingNow) {
                CalendarHelper.getInstance(this).saveSchedule(this, schedule);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (((ToggleButton) findViewById(R.id.editModeToggle)).isChecked()) {
            CalendarHelper.getInstance(this).saveSchedule(this, schedule);
        }
    }

    private void displaySchedule(ScrollView scrollView, int width, int height,
                                 Schedule schedule) {
        CalendarHelper.getInstance(this).loadSchedule(this, schedule);
        for (TimeBlock event : schedule.getSchedule()) {
            addTimeBlock(scrollView, width, height, event);
        }
    }

    private void addTimeBlock(ScrollView scrollView, int width, int height,
                              TimeBlock event) {
        long start = event.getStart().getHour() + (event.getStart().getMinute() / 60);
        long end = event.getEnd().getHour() + (event.getEnd().getMinute() / 60);
        DraggableButton button = new DraggableButton(this, event, scrollView, height,
                HOUR_HEIGHT / 4, TOP_PADDING);
        button.setLayoutParams(new ViewGroup.LayoutParams(width - HOUR_TEXT_WIDTH,
                (int) ((end - start) * HOUR_HEIGHT)));
        button.setTextSize(10);
        button.setPadding(50, 10, 0, 0);
        button.setText(event.getName());
        button.setGravity(Gravity.TOP | Gravity.START);
        button.setAllCaps(false);
        button.setX(HOUR_TEXT_WIDTH);
        button.setY(start * HOUR_HEIGHT + TOP_PADDING);
        buttons.add(button);
        ((RelativeLayout) findViewById(R.id.scheduleDisplay)).addView(button);
    }

    private void drawScheduleMaker(ImageView view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // draw borders
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
        for (int i = 0; i < HOUR_HEIGHT - 1; i++) {
            canvas.drawLine(HOUR_TEXT_WIDTH, HOUR_HEIGHT * i + TOP_PADDING, width, HOUR_HEIGHT * i + TOP_PADDING, lines);
        }

        // type hour labels
        Paint text = new Paint();
        text.setTextSize(32);
        text.setTextAlign(Paint.Align.RIGHT);
        text.setColor(Color.BLACK);
        text.setStyle(Paint.Style.FILL);
        for (int i = 0; i < HOURS_TEXT.length; i++) {
            canvas.drawText(HOURS_TEXT[i], HOUR_TEXT_MARGIN, HOUR_HEIGHT * i + HOUR_TEXT_TOP_PADDING, text);
        }

        // put all above changes in activity
        view.setImageBitmap(bitmap);
    }
}