package cz.datalite.mime;

/**
 * An Internet media type, originally called a MIME type after MIME
 * (Multipurpose Internet Mail Extensions) and sometimes a Content-type.
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Internet_media_type">wiki Internet Media Type</a>
 * </p>
 *
 * @author mstastny
 */
public enum InternetMediaType {
	/**
	 * application/pdf
	 */
	PDF("application/pdf", "pdf"),
	/**
	 * application/xhtml+xml
	 */
	XHTML_XML("application/xhtml+xml", "xhtml"),
	/**
	 * application/vnd.android.package-archive - android application
	 */
	APK("application/vnd.android.package-archive", "apk"),
	/**
	 * image/jpeg
	 */
	JPEG("image/jpeg", "jpg"),
	/**
	 * image/png
	 */
	PNG("image/png", "png"),
	/**
	 * application/zip
	 */
	ZIP("application/zip", "zip"),
	/**
	 * text/html
	 */
	HTML("text/html", "html"),
	/**
	 * text/plain
	 */
	PLAIN_TEXT("text/plain", "txt"),
	/**
	 * text/xml
	 */
	XML("text/xml", "xml"),
	/**
	 * application/vnd.ms-excel
	 */
	MS_EXCEL("application/vnd.ms-excel", "xls"),
	/**
	 * application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
	 */
	MS_EXCEL_2007(
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"xlsx"),
	/**
	 * application/msword
	 */
	MS_WORD("application/msword", "doc"),
	/**
	 * application/vnd.openxmlformats-officedocument.wordprocessingml.document
	 */
	MS_WORD_2007(
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"docx"),
	/**
	 * application/vnd.ms-powerpoint
	 */
	MS_POWERPOINT("application/vnd.ms-powerpoint", "ppt"),

	/**
	 * Binarni data
	 */
	X_DOSEXEC("application/x-dosexec", "exe"),

	/**
	 * Comma separated values
	 */
	CSV("text/csv", "csv");

	private String contentType;

	/**
	 * Obvykla pripona nazvu souboru
	 */
	private String filenameExtension;

	/**
	 * @param contentType
	 * @param filenameExtension
	 */
	InternetMediaType(String contentType, String filenameExtension) {
		this.contentType = contentType;
		this.filenameExtension = filenameExtension;
	}

	/**
	 * @param value
	 * @return
	 */
	public static InternetMediaType getByContentType(String value) {

		for (InternetMediaType type : values()) {
			if (type.getContentType().equals(value)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return the filenameExtension
	 */
	public String getFilenameExtension() {
		return filenameExtension;
	}

	/**
	 * @return Klic polozky enumu, pouzitelny k lokalizaci
	 */
	public String getLocalizationKey() {
		return String.format("%s.%s", getClass().getName(), this.name());
	}
}
