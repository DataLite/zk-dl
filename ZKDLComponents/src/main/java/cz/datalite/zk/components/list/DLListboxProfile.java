package cz.datalite.zk.components.list;

public interface DLListboxProfile {

	Long getId();

	void setId(Long id);

	String getDlListboxId();

	void setDlListboxId(String dlListboxId);

	String getName();

	void setName(String name);

	boolean isPublicProfile();

	void setPublicProfile(boolean publicProfile);

	boolean isDefaultProfile();

	void setDefaultProfile(boolean defaultProfile);

	boolean isHidden();

	void setHidden(boolean hidden);

	boolean isEditable();

	void setEditable(boolean editable);

	String getUser();

	void setUser(String user);

	String getColumnModelJsonData();

	void setColumnModelJsonData(String columnModelJsonData);

	String getFilterModelJsonData();

	void setFilterModelJsonData(String filterModelJsonData);

	Integer getColumnsHashCode();

	void setColumnsHashCode(Integer columnsHashCode);


}