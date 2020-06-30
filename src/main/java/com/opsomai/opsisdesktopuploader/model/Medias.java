package com.opsomai.opsisdesktopuploader.model;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 * Classe médias package model
 */
public class Medias {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private ArrayList<Media> medias = new ArrayList<>();
    private ArrayList<ImageIcon> thumbnails = new ArrayList<>();

    //////////////
    // METHODES //
    //////////////
    /**
     * base constructor
     */
    public Medias() {
    }

    /**
     * creating medias thumbnails in a separate thread
     */
    public void createThumbnails() {
        
        // for every files in the list (check list !empty())
        
        // is the thumbnail generated
        
        // else we need to create it
        
        // what type of file is it (JMimeMagic)
        
        // If it
        
    }

    /**
     * Get scaled dimensions within boundaries while conserving aspect ratio
     *
     * @param imgSize
     * @param boundary
     * @return
     */
    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
    
    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    public void addMedia(File file) {
        
        Integer index;
        
        if (medias.isEmpty()) {
            index = 0;
        } else {
            index = medias.size();
        }
        
        Media media = new Media(index, file);
        
        this.medias.add(media);

    }
    
    public ArrayList<Media> getMedias() {
        return medias;
    }
}
