package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.view.ConnectionPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class ConPanCon extends PanCon {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    protected ConnectionPanel theView;
    
    private String url;
    private String api;
    private String nom;

    //////////////////////
    // ACTION LISTENERS //
    //////////////////////
    /**
     * Implementation of the connection button listener
     */
    class ConnButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {

            url = theView.getUrl();
            api = theView.getApi();
            nom = theView.getNom();
            
            System.out.println("-- Connecting as:");
            System.out.println("url = " + url);
            System.out.println("API = " + api);
            System.out.println("nom = " + nom);
            
            // Good jusque là
            // Il faut mtn check que les champs sont bien remplis,
            // que le format est bon
            // puis se connecter et si c'est bon :
            // sauvegarder les infos de connexion dans un fichier et passer à la suite.
            
        }

    }

    //////////////
    // METHODES //
    //////////////
    /**
     * base constructor
     *
     * @param theView
     */
    public ConPanCon(ConnectionPanel theView) {

        this.theView = theView;

        // Connecting the action listener to the view
        this.theView.addConnButtonListener(new ConnButtonListener());

    }
}
