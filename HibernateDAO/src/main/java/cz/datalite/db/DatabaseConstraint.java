package cz.datalite.db;

/**
 * Wrapper containing data for DB constraint.
 */
public class DatabaseConstraint {

	private String constraintType;
	private String tableName;
	private String columnNames;

	public DatabaseConstraint() {
		super();
	}

	public DatabaseConstraint(String constraintType, String tableName, String columnNames) {
		super();
		this.constraintType = constraintType;
		this.tableName = tableName;
		this.columnNames = columnNames;
	}

	public String getConstraintType() {
		return constraintType;
	}

	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}

	public ConstraintType getConstraintTypeEnum() {
		return ConstraintType.valueOf(constraintType);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String columnNames) {
		this.columnNames = columnNames;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("DatabaseConstraint{");
		sb.append("constraintType='").append(constraintType).append('\'');
		sb.append(", tableName='").append(tableName).append('\'');
		sb.append(", columnNames='").append(columnNames).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
