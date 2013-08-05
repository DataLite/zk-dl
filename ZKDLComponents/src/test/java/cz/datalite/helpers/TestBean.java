package cz.datalite.helpers;

import cz.datalite.zk.components.profile.DLFilterBean;

public class TestBean implements DLFilterBean<TestBean> {
	
	Integer id;
	
	public TestBean() {
		super();
	}

	public TestBean(Integer id) {
		super();
		this.id = id;
	}

	@Override
	public String toJson() {
		return id != null ? id.toString() : null;
	}

	@Override
	public TestBean fromJson(String json) {
		return json != null ? new TestBean(Integer.valueOf(json)) : null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TestBean other = (TestBean) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}