package cz.datalite.zk.components.profile;

public interface ProfileServiceFactory {

	ProfileService create(Object session);

}
