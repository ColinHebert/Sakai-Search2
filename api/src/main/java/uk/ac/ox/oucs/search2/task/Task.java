package uk.ac.ox.oucs.search2.task;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Colin Hebert
 */
public interface Task extends Serializable {
    String getType();

    Date getCreationDate();

    String getProperty(String propertyName);
}
