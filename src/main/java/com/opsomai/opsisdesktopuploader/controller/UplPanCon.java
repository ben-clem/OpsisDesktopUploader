package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.view.UploadPanel;

/**
 * Upload Panel Controller
 */
public class UplPanCon extends PanCon {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private final UploadPanel theView;

    //////////////////////
    // ACTION LISTENERS //
    //////////////////////
    
    //////////////
    // METHODES //
    //////////////
    /**
     * base constructor
     *
     * @param theView
     */
    public UplPanCon(UploadPanel theView) {

        this.theView = theView;

    }

}
