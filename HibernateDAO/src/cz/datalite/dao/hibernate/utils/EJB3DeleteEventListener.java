package cz.datalite.dao.hibernate.utils;

import org.hibernate.event.DeleteEvent;

/**
 * We need to override special situation.<br>
 * @OneToMany(mappedBy = "idProperty", cascade=CascadeType.ALL) @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
 *
 * <p>
 * After removing from collection in detached state and calling saveOrUpdate(), performDetachedEntityDeletionCheck throws an exception:
 *   throw new IllegalArgumentException("Removing a detached instance "+ entityName + "#" + id);
 * </p>
 * This behaviour is required by JPA specification, personally I consider it an error.<br/>
 * Hibernate can perform this deletion of detached entity without any problems and it is enabled in version without entity manager.
 * This event listener returns default hibernate behaviour.
 * 
 * @author Jiri Bubnik
 */
public class EJB3DeleteEventListener extends org.hibernate.ejb.event.EJB3DeleteEventListener
{
	@Override
	protected void performDetachedEntityDeletionCheck(DeleteEvent event) {

        }
}
