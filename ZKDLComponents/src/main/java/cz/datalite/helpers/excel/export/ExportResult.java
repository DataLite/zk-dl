package cz.datalite.helpers.excel.export;

import org.zkoss.util.media.AMedia;

public class ExportResult
{
    private AMedia media ;
    private int rows ;

    public ExportResult(AMedia media, int rows)
    {
        this.media = media;
        this.rows = rows;
    }

    public AMedia getMedia()
    {
        return media;
    }

    public int getRows()
    {
        return rows;
    }
}
