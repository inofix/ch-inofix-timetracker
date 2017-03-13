package ch.inofix.timetracker.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * 
 * @author Stefan Luebbers
 * @created 2017-03-09 13:43
 * @modified 2017-03-12 00:44
 * @version 1.0.0
 *
 */
@Meta.OCD(id = "ch.inofix.timetracker.web.configuration.TimetrackerConfiguration", localization = "content/Language", name = "timetracker.configuration.name")

public interface TimetrackerConfiguration {
	
    @Meta.AD(deflt = "create-date|description|duration|end-date|modified-date|start-date|status|task-record-id|ticket-url|user-name|work-package", required = false)
    public String[] columns();
    
    @Meta.AD(deflt = "35", required = false)
    public String maxLength();
    
    @Meta.AD(deflt = "from-until", required = false)
    public String timeFormat();
    
    
}
