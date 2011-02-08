package cz.datalite.zkdl.demo.hibernateDao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;


/**
 * Basic JPA/Hibernate entity, nothing special here.
 *
 * @author Jiri Bubnik
 */
@Entity
public class HibernateTodo implements Serializable
{
    /** Primární klíč (automaticky generovaný). */
    @Id
    @GeneratedValue
    private Long idTodo;

    String name;

    String description;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Date targetDate;

    public HibernateTodo() { }

    public HibernateTodo(String name, String description, Date targetDate)
    {
        this.name = name;
        this.description = description;
        this.targetDate = targetDate;
    }

    /*************************** SETTERY & GETTERY ****************************/

    public Long getIdTodo() {
        return idTodo;
    }

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