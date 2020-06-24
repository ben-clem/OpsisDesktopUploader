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
    
    public void setNeedRefresh(Boolean b) {
        needRefresh = b;
    }

    public String getRefreshType() {
        return this.refreshType;
    }
    
    public void setRefreshType(String s) {
        refreshType = s;
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
