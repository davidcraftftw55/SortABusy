package me.codecritter.sortabusy.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import me.codecritter.sortabusy.DateTime;
import me.codecritter.sortabusy.TimeBlock;
import me.codecritter.sortabusy.activity.MainActivity;

/**
 * A custom implementation of a Button which allows the user to drag on it to either resize or move it
 */
@SuppressLint("ViewConstructor")
public class DraggableButton extends AppCompatButton {

    /**
     * If a button is held for more milliseconds than this, it's a drag not a click
     */
    private static final long CLICK_TIME = 100L; // milliseconds

    private enum DRAG_TYPE { MOVE, RESIZE_TOP, RESIZE_BOT }

    private final ScrollView parent;
    private final int PARENT_HEIGHT;
    private final int MIN_HEIGHT;
    private final int Y_OFFSET;


    private final TimeBlock event;
    private boolean editMode;
    private boolean isClick;
    private DRAG_TYPE dragType;
    /**
     * Difference between this button's absolute y coordinate and its getY()
     */
    private float dY;
    /**
     * Height of button before user started dragging it
     */
    private float origHeight;
    /**
     * Difference from top of button to tap location
     */
    private float tapDy;

    /**
     * Constructs this button (typically only used by code)
     * @param context context needed
     * @param event TimeBlock object corresponding to the event this button represents
     * @param parent ScrollView where this button will be added
     * @param parentHeight height of parent view
     * @param minHeight minimum height of this button
     *                  (should be 15 minutes, or 1/4 of the space between hour lines)
     * @param yOffset y coordinate of the top most block for this button
     * @param editMode true if editMode is currently enabled, and button can immediately start being
     *                 dragged, false otherwise
     */
    public DraggableButton(@NonNull Context context, TimeBlock event, ScrollView parent,
                           int parentHeight, int minHeight, int yOffset, boolean editMode) {
        super(context);
        this.event = event;
        this.parent = parent;
        PARENT_HEIGHT = parentHeight;
        MIN_HEIGHT = minHeight;
        Y_OFFSET = yOffset;
        this.editMode = editMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (editMode) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    parent.requestDisallowInterceptTouchEvent(true);
                    isClick = true;
                    int[] coords = new int[2];
                    getLocationOnScreen(coords);
                    float tapDx = motionEvent.getRawX() - coords[0];
                    dY = coords[1] - getY();
                    tapDy = motionEvent.getRawY() - coords[1];
                    origHeight = getHeight();

                    if (tapDx < getWidth() * 0.33) {
                        dragType = DRAG_TYPE.RESIZE_TOP;
                    } else if (tapDx < getWidth() * 0.67) {
                        dragType = DRAG_TYPE.MOVE;
                    } else {
                        dragType = DRAG_TYPE.RESIZE_BOT;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isClick || motionEvent.getEventTime() - motionEvent.getDownTime() > 100) {
                        isClick = false;
                        switch (dragType) {
                            case RESIZE_TOP:
                                // find notch for newTop
                                float newTop = roundToNotch(motionEvent.getRawY() - dY - tapDy);

                                int heightIncrease = Math.round(getY() - newTop);
                                if (getHeight() + heightIncrease >= MIN_HEIGHT && newTop > 0) {
                                    setY(newTop);

                                    ViewGroup.LayoutParams params = getLayoutParams();
                                    params.height += heightIncrease;
                                    setLayoutParams(params);
                                }
                                break;
                            case MOVE:
                                float newY = roundToNotch(motionEvent.getRawY() - dY - tapDy);
                                if (newY > 0 && newY < PARENT_HEIGHT - getHeight()) {
                                    setY(newY);
                                }
                                break;
                            case RESIZE_BOT:
                                float newBot = roundToNotch(motionEvent.getRawY() - dY - tapDy);
                                int newHeight = Math.round(newBot + origHeight - getY());
                                if (newHeight >= MIN_HEIGHT && newHeight < PARENT_HEIGHT - getY()) {
                                    ViewGroup.LayoutParams params = getLayoutParams();
                                    params.height = newHeight;
                                    setLayoutParams(params);
                                }
                                break;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    parent.requestDisallowInterceptTouchEvent(false);
                    if (isClick || motionEvent.getEventTime() - motionEvent.getDownTime() < CLICK_TIME) {
                        performClick();
                    } else {
                        switch (dragType) {
                            case RESIZE_TOP:
                                event.setStart(new DateTime(MainActivity.convertYToTime((int) getY())));
                                break;
                            case MOVE:
                                event.setStart(new DateTime(MainActivity.convertYToTime((int) getY())));
                                event.setEnd(new DateTime(MainActivity.convertYToTime((int) (getY() + getHeight()))));
                                break;
                            case RESIZE_BOT:
                                event.setEnd(new DateTime(MainActivity.convertYToTime((int) (getY() + getHeight()))));
                                break;
                        }
                    }
                    break;
            }
        }
        return true;
    }

    private float roundToNotch(float y) {
        float mod = (y - Y_OFFSET) % MIN_HEIGHT;
        if (mod < MIN_HEIGHT / 2F) {
            return y - mod;
        } else {
            return y + (MIN_HEIGHT - mod);
        }
    }

    /**
     * Getter method for the event variable
     * @return TimeBlock object that corresponds to the event this represents
     */
    public TimeBlock getEvent() {
        return event;
    }

    /**
     * Setter method for the editMode variable
     * @param editMode true if this DraggableButton can now be dragged, false otherwise
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    @Override
    // onTouchEvent warning says this needs to be included
    public boolean performClick() {
        return super.performClick();
    }
}
