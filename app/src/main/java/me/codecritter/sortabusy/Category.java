package me.codecritter.sortabusy;

import android.graphics.Color;

public class Category {

    private final String name;
    private Color color; // TASK to be implemented

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
