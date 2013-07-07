package cz.datalite.zk.components.list;

public class DLListboxProfile {

	private Long 	id;
	private String	dlListboxId;
	private String	name;

	private Boolean publicProfile;
	private Boolean hidden;
	private String  user;

	private String	columnModelJsonData;
	private String	filterModelJsonData;
	private Integer	columnsHashCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDlListboxId() {
		return dlListboxId;
	}

	public void setDlListboxId(String dlListboxId) {
		this.dlListboxId = dlListboxId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getPublicProfile() {
		return publicProfile;
	}

	public void setPublicProfile(Boolean publicProfile) {
		this.publicProfile = publicProfile;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getColumnModelJsonData() {
		return columnModelJsonData;
	}

	public void setColumnModelJsonData(String columnModelJsonData) {
		this.columnModelJsonData = columnModelJsonData;
	}

	public String getFilterModelJsonData() {
		return filterModelJsonData;
	}

	public void setFilterModelJsonData(String filterModelJsonData) {
		this.filterModelJsonData = filterModelJsonData;
	}

	public Integer getColumnsHashCode() {
		return columnsHashCode;
	}

	public void setColumnsHashCode(Integer columnsHashCode) {
		this.columnsHashCode = columnsHashCode;
	}

	@Override
	public String toString() {
		return "DLListboxProfile [id=" + id + ", dlListboxId=" + dlListboxId
				+ ", name=" + name + ", publicProfile=" + publicProfile
				+ ", hidden=" + hidden + ", user=" + user
				+ ", columnModelJsonData=" + columnModelJsonData
				+ ", filterModelJsonData=" + filterModelJsonData
				+ ", columnsHashCode=" + columnsHashCode + "]";
	}
}
