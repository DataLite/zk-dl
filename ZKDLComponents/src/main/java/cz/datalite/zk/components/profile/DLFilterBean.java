package cz.datalite.zk.components.profile;

/**
 * Bean used in filter must implement this interface to ensure
 * that will be properly serialized to profile. 
 */
public interface DLFilterBean<T> {
	
	String toJson();
	T fromJson(String json);

}
