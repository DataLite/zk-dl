package cz.datalite.helpers.excel.export;

import jxl.biff.DisplayFormat;
import jxl.write.DateFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

import java.util.HashMap;
import java.util.Map;

/**
 * JXL formáty nelze sdílet přes více workbook, proto nemohou být jako statické.
 * Pokud se ale vytvářejí pro každou buňku znovu, minimálně pro datum po určitém čase přestane fungovat.
 *
 * Tato třída slouží jako cache pro jeden export (do jednoho sheet/workbook). Nelze ji sdílet přes více
 * exportů.
 */
public class CellFormats {
    Map<DisplayFormat, Map<WritableFont, WritableCellFormat>> cache = new HashMap<DisplayFormat, Map<WritableFont, WritableCellFormat>>();

    private boolean containsCache(DisplayFormat type, WritableFont font) {
        if (!cache.containsKey(type))
            cache.put(type, new HashMap<WritableFont, WritableCellFormat>());

        return cache.get(type).containsKey(font);
    }

    private WritableCellFormat getFromCache(WritableFont font, DisplayFormat format) {
        if (!containsCache(format, font)) {
            cache.get(format).put(font, new WritableCellFormat(font, format));
        }

        return cache.get(format).get(font);
    }

    private WritableCellFormat DATE = new WritableCellFormat(new DateFormat("d.M.yyyy"));
    public WritableCellFormat getDate() {
      return DATE;
    }

    private WritableCellFormat DATE_TIME = new WritableCellFormat(new DateFormat("d.M.yyyy HH:mm"));
    public WritableCellFormat getDateWithTime() {
        return DATE_TIME;
    }

    public WritableCellFormat getFloat(WritableFont font) {
        return getFromCache(font, NumberFormats.FLOAT);
    }


    public WritableCellFormat getString(WritableFont font) {
        return getFromCache(font, NumberFormats.DEFAULT);
    }

    public WritableCellFormat getInteger(WritableFont font) {
        return getFromCache(font, NumberFormats.INTEGER);
    }
}
