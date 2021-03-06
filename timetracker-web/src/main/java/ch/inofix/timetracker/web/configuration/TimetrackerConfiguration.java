package ch.inofix.timetracker.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 *
 * @author Christian Berndt
 * @author Stefan Luebbers
 * @created 2017-03-09 13:43
 * @modified 2017-10-30 21:15
 * @version 1.0.7
 *
 */
@Meta.OCD(
    id = "ch.inofix.timetracker.web.configuration.TimetrackerConfiguration", 
    localization = "content/Language", name = "timetracker.configuration.name"
)
public interface TimetrackerConfiguration {

    @Meta.AD(deflt = "task-record-id|work-package|from-date|duration|description|user-name|modified-date|status", required = false)
    public String[] columns();

    @Meta.AD(deflt = "out.csv", required = false)
    public String[] exportFileNames();

    @Meta.AD(deflt = "CSV", required = false)
    public String[] exportNames();

    @Meta.AD(deflt = "<#list taskRecords as taskRecord><h3>${taskRecord.workPackage}</h3>\n</#list>", required = false)
    public String[] exportScripts();

    @Meta.AD(deflt = "lexicon", required = false)
    public String markupView();

    @Meta.AD(deflt = "35", required = false)
    public String maxLength();

    @Meta.AD(deflt = "false", required = false)
    public boolean showSearchSpeeed();

    @Meta.AD(deflt = "from-until", required = false)
    public String timeFormat();

}
