package cz.datalite.zk.components.list.service;

import java.util.List;

import cz.datalite.zk.components.list.DLListboxProfile;

public interface ProfileService {

	public List<DLListboxProfile> findAll();

	public List<DLListboxProfile> findAll(String dlListboxId);

	public DLListboxProfile findById(Long id);

	public DLListboxProfile makePersistent(DLListboxProfile dlListboxProfile);
	
	public void makeTransient(DLListboxProfile dlListboxProfile);

}
