package cz.datalite.helpers;

import java.text.Normalizer;

/**
 * Funkce pro práci se soubory
 *
 * @author Michal Pavlusek
 */
public abstract class FileHelper {

    /**
     * Funkce rozpoznává základní content type a vrací ho jako string.
     * For  mor information goto <a href="http://en.wikipedia.org/wiki/Internet_media_type">Wikipedia / Internet_media_type</a>.
     * @param filename celé jméno souboru (obrazek.jpg)
     * @return string contentType (image/jpeg)
     */
    public static String getContentType(String filename)
    {
        String koncovka = filename.substring(filename.lastIndexOf(".")+1, filename.length());

        // FORMÁTY OBRÁZKŮ
        if("jpg".equalsIgnoreCase(koncovka))
        {
            return "image/jpeg";
        }
        else if("gif".equalsIgnoreCase(koncovka))
        {
            return "image/gif";
        }
        else if("png".equalsIgnoreCase(koncovka))
        {
            return "image/png";
        }
        // ADOBE FORMÁTY
        else if("pdf".equalsIgnoreCase(koncovka))
        {
            return "application/pdf";
        }
        // MICROSOFT OFFICE FORMÁTY
        else if("xls".equalsIgnoreCase(koncovka) || "xlsx".equalsIgnoreCase(koncovka))
        {
            return "application/vnd.ms-excel";
        }
        else if("ppt".equalsIgnoreCase(koncovka) || "pps".equalsIgnoreCase(koncovka) || "ppsx".equalsIgnoreCase(koncovka) || "pptx".equalsIgnoreCase(koncovka))
        {
            return "application/vnd.ms-powerpoint";
        }
        else if("doc".equalsIgnoreCase(koncovka) || "docx".equalsIgnoreCase(koncovka))
        {
            return "application/msword";
        }
        // ARCHIVY
        else if("rar".equalsIgnoreCase(koncovka))
        {
            return "application/x-rar-compressed";
        }
        else if("zip".equalsIgnoreCase(koncovka))
        {
            return "application/zip";
        }
        // POŠTA
        else if("xul".equalsIgnoreCase(koncovka))
        {
            return "application/vnd.mozilla.xul+xml";
        }
        // OSTATNÍ
        else if("txt".equalsIgnoreCase(koncovka))
        {
            return "text/plain";
        }
        else
        {
            return null;
        }
    }

    /**
     * Funkce zjistí jakého je cílový soubor typu
     * @param filename je název souboru na serveru
     * @return známý typ souboru nebo jeho koncovka převedená na uppercase
     */
    public static String getFileType(String filename)
    {
        String koncovka = filename.substring(filename.lastIndexOf(".")+1, filename.length());

        if("xls".equalsIgnoreCase(koncovka) || "xlsx".equalsIgnoreCase(koncovka))
        {
            return "Excel";
        }
        else if("ppt".equalsIgnoreCase(koncovka) || "pps".equalsIgnoreCase(koncovka) || "ppsx".equalsIgnoreCase(koncovka) || "pptx".equalsIgnoreCase(koncovka))
        {
            return "PowerPoint";
        }
        else if("doc".equalsIgnoreCase(koncovka) || "docx".equalsIgnoreCase(koncovka))
        {
            return "Word";
        }
        else
        {
            return koncovka.toUpperCase();
        }
    }

    /**
     * Odstrani z nazvu souboru znaky nevhodne pro ulozeni stahovaneho souboru. (Vcetne lomitek cesty
     * @param filename
     * @return vycisteny nazev
     */
    public static String cleanFilename(String filename) {
        if (filename == null) {
            return null;
        }
        filename =  Normalizer.normalize(filename, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }


}
