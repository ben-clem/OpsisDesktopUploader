package com.opsomai.opsisdesktopuploader.controller;

/**
 * Classe abstraite Panel Controller
 */
public abstract class PanCon {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    protected boolean needRefresh = false;
    protected String refreshType;

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    public boolean getNeedRefresh() {
        return this.needRefresh;
    }

    public String getRefreshType() {
        return this.refreshType;
    }

}
