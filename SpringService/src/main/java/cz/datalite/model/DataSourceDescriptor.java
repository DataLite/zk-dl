package cz.datalite.model;

/**
 * Popis datového zdroje
 */
@SuppressWarnings("unused")
public class DataSourceDescriptor
{
    String name ;
    String description ;

    public DataSourceDescriptor(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}
