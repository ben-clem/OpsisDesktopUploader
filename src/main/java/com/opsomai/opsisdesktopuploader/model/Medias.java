package com.opsomai.opsisdesktopuploader.model;

import com.opsomai.opsisdesktopuploader.controller.UplPanCon;
import com.opsomai.opsisdesktopuploader.view.UploadPanel;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;
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

    private static Map s_mapMimeTypes = null;

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
                    System.out.println("\n-- Thumbnail n°" + media.getIndex() + "(" + thumbnail.getIndex() + ") is null --> starting creating one");

                    try {

                        thumbnail = createThumbnail(media);

                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    publish(thumbnail);

                    generatedThumbnails.add(thumbnail);


                } else {
                    System.out.println("\n-- Thumbnail n°" + media.getIndex() + "(" + thumbnail.getIndex() + ") is already there");
                }

            });

            // Task end
            return generatedThumbnails;

        }

        @Override
        public void process(List<Thumbnail> chunks) {

            chunks.forEach(thumbnail -> {
                
                theView.addThumbnail(thumbnail);

                System.out.println("\n== asking for reload from model");

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
                
                System.out.println("Task finished");
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

        s_mapMimeTypes = new HashMap(161);
        s_mapMimeTypes.put("ai", "application/postscript");
        s_mapMimeTypes.put("aif", "audio/x-aiff");
        s_mapMimeTypes.put("aifc", "audio/x-aiff");
        s_mapMimeTypes.put("aiff", "audio/x-aiff");
        s_mapMimeTypes.put("asc", "text/plain");
        s_mapMimeTypes.put("asf", "video/x.ms.asf");
        s_mapMimeTypes.put("asx", "video/x.ms.asx");
        s_mapMimeTypes.put("au", "audio/basic");
        s_mapMimeTypes.put("avi", "video/x-msvideo");
        s_mapMimeTypes.put("bcpio", "application/x-bcpio");
        s_mapMimeTypes.put("bin", "application/octet-stream");
        s_mapMimeTypes.put("cab", "application/x-cabinet");
        s_mapMimeTypes.put("cdf", "application/x-netcdf");
        s_mapMimeTypes.put("class", "application/java-vm");
        s_mapMimeTypes.put("cpio", "application/x-cpio");
        s_mapMimeTypes.put("cpt", "application/mac-compactpro");
        s_mapMimeTypes.put("crt", "application/x-x509-ca-cert");
        s_mapMimeTypes.put("csh", "application/x-csh");
        s_mapMimeTypes.put("css", "text/css");
        s_mapMimeTypes.put("csv", "text/comma-separated-values");
        s_mapMimeTypes.put("dcr", "application/x-director");
        s_mapMimeTypes.put("dir", "application/x-director");
        s_mapMimeTypes.put("dll", "application/x-msdownload");
        s_mapMimeTypes.put("dms", "application/octet-stream");
        s_mapMimeTypes.put("doc", "application/msword");
        s_mapMimeTypes.put("dtd", "application/xml-dtd");
        s_mapMimeTypes.put("dvi", "application/x-dvi");
        s_mapMimeTypes.put("dxr", "application/x-director");
        s_mapMimeTypes.put("eps", "application/postscript");
        s_mapMimeTypes.put("etx", "text/x-setext");
        s_mapMimeTypes.put("exe", "application/octet-stream");
        s_mapMimeTypes.put("ez", "application/andrew-inset");
        s_mapMimeTypes.put("gif", "image/gif");
        s_mapMimeTypes.put("gtar", "application/x-gtar");
        s_mapMimeTypes.put("gz", "application/gzip");
        s_mapMimeTypes.put("gzip", "application/gzip");
        s_mapMimeTypes.put("hdf", "application/x-hdf");
        s_mapMimeTypes.put("htc", "text/x-component");
        s_mapMimeTypes.put("hqx", "application/mac-binhex40");
        s_mapMimeTypes.put("html", "text/html");
        s_mapMimeTypes.put("htm", "text/html");
        s_mapMimeTypes.put("ice", "x-conference/x-cooltalk");
        s_mapMimeTypes.put("ief", "image/ief");
        s_mapMimeTypes.put("iges", "model/iges");
        s_mapMimeTypes.put("igs", "model/iges");
        s_mapMimeTypes.put("jar", "application/java-archive");
        s_mapMimeTypes.put("java", "text/plain");
        s_mapMimeTypes.put("jnlp", "application/x-java-jnlp-file");
        s_mapMimeTypes.put("jpeg", "image/jpeg");
        s_mapMimeTypes.put("jpe", "image/jpeg");
        s_mapMimeTypes.put("jpg", "image/jpeg");
        s_mapMimeTypes.put("js", "application/x-javascript");
        s_mapMimeTypes.put("jsp", "text/plain");
        s_mapMimeTypes.put("kar", "audio/midi");
        s_mapMimeTypes.put("latex", "application/x-latex");
        s_mapMimeTypes.put("lha", "application/octet-stream");
        s_mapMimeTypes.put("lzh", "application/octet-stream");
        s_mapMimeTypes.put("man", "application/x-troff-man");
        s_mapMimeTypes.put("mathml", "application/mathml+xml");
        s_mapMimeTypes.put("me", "application/x-troff-me");
        s_mapMimeTypes.put("mesh", "model/mesh");
        s_mapMimeTypes.put("mid", "audio/midi");
        s_mapMimeTypes.put("midi", "audio/midi");
        s_mapMimeTypes.put("mif", "application/vnd.mif");
        s_mapMimeTypes.put("mol", "chemical/x-mdl-molfile");
        s_mapMimeTypes.put("movie", "video/x-sgi-movie");
        s_mapMimeTypes.put("mov", "video/quicktime");
        s_mapMimeTypes.put("mp2", "audio/mpeg");
        s_mapMimeTypes.put("mp3", "audio/mpeg");
        s_mapMimeTypes.put("mp4", "video/mp4");
        s_mapMimeTypes.put("mpeg", "video/mpeg");
        s_mapMimeTypes.put("mpe", "video/mpeg");
        s_mapMimeTypes.put("mpga", "audio/mpeg");
        s_mapMimeTypes.put("mpg", "video/mpeg");
        s_mapMimeTypes.put("ms", "application/x-troff-ms");
        s_mapMimeTypes.put("msh", "model/mesh");
        s_mapMimeTypes.put("msi", "application/octet-stream");
        s_mapMimeTypes.put("nc", "application/x-netcdf");
        s_mapMimeTypes.put("oda", "application/oda");
        s_mapMimeTypes.put("ogg", "application/ogg");
        s_mapMimeTypes.put("pbm", "image/x-portable-bitmap");
        s_mapMimeTypes.put("pdb", "chemical/x-pdb");
        s_mapMimeTypes.put("pdf", "application/pdf");
        s_mapMimeTypes.put("pgm", "image/x-portable-graymap");
        s_mapMimeTypes.put("pgn", "application/x-chess-pgn");
        s_mapMimeTypes.put("png", "image/png");
        s_mapMimeTypes.put("pnm", "image/x-portable-anymap");
        s_mapMimeTypes.put("ppm", "image/x-portable-pixmap");
        s_mapMimeTypes.put("ppt", "application/vnd.ms-powerpoint");
        s_mapMimeTypes.put("ps", "application/postscript");
        s_mapMimeTypes.put("qt", "video/quicktime");
        s_mapMimeTypes.put("ra", "audio/x-pn-realaudio");
        s_mapMimeTypes.put("ra", "audio/x-realaudio");
        s_mapMimeTypes.put("ram", "audio/x-pn-realaudio");
        s_mapMimeTypes.put("ras", "image/x-cmu-raster");
        s_mapMimeTypes.put("rdf", "application/rdf+xml");
        s_mapMimeTypes.put("rgb", "image/x-rgb");
        s_mapMimeTypes.put("rm", "audio/x-pn-realaudio");
        s_mapMimeTypes.put("roff", "application/x-troff");
        s_mapMimeTypes.put("rpm", "application/x-rpm");
        s_mapMimeTypes.put("rpm", "audio/x-pn-realaudio");
        s_mapMimeTypes.put("rtf", "application/rtf");
        s_mapMimeTypes.put("rtx", "text/richtext");
        s_mapMimeTypes.put("ser", "application/java-serialized-object");
        s_mapMimeTypes.put("sgml", "text/sgml");
        s_mapMimeTypes.put("sgm", "text/sgml");
        s_mapMimeTypes.put("sh", "application/x-sh");
        s_mapMimeTypes.put("shar", "application/x-shar");
        s_mapMimeTypes.put("silo", "model/mesh");
        s_mapMimeTypes.put("sit", "application/x-stuffit");
        s_mapMimeTypes.put("skd", "application/x-koan");
        s_mapMimeTypes.put("skm", "application/x-koan");
        s_mapMimeTypes.put("skp", "application/x-koan");
        s_mapMimeTypes.put("skt", "application/x-koan");
        s_mapMimeTypes.put("smi", "application/smil");
        s_mapMimeTypes.put("smil", "application/smil");
        s_mapMimeTypes.put("snd", "audio/basic");
        s_mapMimeTypes.put("spl", "application/x-futuresplash");
        s_mapMimeTypes.put("src", "application/x-wais-source");
        s_mapMimeTypes.put("sv4cpio", "application/x-sv4cpio");
        s_mapMimeTypes.put("sv4crc", "application/x-sv4crc");
        s_mapMimeTypes.put("svg", "image/svg+xml");
        s_mapMimeTypes.put("swf", "application/x-shockwave-flash");
        s_mapMimeTypes.put("t", "application/x-troff");
        s_mapMimeTypes.put("tar", "application/x-tar");
        s_mapMimeTypes.put("tar.gz", "application/x-gtar");
        s_mapMimeTypes.put("tcl", "application/x-tcl");
        s_mapMimeTypes.put("tex", "application/x-tex");
        s_mapMimeTypes.put("texi", "application/x-texinfo");
        s_mapMimeTypes.put("texinfo", "application/x-texinfo");
        s_mapMimeTypes.put("tgz", "application/x-gtar");
        s_mapMimeTypes.put("tiff", "image/tiff");
        s_mapMimeTypes.put("tif", "image/tiff");
        s_mapMimeTypes.put("tr", "application/x-troff");
        s_mapMimeTypes.put("tsv", "text/tab-separated-values");
        s_mapMimeTypes.put("txt", "text/plain");
        s_mapMimeTypes.put("ustar", "application/x-ustar");
        s_mapMimeTypes.put("vcd", "application/x-cdlink");
        s_mapMimeTypes.put("vrml", "model/vrml");
        s_mapMimeTypes.put("vxml", "application/voicexml+xml");
        s_mapMimeTypes.put("wav", "audio/x-wav");
        s_mapMimeTypes.put("wbmp", "image/vnd.wap.wbmp");
        s_mapMimeTypes.put("wmlc", "application/vnd.wap.wmlc");
        s_mapMimeTypes.put("wmlsc", "application/vnd.wap.wmlscriptc");
        s_mapMimeTypes.put("wmls", "text/vnd.wap.wmlscript");
        s_mapMimeTypes.put("wml", "text/vnd.wap.wml");
        s_mapMimeTypes.put("wrl", "model/vrml");
        s_mapMimeTypes.put("wtls-ca-certificate", "application/vnd.wap.wtls-ca-certificate");
        s_mapMimeTypes.put("xbm", "image/x-xbitmap");
        s_mapMimeTypes.put("xht", "application/xhtml+xml");
        s_mapMimeTypes.put("xhtml", "application/xhtml+xml");
        s_mapMimeTypes.put("xls", "application/vnd.ms-excel");
        s_mapMimeTypes.put("xml", "application/xml");
        s_mapMimeTypes.put("xpm", "image/x-xpixmap");
        s_mapMimeTypes.put("xpm", "image/x-xpixmap");
        s_mapMimeTypes.put("xsl", "application/xml");
        s_mapMimeTypes.put("xslt", "application/xslt+xml");
        s_mapMimeTypes.put("xul", "application/vnd.mozilla.xul+xml");
        s_mapMimeTypes.put("xwd", "image/x-xwindowdump");
        s_mapMimeTypes.put("xyz", "chemical/x-xyz");
        s_mapMimeTypes.put("z", "application/compress");
        s_mapMimeTypes.put("zip", "application/zip");
    }

    /**
     * creating medias thumbnails in a separate thread
     *
     * @param media
     * @return
     * @throws java.io.FileNotFoundException
     */
    public Thumbnail createThumbnail(Media media) throws FileNotFoundException, IOException {

        Thumbnail genThumb = new Thumbnail();

        // what type of file is it
        String mimetype = new MimetypesFileTypeMap().getContentType(media.getFile());
        System.out.println("*** mimetype = " + mimetype);

        // Fixing missing MIME type
        if ("application/octet-stream".equals(mimetype)) {

            String extension = FilenameUtils.getExtension(media.getFile().getName());

            mimetype = s_mapMimeTypes.get(extension).toString();

            System.out.println("*** Fixed missing mimetype = " + mimetype);
        }

        String type = mimetype.split("/")[0];

        switch (type) {

            case "image":

                System.out.println("It's an image, starting thumbnail creation");

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
                BufferedImage img = reader.read(0, params);
                
                // Determining final Width and Height :
                
                Dimension dim = new Dimension(img.getWidth(), img.getHeight());
                
                Dimension newDim = getScaledDimension(dim, new Dimension(100, 100));
                
                Image newImg = img.getScaledInstance((int) newDim.getWidth(), (int) newDim.getHeight(), Image.SCALE_SMOOTH);
                
                //
                

                ImageIcon icon = new ImageIcon(newImg);

                genThumb = new Thumbnail(media.getIndex(), icon);

                // Fin
                break;

            case "video":
                // If it's a video
                System.out.println("It's a video, starting thumbnail creation");
                break;

            default:
                // Else --> generic file icon
                System.out.println("It's any other file type, putting generic file icon");
                break;
        }

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

        if (medias.isEmpty()) {
            index = 0;
        } else {
            index = medias.size();
        }

        Media media = new Media(index, file);

        this.medias.add(media);

        Thumbnail thumbnail = new Thumbnail(index);

        this.thumbnails.add(thumbnail);

    }

    public ArrayList<Media> getMedias() {
        return medias;
    }
}
