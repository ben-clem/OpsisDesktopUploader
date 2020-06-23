package com.opsomai.opsisdesktopuploader.view;

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

        setTitle("Opsis Desktop Uploader");
        setSize(960, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
