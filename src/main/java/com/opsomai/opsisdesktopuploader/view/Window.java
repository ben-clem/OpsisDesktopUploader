package com.opsomai.opsisdesktopuploader.view;

import com.opsomai.opsisdesktopuploader.utility.Global;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 * Main Window
 */
public class Window extends JFrame {

    //////////////
    // METHODES //
    //////////////
    /**
     * default constructor
     */
    public Window() {
        initWindow();
    }

    /**
     * initialisation de la fenêtre
     */
    public final void initWindow() {
        
        Image icon = Toolkit.getDefaultToolkit().getImage(Global.getWorkingDirPrefix() + "resources/img/appLogo.png");  
        setIconImage(icon);

        setTitle("Opsis Desktop Uploader");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
