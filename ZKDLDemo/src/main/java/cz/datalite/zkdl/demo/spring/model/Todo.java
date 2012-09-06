package cz.datalite.zkdl.demo.spring.model;

import cz.datalite.helpers.EqualsHelper;
import cz.datalite.helpers.JpaEqualHelper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;


/**
 * Basic JPA entity for Xxx - mapped to a database table.
 *
 * @author Jiri Bubnik
 */
@Entity
public class Todo implements Serializable
{
    /** Primary key (automaticaly generated). */
    @Id
    @GeneratedValue
    private Long idTodo;

    /** Todo's name */
    String name;

    /** Todo's description */
    String description;

    /** Todo is due to */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Date targetDate;


   /***************** Serializable / Equals / Hash / toString *****************/

    /** Serial version. */
    private static final long serialVersionUID = 31012010L;

    @Override
    public boolean equals(Object obj)
    {
       return this == obj || (JpaEqualHelper.isEntitySameClass(this, obj) &&
       EqualsHelper.isEquals(this.idTodo,((Todo)obj).getIdTodo()));
    }

    @Override
    public int hashCode()
    {
       return this.idTodo == null ? super.hashCode() : this.idTodo.hashCode();
    }

    @Override
    public String toString()
    {
        return "cz.datalite.zk.example.model.Todo[id=" + this.idTodo + "][val=" + getName() + "]";
    }


    /*************************** SETTERS & GETTERS ****************************/

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