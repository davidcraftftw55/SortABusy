package me.codecritter.sortabusy;

/**
 * Interface to be implemented by activities that display TimeBlocks
 */
public interface TimeBlockDisplayer {
    /**
     * Event method to be called when a TimeBlock this displayer should be displaying is moved
     */
    void onTimeBlockMoved();
}
