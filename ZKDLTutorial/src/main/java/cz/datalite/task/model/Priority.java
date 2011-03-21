/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.task.model;

/**
 * Priority of a task.
 */
public enum Priority {
    HIGH("High"),
    NORMAL("Normal"),
    LOW("Low");

    Priority(String name) {
        this.name = name;
    }

    /** Name of this priority. **/
    private String name;

    /** Returns the name of the priorty. **/
    public String getName() {
        return name;
    }
}
