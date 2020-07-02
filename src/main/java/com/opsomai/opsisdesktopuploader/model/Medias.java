package com.opsomai.opsisdesktopuploader.model;

import com.opsomai.opsisdesktopuploader.controller.UplPanCon;
import com.opsomai.opsisdesktopuploader.view.UploadPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.openide.util.Exceptions;

/**
 * Classe médias package model
 */
public final class Medias {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private ArrayList<Media> medias = new ArrayList<>();
    private ArrayList<Thumbnail> thumbnails = new ArrayList<>();

    private UploadPanel theView;
    private UplPanCon theController;

    ////////////////////
    // NESTED CLASSES //
    ////////////////////
    public class ThumbnailsWorker extends SwingWorker<ArrayList<Thumbnail>, Thumbnail> {

        public ThumbnailsWorker() {
            // Init
        }

        @Override
        public ArrayList<Thumbnail> doInBackground() throws Exception {

            ArrayList<Thumbnail> generatedThumbnails = new ArrayList<>();

           
            
            // Task
            medias.forEach(media -> {

                Thumbnail thumbnail = thumbnails.get(media.getIndex());

                // is the thumbnail generated
                if (thumbnail.getIcon() == null) {
                    System.out.println("\n_Thumbnail n°" + media.getIndex() + "(" + thumbnail.getIndex() + ") is null --> starting creating one");

                    try {

                        thumbnail = createThumbnail(media);

                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    publish(thumbnail);

                    generatedThumbnails.add(thumbnail);

                } else {
                    System.out.println("\n_Thumbnail n°" + media.getIndex() + "(" + thumbnail.getIndex() + ") is already there");
                
                    thumbnail = thumbnails.get(media.getIndex());
                    
                    thumbnail.setIndex(media.getIndex());
                    
                    publish(thumbnail);
                    
                    generatedThumbnails.add(thumbnail);
                    
                }

            });

            // Task end
            return generatedThumbnails;

        }

        @Override
        public void process(List<Thumbnail> chunks) {

            chunks.forEach(thumbnail -> {

                theView.addThumbnail(thumbnail);

                System.out.println("\n_asking for reload from model");

                theController.setNeedRefresh(true);
                theController.setRefreshType("reloadUploadPanel");

            });

        }

        @Override
        protected void done() {

            ArrayList<Thumbnail> result = new ArrayList<>();

            try {

                result = get();

            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }

            thumbnails = result;

            System.out.println("_Task finished");
        }

    }

    //////////////
    // METHODES //
    //////////////
    /**
     * base constructor
     *
     * @param theView
     * @param theController
     */
    public Medias(UploadPanel theView, UplPanCon theController) {

        this.theView = theView;
        this.theController = theController;
    }

    /**
     * creating medias thumbnails in a separate thread
     *
     * @param media
     * @return
     * @throws java.io.FileNotFoundException
     */
    public Thumbnail createThumbnail(Media media) throws FileNotFoundException, IOException {

        // what type of file is it
        String mimetype = new MimetypesFileTypeMap().getContentType(media.getFile());
        System.out.println("_mimetype = " + mimetype);

        // Fixing missing MIME type
        if ("application/octet-stream".equals(mimetype)) {

            String extension = FilenameUtils.getExtension(media.getFile().getName());

            MimeTypesFixer fixer = new MimeTypesFixer();

            try {
                mimetype = fixer.getMap().get(extension).toString();
                System.out.println("_fixed missing mimetype = " + mimetype);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.out.println("_unable to fix MIME type, keeping: " + mimetype);
            }
        }

        String type = mimetype.split("/")[0];

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        switch (type) {

            case "image":

                System.out.println("_it's an image, starting thumbnail creation");

                // Image scaling without loading into memory
                // https://stackoverflow.com/questions/10817597/java-image-scaling-without-loading-the-whole-image-into-memory
                FileInputStream fin = new FileInputStream(media.getFile());

                ImageInputStream iis = ImageIO.createImageInputStream(fin);

                Iterator iter = ImageIO.getImageReaders(iis);
                if (!iter.hasNext()) {
                    break;
                }

                ImageReader reader = (ImageReader) iter.next();

                ImageReadParam params = reader.getDefaultReadParam();

                reader.setInput(iis, true, true);

                // Getting intermediate image with size divided by 4
                params.setSourceSubsampling(4, 4, 0, 0);

                img = reader.read(0, params);

                break;

            case "video":
                // If it's a video
                System.out.println("_it's a video, starting thumbnail creation");

                FFmpegFrameGrabber g = new FFmpegFrameGrabber(media.getFile());

                g.start();

                Java2DFrameConverter c = new Java2DFrameConverter();

                img = c.convert(g.grabKeyFrame());

                g.stop();

                // TODO: if unable to get keyFrame -> put genreic video logo
                break;

            default:
                // Else --> generic file icon
                System.out.println("_it's any other file type, putting generic file icon");

                BufferedImage fileLogo = ImageIO.read(new FileInputStream("img/icons8-file-240.png"));

                img = fileLogo;

                break;
        }

        // Determining final Width and Height :
        Dimension dim = new Dimension(img.getWidth(), img.getHeight());

        Dimension newDim = getScaledDimension(dim, new Dimension(100, 100));

        Image newImg = img.getScaledInstance((int) newDim.getWidth(), (int) newDim.getHeight(), Image.SCALE_SMOOTH);

//
//      Abandonned because wasn't pretty and made the thumbnail off-centered      
//
//        if ("video".equals(type)) {
//
//            // Adding a video logo to the video thumbnail
//            // Converting the resized image to a buffered image for logo application
//            // Create a buffered image with transparency
//            BufferedImage bimage = new BufferedImage(newImg.getWidth(null), newImg.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//
//            // Draw the image on to the buffered image
//            Graphics2D bGr = bimage.createGraphics();
//            bGr.drawImage(newImg, 0, 0, null);
//            bGr.dispose();
//            // bimage is the converted BufferedImage to use next
//
//            // Loading Logo
//            BufferedImage videoLogo = ImageIO.read(new FileInputStream("img/icons8-video-100.png"));
//
//            // Scaling down
//            BufferedImage after = new BufferedImage(videoLogo.getWidth(), videoLogo.getHeight(), BufferedImage.TYPE_INT_ARGB);
//            AffineTransform at = new AffineTransform();
//            at.scale(0.35, 0.35);
//            AffineTransformOp scaleOp
//                    = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//            after = scaleOp.filter(videoLogo, after);
//
//            // Applying the logo
//            // create the new image, canvas size is the max. of both image sizes
//            int w = Math.max(bimage.getWidth(), after.getWidth());
//            int h = Math.max(bimage.getHeight(), after.getHeight());
//            BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//
//            // paint both images, preserving the alpha channels
//            Graphics g = combined.getGraphics();
//            g.drawImage(bimage, 0, 0, null);
//            g.drawImage(after, 0, 0, null);
//
//            g.dispose();
//
//            newImg = combined;
//
//        }
//
        ImageIcon icon = new ImageIcon(newImg);

        Thumbnail genThumb = new Thumbnail(media.getIndex(), icon);
        
        System.out.println("_creating thumbnail: " + media.getIndex());

        // Fin
        return genThumb;

    }

    /**
     * Get scaled dimensions within boundaries while conserving aspect ratio
     *
     * @param imgSize
     * @param boundary
     * @return
     */
    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    public void addMedia(File file) {

        Integer index;

        if (this.medias.isEmpty()) {
            index = 0;
            System.out.println("_medias is empty: index = " + index);
        } else {
            index = this.medias.size();
            System.out.println("_medias is not empty: index = " + index);
        }

        Media media = new Media(index, file);

        this.medias.add(media);

        Thumbnail thumbnail = new Thumbnail(index);

        this.thumbnails.add(thumbnail);

    }
    
    public void sortAllByIndex() {
        
        this.medias.sort(Comparator.comparing(Media::getIndex));
        this.thumbnails.sort(Comparator.comparing(Thumbnail::getIndex));
        
    }

    public ArrayList<Media> getMedias() {
        return this.medias;
    }

}
