package ch.inofix.timetracker.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 *
 * @author Christian Berndt
 * @author Stefan Luebbers
 * @created 2017-03-09 13:43
 * @modified 2017-06-10 00:00
 * @version 1.0.2
 *
 */
@Meta.OCD(id = "ch.inofix.timetracker.web.configuration.TimetrackerConfiguration", localization = "content/Language", name = "timetracker.configuration.name")

public interface TimetrackerConfiguration {

    @Meta.AD(deflt = "task-record-id|work-package|from-date|duration|description|user-name|modified-date|status", required = false)
    public String[] columns();

    @Meta.AD(deflt = "35", required = false)
    public String maxLength();

    @Meta.AD(deflt = "from-until", required = false)
    public String timeFormat();


}
