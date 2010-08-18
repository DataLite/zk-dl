package cz.datalite.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pomocne funkce pro vypocet hashe
 * 
 * @author Jiri Bubnik
 */
public class HashCalcHelper {

    /**
     * Zakoduje zpravu a vrati ji jako string pomoci algoritmu SHA-512
     * 
     * @param message zprava
     * @return kod zpravy jako string
     */
    public static String encodeSHA512(String message)
    {
        return toHexString(getEncodedBytes(message, "SHA-512"));
    }

     /**
     * Zakoduje zpravu a vrati ji jako pole byte pomoci algoritmu SHA-512
     *
     * @param message zprava
     * @return kod zpravy jako pole byte
     */
    public static byte[] encodeByteSHA512(String message)
    {
        return getEncodedBytes(message, "SHA-512");
    }

    /**
     * Zakoduje zpravu a vrati ji jako string pomoci algoritmu MD5
     *
     * @param message zprava
     * @return kod zpravy jako string
     */
    public static String encodeMD5(String message)
    {
        return toHexString(getEncodedBytes(message, "MD5"));
    }


    /**
     * Funkce provede vlastni vypocet hashe na zaklade vstupniho stringu a algoritmu
     *
     * @param message zprava pro zakodovani
     * @param algorithm algoritms (napr "MD5" nebo "SHA-512" - viz. MessageDigest.getInstance() )
     * @return zakodovani retezec jako pole bytu
     */
    protected static byte[] getEncodedBytes(String message, String algorithm) {
        byte[] buffer = message.getBytes();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
            return md.digest(buffer);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HashCalcHelper.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex.getLocalizedMessage());
        }
    }

    /**
     * Prevede zakodovany stringu z pole bytu zpet na aktualni string (jako je napr. ulozeny v DB)
     *
     * @param v zakodovany retezec
     * @return vraceny jako string
     */
    protected static String toHexString( byte [] v ){
		StringBuffer	sb = new StringBuffer ();
		byte		n1, n2;

		for (int c = 0; c < v.length; c++)
		{
			n1 = (byte)((v[c] & 0xF0) >>> 4); // This line was changed
			n2 = (byte)((v[c] & 0x0F)); // So was this line

			sb.append (n1 >= 0xA ? (char)(n1 - 0xA + 'a') : (char)(n1 + '0'));
			sb.append (n2 >= 0xA ? (char)(n2 - 0xA + 'a') : (char)(n2 + '0'));
		}
		
		return sb.toString();
	}


}
