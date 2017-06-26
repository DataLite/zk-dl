package cz.datalite.zk.liferay.mock;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.AddressImpl;
import com.liferay.portal.model.impl.ContactImpl;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.service.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.mockito.Mockito.*;

/**
 *
 * @author Jiri Bubnik
 */
public class UserMockFactory
{
    /**
     * Create a user.
     */
    public User createUserImpl(String prefix, long userId) throws PortalException, SystemException
    {
        User user = spy(new UserImpl());
        user.setNew(false);
        user.setPrimaryKey(userId);
        user.setCompanyId(CompanyMockFactory.DEFAULT_COMPANY_ID);

        user.setFirstName(prefix + " FirstName");
        user.setEmailAddress(prefix + "_email@address.cz");
        user.setScreenName(prefix + " ScreenName");

        user.setComments(prefix + " comments");
        user.setAgreedToTermsOfUse(true);
        user.setCreateDate(new Date());
        user.setDefaultUser(true);
        user.setFacebookId(1234);
        user.setFirstName(prefix + " FirstName");
        user.setGreeting(prefix + " Greeting");
        user.setJobTitle(prefix + " JobTitle");
        user.setLanguageId("en_US");
        user.setLastName(prefix + " LastName");
        user.setLoginIP("1.1.1.1");
        user.setPassword(prefix + "_password");
        user.setTimeZoneId(TimeZone.getDefault().getID());
        user.setUserId(userId);
        user.setUserUuid(prefix + "_UserUUID");
        user.setUuid(prefix + "_UUID");

        when(UserLocalServiceUtil.getService().getUser(userId)).thenReturn(user);
        when(UserLocalServiceUtil.getService().getUserByEmailAddress(CompanyMockFactory.DEFAULT_COMPANY_ID, user.getEmailAddress())).thenReturn(user);
        when(UserLocalServiceUtil.getService().getUserById(userId)).thenReturn(user);
        when(UserLocalServiceUtil.getService().getUserById(CompanyMockFactory.DEFAULT_COMPANY_ID, userId)).thenReturn(user);
        when(UserLocalServiceUtil.getService().getUserByFacebookId(CompanyMockFactory.DEFAULT_COMPANY_ID, user.getFacebookId())).thenReturn(user);

        return user;
    }

    /**
     * Setup the group in GroupLocalServiceUtil.
     */
    public void addUserToGroup(User user, Group group) throws PortalException, SystemException
    {
        when(GroupLocalServiceUtil.getService().getUserGroup(user.getCompanyId(), user.getPrimaryKey())).thenReturn(group);
    }

    /**
     * Contact (call addContactToUser)
     */
    public Contact createContactImpl(String prefix, long contactId) throws PortalException, SystemException
    {
        Contact contact = spy(new ContactImpl());
        contact.setNew(false);
        contact.setPrimaryKey(contactId);
        contact.setCompanyId(CompanyMockFactory.DEFAULT_COMPANY_ID);

        contact.setEmployeeNumber("1234");
        contact.setFirstName(prefix + " FirstName");
        contact.setLastName(prefix + " LastName");
        contact.setUserName(prefix + " UserName");

        when(ContactLocalServiceUtil.getContact(contactId)).thenReturn(contact);

        return contact;
    }

    /**
     * Setup contactId in user.
     */
    public void addContactToUser(Contact contact, User user) throws PortalException, SystemException
    {
        // implemnetation in user - ContactLocalServiceUtil.getContact(getContactId());
        user.setContactId(contact.getContactId());
        when(UserLocalServiceUtil.getService().getUserByContactId(contact.getContactId())).thenReturn(user);
    }


    public Address createAddressImpl(String prefix, long addressId) throws PortalException, SystemException
    {
        Address address = spy(new AddressImpl());
        address.setNew(false);
        address.setPrimaryKey(addressId);
        address.setCompanyId(CompanyMockFactory.DEFAULT_COMPANY_ID);
        
        address.setCity(prefix + " City");
        address.setMailing(true);
        address.setStreet1(prefix + " Street1");
        address.setZip(prefix + " ZIP");

        when(AddressLocalServiceUtil.getService().getAddress(addressId)).thenReturn(address);

        return address;
    }

    public void addAddressToContact(Address address, Contact contact) throws PortalException, SystemException
    {
        List<Address> addresses =  AddressLocalServiceUtil.getAddresses(CompanyMockFactory.DEFAULT_COMPANY_ID, "com.liferay.portal.model.Contact", contact.getContactId());
        if (addresses == null) {
            addresses = new ArrayList<Address>();
        }
        addresses.add(address);

        when(AddressLocalServiceUtil.getAddresses(CompanyMockFactory.DEFAULT_COMPANY_ID, "com.liferay.portal.model.Contact", contact.getContactId())).thenReturn(addresses);
    }

    public void createUserQuery(List<User> users) throws PortalException, SystemException
    {
        User defaultUser = users.get(0);
        long defaultUserId = defaultUser.getPrimaryKey();

        UserLocalService userLocalService = UserLocalServiceUtil.getService();
        when(userLocalService.dynamicQuery(any(DynamicQuery.class))).thenReturn(users);
        when(userLocalService.dynamicQuery(any(DynamicQuery.class), anyInt(), anyInt())).thenReturn(users);
        when(userLocalService.dynamicQuery(any(DynamicQuery.class), anyInt(), anyInt(), any(OrderByComparator.class))).thenReturn(users);
        when(userLocalService.dynamicQueryCount(any(DynamicQuery.class))).thenReturn(Long.valueOf(users.size()));

        when(userLocalService.getCompanyUsers(eq(CompanyMockFactory.DEFAULT_COMPANY_ID), anyInt(), anyInt())).thenReturn(users);
        when(userLocalService.getCompanyUsersCount(CompanyMockFactory.DEFAULT_COMPANY_ID)).thenReturn(users.size());

        when(userLocalService.getDefaultUser(CompanyMockFactory.DEFAULT_COMPANY_ID)).thenReturn(defaultUser);
        when(userLocalService.getDefaultUserId(CompanyMockFactory.DEFAULT_COMPANY_ID)).thenReturn(defaultUserId);

        when(userLocalService.getGroupUsers(anyLong())).thenReturn(users);
        when(userLocalService.getGroupUsersCount(anyLong())).thenReturn(users.size());
        when(userLocalService.getGroupUsersCount(anyLong(), anyInt())).thenReturn(users.size());

        when(userLocalService.getOrganizationUsers(anyLong())).thenReturn(users);
        when(userLocalService.getOrganizationUsersCount(anyLong())).thenReturn(users.size());
        when(userLocalService.getOrganizationUsersCount(anyLong(), anyInt())).thenReturn(users.size());

        when(userLocalService.getRoleUsers(anyLong())).thenReturn(users);
        when(userLocalService.getRoleUsers(anyLong(), anyInt(), anyInt())).thenReturn(users);
        when(userLocalService.getRoleUsersCount(anyLong())).thenReturn(users.size());
        when(userLocalService.getRoleUsersCount(anyLong(), anyInt())).thenReturn(users.size());

        // default user when not found by parameter
        when(userLocalService.getUser(anyLong())).thenReturn(defaultUser);
        when(userLocalService.getUserByContactId(anyLong())).thenReturn(defaultUser);
        when(userLocalService.getUserByEmailAddress(eq(CompanyMockFactory.DEFAULT_COMPANY_ID), anyString())).thenReturn(defaultUser);
        when(userLocalService.getUserByFacebookId(eq(CompanyMockFactory.DEFAULT_COMPANY_ID), anyLong())).thenReturn(defaultUser);
        when(userLocalService.getUserById(anyLong())).thenReturn(defaultUser);
        when(userLocalService.getUserById(eq(CompanyMockFactory.DEFAULT_COMPANY_ID), anyLong())).thenReturn(defaultUser);
        when(userLocalService.getUserIdByEmailAddress(eq(CompanyMockFactory.DEFAULT_COMPANY_ID), anyString())).thenReturn(defaultUserId);
    }

}
