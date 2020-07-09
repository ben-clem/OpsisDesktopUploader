package com.opsomai.opsisdesktopuploader.model;

/**
 * utility
 */
public class ProgressPair {

    private int index;
    private int progress;
    
    ProgressPair(int index, int progress) {
        this.index = index;
        this.progress = progress;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public int getProgress() {
        return this.progress;
    }
    
}
