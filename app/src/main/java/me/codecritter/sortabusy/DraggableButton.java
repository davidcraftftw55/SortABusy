package me.codecritter.sortabusy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

public class DraggableButton extends AppCompatButton {

    private enum DRAG_TYPE { MOVE, RESIZE_TOP, RESIZE_BOT }

    private boolean isClick;
    private float dX;
    private float dY;
    private DRAG_TYPE dragType;

    public DraggableButton(@NonNull Context context) {
        super(context);
    }

    public DraggableButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClick = true;
                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();

                float y = event.getRawY() + dY;
                if (y < getY() + getHeight() * 0.25) {
                    dragType = DRAG_TYPE.RESIZE_TOP;
                } else if (y < getY() + getHeight() * 0.75) {
                    dragType = DRAG_TYPE.MOVE;
                } else {
                    dragType = DRAG_TYPE.RESIZE_BOT;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                isClick = false;
                setX(event.getRawX());
                setY(event.getRawY());
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
