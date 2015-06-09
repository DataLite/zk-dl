package cz.datalite.db;

public enum ConstraintType {
	/**
	 * C - Check on a table - Column
	 */
	C,
	/**
	 * O - Read Only on a view - Object
	 */
	O,
	/**
	 * P - Primary Key - Object
	 */
	P,
	/**
	 * R - Referential AKA Foreign Key - Column
	 */
	R,
	/**
	 * U - Unique Key - Column
	 */
	U,
	/**
	 * V - Check Option on a view - Object
	 */
	V
}
