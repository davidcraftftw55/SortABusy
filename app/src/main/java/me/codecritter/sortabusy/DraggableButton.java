package me.codecritter.sortabusy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

/**
 * A custom implementation of a Button which allows the user to drag on it to either resize or move it
 */
public class DraggableButton extends AppCompatButton {

    private static final int MIN_HEIGHT = 200;

    private enum DRAG_TYPE { MOVE, RESIZE_TOP, RESIZE_BOT }

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
     */
    public DraggableButton(@NonNull Context context) {
        super(context);
    }

    /**
     * Constructs this button (typically used by LayoutInflaters)
     * @param context context needed
     * @param attrs set of attributes assigned by the layout file
     */
    public DraggableButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructs this button (typically used by LayoutInflaters), with a specified default style
     * @param context context needed
     * @param attrs set of attributes assigned by the layout file
     * @param defStyleAttr resource id for a default style (0 to keep current default style)
     */
    public DraggableButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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
                isClick = false;

                switch(dragType) {
                    case RESIZE_TOP:
                        float newTop = event.getRawY() - dY - tapDy;
                        int heightIncrease = Math.round(getY() - newTop);

                        if (getHeight() + heightIncrease > MIN_HEIGHT) {
                            setY(newTop);

                            ViewGroup.LayoutParams params = getLayoutParams();
                            params.height += heightIncrease;
                            setLayoutParams(params);
                        }
                        break;
                    case MOVE:
                        setY(event.getRawY() - dY - tapDy);
                        break;
                    case RESIZE_BOT:
                        int newHeight = Math.round(event.getRawY() - dY - tapDy + origHeight - getY());
                        if (newHeight > 200) {
                            ViewGroup.LayoutParams params = getLayoutParams();
                            params.height = newHeight;
                            setLayoutParams(params);
                        }
                        break;
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isClick) {
                    performClick();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
