package uk.ac.ox.oucs.search2.task;

import java.util.Date;

/**
 * @author Colin Hebert
 */
public interface Task {
    String getType();
    Date getCreationDate();
    String getProperty(String propertyName);
}
