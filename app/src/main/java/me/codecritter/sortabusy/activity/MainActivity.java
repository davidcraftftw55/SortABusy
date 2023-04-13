package me.codecritter.sortabusy.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import me.codecritter.sortabusy.Date;
import me.codecritter.sortabusy.DateTime;
import me.codecritter.sortabusy.R;
import me.codecritter.sortabusy.Schedule;
import me.codecritter.sortabusy.TimeBlock;
import me.codecritter.sortabusy.TimeBlockDisplayer;
import me.codecritter.sortabusy.util.CalendarHelper;
import me.codecritter.sortabusy.view.DraggableButton;

/**
 * The Main Activity for this app is the Schedule Maker, where user will be able to edit their
 * schedule on a given day; they should also be able to navigate to the other pages from this activity
 */
public class MainActivity extends AppCompatActivity implements TimeBlockDisplayer {

    private static final String[] HOURS_TEXT = {"12am", "1am", "2am", "3am", "4am", "5am", "6am",
            "7am", "8am", "9am", "10am", "11am", "12pm", "1pm", "2pm", "3pm", "4pm", "5pm", "6pm",
            "7pm", "8pm", "9pm", "10pm", "11pm"};
    private static final int HOUR_HEIGHT = 240;
    private static final int TOP_PADDING = 16;
    private static final int HOUR_TEXT_WIDTH = 100;
    private static final int HOUR_TEXT_MARGIN = 80;
    private static final int HOUR_TEXT_TOP_PADDING = 26;

    static class EventDetails {
        private int index;
        private String title;
        private boolean newEvent;
        private boolean deleted;

        private EventDetails() {
        }

        private EventDetails(int index, String title, boolean newEvent) {
            this.index = index;
            this.title = title;
            this.newEvent = newEvent;
        }
    }

    private static Date selectedDate = new Date();

    /**
     * Translates the y coordinate into a time of day, according to the format of this Schedule Maker
     *
     * @param y y coordinate to translate
     * @return time (in milliseconds since the epoch) that the y coordinate corresponds to
     */
    public static long convertYToTime(int y) {
        Calendar time = Calendar.getInstance();
        int hour = (y - TOP_PADDING) / HOUR_HEIGHT;
        int minute = (int) ((y - TOP_PADDING) % HOUR_HEIGHT * 60F / HOUR_HEIGHT);
        time.set(selectedDate.getYear(), selectedDate.getMonth() - 1, selectedDate.getDay(), hour, minute, 0);
        return time.getTimeInMillis();
    }

    /**
     * Translates the epochTime into a y coordinate, according to the format of this Schedule Maker
     *
     * @param context context needed
     * @param epochTime time (in milliseconds since the epoch) to convert to a y coordinate
     * @return y coordinate that corresponds to the epochTime param
     */
    public static int convertTimeToY(Context context, long epochTime) {
        int timezoneOffset = TimeZone.getTimeZone(CalendarHelper.getInstance(context).getTimezone())
                .getOffset(epochTime);
        return (int) ((epochTime + timezoneOffset) % 86400000L / 3600000F * HOUR_HEIGHT + TOP_PADDING);
    }

    private int displayWidth;
    private int displayHeight;

    private ArrayList<DraggableButton> buttons;
    private Schedule schedule;
    private boolean editModeEnabled;
    private ActivityResultLauncher<EventDetails> eventDetailsLauncher;

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
            displayWidth = view.getWidth();
            displayHeight = view.getHeight();
            drawScheduleMaker(view);

            schedule = new Schedule(selectedDate);
            displaySchedule();

            findViewById(R.id.scheduleDisplay).setOnTouchListener((display, event) -> {
                if (editModeEnabled && event.getAction() == MotionEvent.ACTION_UP
                        && event.getEventTime() - event.getDownTime() < 250L) {
                    addNewTimeBlock(convertYToTime((int) event.getY()));
                    display.performClick(); // really just to silence the warning
                }
                return true;
            });
        });

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.scrollTo(0,
                convertTimeToY(this, System.currentTimeMillis() - 1800000)));

        findViewById(R.id.editModeToggle).setOnClickListener(editMode -> {
            editModeEnabled = ((ToggleButton) editMode).isChecked();
            for (DraggableButton button : buttons) {
                button.setEditMode(editModeEnabled);
            }

            if (!editModeEnabled) {
                CalendarHelper.getInstance(this).saveSchedule(this, schedule);
            }
        });

        eventDetailsLauncher = registerForActivityResult(
                new ActivityResultContract<EventDetails, EventDetails>() {
                    @NonNull
                    @Override
                    public Intent createIntent(@NonNull Context context, EventDetails input) {
                        Intent intent = new Intent(context, me.codecritter.sortabusy.activity.EventDetails.class);
                        intent.putExtra("EVENT_INDEX", input.index);
                        intent.putExtra("EVENT_TITLE", input.title);
                        if (input.newEvent) {
                            intent.putExtra("EVENT_NEW", true);
                        }
                        return intent;
                    }

                    @Override
                    public EventDetails parseResult(int resultCode, @Nullable Intent intent) {
                        if (intent != null) {
                            EventDetails result = new EventDetails();
                            int index = intent.getIntExtra("EVENT_INDEX", -1);
                            String title = intent.getStringExtra("EVENT_TITLE");
                            if (index != -1) {
                                result.index = index;
                                result.title = title;
                                result.newEvent = intent.hasExtra("EVENT_NEW");
                                result.deleted = intent.hasExtra("EVENT_DELETED");
                                return result;
                            }
                        }
                        return null;
                    }
                },
                result -> {
                    if (result != null) {
                        DraggableButton button = buttons.get(result.index);
                        if (!result.deleted) {
                            if (result.title != null && !result.title.isEmpty()) {
                                button.getEvent().setName(result.title);
                                button.setText(result.title);
                            } else {
                                if (result.newEvent) {
                                    buttons.remove(result.index);
                                    schedule.getSchedule().remove(button.getEvent());
                                    ((RelativeLayout) findViewById(R.id.scheduleDisplay)).removeView(button);
                                }
                            }
                        } else {
                            if (button.getEvent().getEventId() != -1) {
                                CalendarHelper.getInstance(this).deleteEvent(this,
                                        button.getEvent());
                            }
                            buttons.remove(result.index);
                            schedule.getSchedule().remove(button.getEvent());
                            ((RelativeLayout) findViewById(R.id.scheduleDisplay)).removeView(button);
                        }
                    }
                });

        findViewById(R.id.prevDayButton).setOnClickListener(button -> {
            clearSchedule();
            selectedDate = selectedDate.getYesterday();
            schedule = new Schedule(selectedDate);
            displaySchedule();
        });

        findViewById(R.id.nextDayButton).setOnClickListener(button -> {
            clearSchedule();
            selectedDate = selectedDate.getTomorrow();
            schedule = new Schedule(selectedDate);
            displaySchedule();
        });
    }

    @Override
    public void onTimeBlockMoved() {
        calculateOverlap();
    }

    private void addNewTimeBlock(long startTime) {
        long start = startTime / 1800000 * 1800000; // clever trick to round to lower 30 min mark
        DateTime eventStart = new DateTime(start);
        DateTime eventEnd = new DateTime(start + 3600000);
        TimeBlock newEvent = new TimeBlock(-1, "", eventStart, eventEnd);
        schedule.getSchedule().add(newEvent);
        int index = addTimeBlock(newEvent);
        calculateOverlap();

        // and open up EventDetails so the name can be set
        eventDetailsLauncher.launch(new EventDetails(index, "", true));
    }

    private void displaySchedule() {
        ((TextView) findViewById(R.id.dayDisplay)).setText(selectedDate.getDayName());
        CalendarHelper.getInstance(this).loadSchedule(this, schedule);
        for (TimeBlock event : schedule.getSchedule()) {
            addTimeBlock(event);
        }
        calculateOverlap();
    }

    private void clearSchedule() {
        // save schedule
        if (editModeEnabled) {
            CalendarHelper.getInstance(this).saveSchedule(this, schedule);
        }

        // disable editMode if enabled
        editModeEnabled = false;
        ((ToggleButton) findViewById(R.id.editModeToggle)).setChecked(false);

        // remove all timeblocks from schedule maker
        RelativeLayout layout = findViewById(R.id.scheduleDisplay);
        while (!buttons.isEmpty()) {
            layout.removeView(buttons.get(0));
            buttons.remove(0);
        }
    }

    private void calculateOverlap() {
        int[] column = new int[buttons.size()];
        int columnCount = 1;
        for (int i = 0; i < buttons.size(); i++) {
            for (int j = i + 1; j < buttons.size(); j++) {
                if (buttons.get(i).getEvent().isOverlapping(buttons.get(j).getEvent())) {
                    if (column[j] == column[i]) {
                        column[j] = column[i] + 1;
                        columnCount = Math.max(columnCount, column[i] + 2);
                    }
                }
            }
        }

        int columnWidth = (displayWidth - HOUR_TEXT_WIDTH) / columnCount;
        for (int i = 0; i < buttons.size(); i++) {
            ViewGroup.LayoutParams layoutParams = buttons.get(i).getLayoutParams();
            layoutParams.width = columnWidth;
            buttons.get(i).setLayoutParams(layoutParams);
            buttons.get(i).setX(columnWidth * column[i] + HOUR_TEXT_WIDTH);
        }
    }

    private int addTimeBlock(TimeBlock event) {
        float start = event.getStart().getHour() + (event.getStart().getMinute() / 60F);
        float end = event.getEnd().getHour() + (event.getEnd().getMinute() / 60F);
        DraggableButton button = new DraggableButton(this, event, this,
                findViewById(R.id.scrollView), displayHeight, HOUR_HEIGHT / 4, TOP_PADDING,
                editModeEnabled);
        button.setLayoutParams(new ViewGroup.LayoutParams(displayWidth - HOUR_TEXT_WIDTH,
                (int) ((end - start) * HOUR_HEIGHT)));
        button.setTextSize(10);
        button.setPadding(50, 10, 0, 0);
        button.setText(event.getName());
        button.setGravity(Gravity.TOP | Gravity.START);
        button.setAllCaps(false);
        button.setX(HOUR_TEXT_WIDTH);
        button.setY(convertTimeToY(this, event.getStart().getEpochTime()));

        int index = buttons.size();
        buttons.add(button);
        ((RelativeLayout) findViewById(R.id.scheduleDisplay)).addView(button);
        button.setOnClickListener(view ->
                eventDetailsLauncher.launch(new EventDetails(index, event.getName(), false)));
        return index;
    }

    private void drawScheduleMaker(ImageView view) {
        Bitmap bitmap = Bitmap.createBitmap(displayWidth, displayHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // draw borders
        Paint border = new Paint();
        border.setColor(Color.BLACK);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(4);
        canvas.drawLine(0F, 0F, displayWidth, 0F, border); // top border
        canvas.drawLine(0F, 0F, 0F, displayHeight, border); // left border
        canvas.drawLine(displayWidth, 0F, displayWidth, displayHeight, border); // right border
        canvas.drawLine(0F, displayHeight, displayWidth, displayHeight, border); // bottom border

        // draw hour lines
        Paint lines = new Paint();
        lines.setColor(Color.BLACK);
        lines.setAlpha(100);
        lines.setStyle(Paint.Style.STROKE);
        lines.setStrokeWidth(8);
        lines.setAntiAlias(true);
        for (int i = 0; i < HOUR_HEIGHT - 1; i++) {
            canvas.drawLine(HOUR_TEXT_WIDTH, HOUR_HEIGHT * i + TOP_PADDING,
                    displayWidth, HOUR_HEIGHT * i + TOP_PADDING, lines);
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