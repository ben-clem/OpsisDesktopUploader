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
    
    protected String url;
    protected String api;
    protected String nom;

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    public boolean getNeedRefresh() {
        return this.needRefresh;
    }

    public String getRefreshType() {
        return this.refreshType;
    }
    
    public String getName() {
        return this.nom;
    }

    public String getUrl() {
        return this.url;
    }

    public String getApiKey() {
        return this.api;
    }

}
