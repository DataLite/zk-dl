package cz.datalite.zkdl.demo.zkDlComponents;

import java.util.Date;

/**
 * ToDo as a simple POJO (no dependency to any library)
 *
 * @author Jiri Bubnik
 */
public class SimpleTodo
{
    String name;

    String description;

    Date targetDate;

    public SimpleTodo() { }

    public SimpleTodo(String name, String description, Date targetDate)
    {
        this.name = name;
        this.description = description;
        this.targetDate = targetDate;
    }

    /*************************** SETTERY & GETTERY ****************************/

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getTargetDate()
    {
        return targetDate;
    }

    public void setTargetDate(Date targetDate)
    {
        this.targetDate = targetDate;
    }

}