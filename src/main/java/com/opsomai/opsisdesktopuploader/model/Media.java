package com.opsomai.opsisdesktopuploader.model;

import java.io.File;

/**
 * Classe média package model
 */
public class Media {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private Integer index;
    private File file;

    //////////////
    // METHODES //
    //////////////
    /**
     * base constructor
     *
     * @param index
     * @param file
     */
    public Media(Integer index, File file) {
        this.index = index;
        this.file = file;
    }

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
    
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
