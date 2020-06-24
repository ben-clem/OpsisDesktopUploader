package com.opsomai.opsisdesktopuploader.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.util.Exceptions;

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

    private JButton openButton;
    private JPanel filesPanel;

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

        choicePanel.add(Box.createRigidArea(new Dimension(5, 0)));

        JLabel lab1 = new JLabel("Choisir des fichiers : ");
        choicePanel.add(lab1);

        try {
            // File Chooser
            openButton = new JButton("Select files...",
                    createImageIcon("img/Open16.gif"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        choicePanel.add(openButton);
        
        JLabel lab2 = new JLabel(" ou les glisser-déposer en dessous :");
        choicePanel.add(lab2);

        // files uploader
        // adding choice Panel
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.BOTH;
        c2.gridx = 0;
        c2.gridy = 1;
        c2.gridwidth = 3;
        c2.weighty = 0.08;
        c2.insets = new Insets(0, 5, 0, 5);
        c2.anchor = GridBagConstraints.PAGE_START;
        this.add(choicePanel, c2);

        // upload panel
        filesPanel = new JPanel();
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

    public void showMedias(ArrayList<File> medias) {
        for (File f : medias) {
            JLabel m = new JLabel(f.toString());
            filesPanel.add(m);
            System.out.println("-- trying to add a media to the filesPanel");
        }
    }
    
    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path
     * @return
     * @throws java.io.FileNotFoundException
     */
    protected static ImageIcon createImageIcon(String path) throws FileNotFoundException, IOException {
        Image img = ImageIO.read(new FileInputStream(path));
        img = img.getScaledInstance(-1, 16, Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(img);
        if (img != null) {
            return logo;
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    /**
     * adds an ActionListener to the open button
     *
     * @param listenForOpenButton ActionListener added by the controller
     */
    public void addOpenButtonListener(ActionListener listenForOpenButton) {

        openButton.addActionListener(listenForOpenButton);

    }

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    /**
     * @return the open button
     */
    public JButton getOpenButton() {
        return openButton;
    }
}
