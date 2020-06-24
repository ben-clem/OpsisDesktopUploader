package com.opsomai.opsisdesktopuploader.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
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
        //c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 3;
        c1.weighty = 0.02;
        c1.insets = new Insets(5, 5, 0, 5);
        c1.anchor = GridBagConstraints.CENTER;
        this.add(topLabel, c1);

        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.LINE_AXIS));
        choicePanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));

        JLabel lab1 = new JLabel("Choisir des fichiers : ");
        choicePanel.add(lab1);

        // files uploader
        // adding choice Panel
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.BOTH;
        c2.gridx = 0;
        c2.gridy = 1;
        c2.gridwidth = 3;
        c2.weighty = 0.1;
        c2.insets = new Insets(0, 5, 0, 5);
        c2.anchor = GridBagConstraints.PAGE_START;
        this.add(choicePanel, c2);

        // upload panel
        JPanel filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.PAGE_AXIS));

        filesPanel.setBackground(fg);
        //filesPanel.setMinimumSize(new Dimension(500, 100));

        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.BOTH;
        c3.gridx = 0;
        c3.gridy = 2;
        c3.gridwidth = 3;
        c3.weighty = 1;
        c3.insets = new Insets(5, 5, 5, 5);
        this.add(filesPanel, c3);

        // buttons
        Component box = Box.createHorizontalStrut(1);
        GridBagConstraints c6 = new GridBagConstraints();
        c6.fill = GridBagConstraints.BOTH;
        c6.gridx = 0;
        c6.gridy = 3;
        c6.weightx = 0.3;
        this.add(box, c6);

        JButton start = new JButton("Démarrer l'upload");
        GridBagConstraints c4 = new GridBagConstraints();
        c4.fill = GridBagConstraints.BOTH;
        c4.gridx = 1;
        c4.gridy = 3;
        c4.gridwidth = 1;
        c4.gridheight = 1;
        c4.weightx = 0.5;
        c4.weighty = 0.05;
        c4.insets = new Insets(5, 5, 10, 5);
        this.add(start, c4);

        JButton cancel = new JButton("X Annuler");
        GridBagConstraints c5 = new GridBagConstraints();
        c5.fill = GridBagConstraints.BOTH;
        c5.gridx = 2;
        c5.gridy = 3;
        c5.weightx = 0.2;
        c5.insets = new Insets(5, 5, 10, 5);
        this.add(cancel, c5);
    }

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
}
