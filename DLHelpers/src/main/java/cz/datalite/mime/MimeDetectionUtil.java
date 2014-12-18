package cz.datalite.mime;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.datalite.check.Checker;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.MimeUtil2;

/**
 * Pomocna trida pro detekci MIME typu.
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public final class MimeDetectionUtil {

	private static final Logger logger = LoggerFactory.getLogger(MimeDetectionUtil.class);

	/**
	 * 
	 */
	private MimeDetectionUtil() {
		// Bez komentare
	}

	static {
		// Registrace detektoru
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
	}

	/**
	 * 
	 * @param fileName
	 * @return MIME typ
	 */
	@SuppressWarnings("unchecked")
	public static String resolveMimeType(String fileName) {
		return getMimeType(MimeUtil.getMimeTypes(fileName));
	}

	/**
	 * 
	 * @param data
	 * @return MIME typ
	 */
	@SuppressWarnings("unchecked")
	public static String resolveMimeType(byte[] data) {
		return getMimeType(MimeUtil.getMimeTypes(data));
	}

	/**
	 * 
	 * @param types
	 * @return
	 */
	private static String getMimeType(Collection<MimeType> types) {

		if (Checker.isNullOrEmpty(types)
				|| (types.size() == 1 && (types.iterator().next()).equals(MimeUtil2.UNKNOWN_MIME_TYPE))) {
			logger.warn(String.format("Unknown mime [%s]", types));
			return null;
		}
		MimeType mimeType = MimeUtil.getMostSpecificMimeType(types);
		logger.debug("Resolved mime type: {}", mimeType);
		return mimeType.toString();
	}

}
