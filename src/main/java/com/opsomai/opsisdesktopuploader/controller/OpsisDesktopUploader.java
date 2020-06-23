package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.view.ConnectionPanel;
import com.opsomai.opsisdesktopuploader.view.UploadPanel;
import com.opsomai.opsisdesktopuploader.view.Window;
import java.time.ZonedDateTime;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Controller de la fenêtre
 */
public class OpsisDesktopUploader {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private Window win;
    
    private Boolean needRefresh;
    private String refreshType;

    //////////////
    // METHODES //
    //////////////
    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args) {

        // Making the look and feel universal
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace(System.err);
        }
        
        // Sets the buttons press key to enter
        UIManager.put("Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
            "ENTER", "pressed",
            "released ENTER", "released"
        }));

        // Instanciating the controller
        OpsisDesktopUploader winCon = new OpsisDesktopUploader();

        // Creating the window
        winCon.win = new Window();

        // Creating the first view
        ConnectionPanel conPan = new ConnectionPanel();
        winCon.win.setContentPane(conPan);

        // Creating its controller
        ConPanCon conPanCon = new ConPanCon(conPan);

        // Showing
        winCon.win.setVisible(true);

        // Starting update loop
        winCon.updateLoop(winCon, conPanCon);

    }

    /**
     * update loop
     */
    private synchronized void updateLoop(OpsisDesktopUploader winCon, PanCon panCon) {

        while (true) { // while app is running

            // delay
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }

            // get refresh info
            needRefresh = panCon.getNeedRefresh();
            refreshType = panCon.getRefreshType();

            if (needRefresh == true) {

                System.out.println("-- needRefresh = " + needRefresh);
                System.out.println("-- updating at " + ZonedDateTime.now());
                System.out.println("-- refreshType = " + refreshType);
                
                switch (refreshType) {

                    case "connection":

                        UploadPanel uploadPanel = new UploadPanel(panCon.getName(), panCon.getUrl());
                        
                        UplPanCon uplPanCon = new UplPanCon(uploadPanel);
                        
                        winCon.win.setContentPane(uploadPanel);
                        winCon.win.setVisible(true);
                        
                        panCon = uplPanCon;
                        
                        break;

//                    case "":
//
//                       
//                        break;
//
//                    case "":
//
//                       
//                        break;
//
//                    case "":
//
//                       
//                        break;
//
//                    case "":
//
//                      
//                        break;
                }

                // updating controller
//                currentUser = panCon.getCurrentUser();

            }
        }

    }
}
