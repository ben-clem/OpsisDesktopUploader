package com.opsomai.opsisdesktopuploader.model;

import javax.swing.ImageIcon;

/**
 * Classe thumbnail package model
 */
public class Thumbnail {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private Integer index = null;
    private ImageIcon icon = null;

    //////////////
    // METHODES //
    //////////////
    /**
     * default constructor
     */
    public Thumbnail() {
    }

    /**
     * constructor with index
     *
     * @param index
     */
    public Thumbnail(Integer index) {
        this.index = index;
    }

    /**
     * base constructor
     *
     * @param index
     * @param icon
     */
    public Thumbnail(Integer index, ImageIcon icon) {
        this.index = index;
        this.icon = icon;
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

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

}
