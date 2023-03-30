package me.codecritter.sortabusy;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

/**
 * A custom implementation of a Button which allows the user to drag on it to either resize or move it
 */
public class DraggableButton extends AppCompatButton {

    /**
     * If a button is held for more milliseconds than this, it's a drag not a click
     */
    private static final long CLICK_TIME = 100L; // milliseconds

    private enum DRAG_TYPE { MOVE, RESIZE_TOP, RESIZE_BOT }

    private final ScrollView parent;
    private final int PARENT_HEIGHT;
    private final int PARENT_WIDTH;
    private final int MIN_HEIGHT;
    private final int Y_OFFSET;


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
     * @param parent ScrollView where this button will be added
     * @param parentWidth width of parent view
     * @param parentHeight height of parent view
     * @param minHeight minimum height of this button
     *                  (should be 15 minutes, or 1/4 of the space between lines)
     * @param yOffset y coordinate of the top most block for this button
     */
    public DraggableButton(@NonNull Context context, ScrollView parent, int parentWidth,
                           int parentHeight, ToggleButton editMode, int minHeight, int yOffset) {
        super(context);

        this.parent = parent;
        PARENT_WIDTH = parentWidth;
        PARENT_HEIGHT = parentHeight;
        MIN_HEIGHT = minHeight;
        Y_OFFSET = yOffset;

        this.editMode = false;
        editMode.setOnClickListener(view -> {
            this.editMode = ((ToggleButton) view).isChecked();
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (editMode) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    parent.requestDisallowInterceptTouchEvent(true);
                    isClick = true;
                    int[] coords = new int[2];
                    getLocationOnScreen(coords);
                    float tapDx = event.getRawX() - coords[0];
                    dY = coords[1] - getY();
                    tapDy = event.getRawY() - coords[1];
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
                    if (!isClick || event.getEventTime() - event.getDownTime() > 100) {
                        isClick = false;
                        switch (dragType) {
                            case RESIZE_TOP:
                                // find notch for newTop
                                float newTop = roundToNotch(event.getRawY() - dY - tapDy);

                                int heightIncrease = Math.round(getY() - newTop);
                                if (heightIncrease != 0) {
                                    Log.i("log", getHeight() + ", " + heightIncrease);
                                }
                                if (getHeight() + heightIncrease >= MIN_HEIGHT && newTop > 0) {
                                    setY(newTop);

                                    ViewGroup.LayoutParams params = getLayoutParams();
                                    params.height += heightIncrease;
                                    setLayoutParams(params);
                                }
                                break;
                            case MOVE:
                                float newY = roundToNotch(event.getRawY() - dY - tapDy);
                                if (newY > 0 && newY < PARENT_HEIGHT - getHeight()) {
                                    setY(newY);
                                }
                                break;
                            case RESIZE_BOT:
                                float newBot = roundToNotch(event.getRawY() - dY - tapDy);
                                int newHeight = Math.round(newBot + origHeight - getY());
                                if (newHeight > MIN_HEIGHT && newHeight < PARENT_HEIGHT - getY()) {
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
                    if (isClick || event.getEventTime() - event.getDownTime() < CLICK_TIME) {
                        performClick();
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

    @Override
    // warning on onTouchEvent says this needs to be included
    public boolean performClick() {
        return super.performClick();
    }
}
