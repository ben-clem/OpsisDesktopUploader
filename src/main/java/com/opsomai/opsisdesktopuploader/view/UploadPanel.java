package com.opsomai.opsisdesktopuploader.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Upload Screen
 */
public final class UploadPanel extends JPanel {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private String name;
    private String url;

    private Color bg = new Color(255, 255, 255);
    private Color fg = new Color(220, 0, 0);

    //////////////
    // METHODES //
    //////////////
    public UploadPanel(String name, String url) {

        // background and layout
        setBackground(bg);
        setLayout(new GridBagLayout());
        

        JLabel topLabel = new JLabel("Connecté à la médiathèque : " + name + " (" + url + ")");
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 3;
        this.add(topLabel, c1);
        
        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.LINE_AXIS));
        choicePanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, Color.BLACK));
        
        
        JLabel lab1 = new JLabel("Choisir des fichiers : ");
        choicePanel.add(lab1);
        
        // files uploader
        
        // adding choice Panel
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridx = 0;
        c2.gridy = 1;
        c2.gridwidth = 3;
        c2.ipadx = 50;
        c2.ipady = 50;
        c2.anchor = GridBagConstraints.PAGE_START;
        this.add(choicePanel, c2);
        
        // upload panel
        JPanel filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.PAGE_AXIS));
        
        filesPanel.setBackground(fg);
        filesPanel.setMinimumSize(new Dimension(500, 100));
        
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 2;
        c3.gridwidth = 3;
        c3.ipadx = 50;
        c3.ipady = 50;
        this.add(filesPanel, c3);
        
        // buttons
        
        JButton start = new JButton("Démarrer l'upload");
        GridBagConstraints c4 = new GridBagConstraints();
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.gridx = 1;
        c4.gridy = 3;
        this.add(start, c4);
        
        JButton cancel = new JButton("X Annuler");
        GridBagConstraints c5 = new GridBagConstraints();
        c5.fill = GridBagConstraints.HORIZONTAL;
        c5.gridx = 2;
        c5.gridy = 3;
        this.add(cancel, c5);
    }

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
}
