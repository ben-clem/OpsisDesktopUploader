package com.opsomai.opsisdesktopuploader.model;

import java.io.File;
import java.util.ArrayList;

/**
 * Classe médias package model
 */
public class Medias {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private ArrayList<File> medias = new ArrayList<>();

    //////////////
    // METHODES //
    //////////////
    /**
     * base constructor
     */
    public Medias() {
    }

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    public void addFile(File f) {
        this.medias.add(f);

        System.out.println("-- adding file :" + f);
    }
    
    public ArrayList<File> getMedias() {
        return medias;
    }
}
