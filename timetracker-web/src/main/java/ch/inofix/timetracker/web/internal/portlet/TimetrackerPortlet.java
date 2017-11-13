package ch.inofix.timetracker.web.internal.portlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import ch.inofix.timetracker.constants.PortletKeys;

/**
 * View Controller of Inofix' timetracker.
 *
 * @author Christian Berndt
 * @author Stefan Luebbers
 * @created 2013-10-07 10:47
 * @modified 2017-11-13 18:29
 * @version 1.9.4
 */
@Component(
    configurationPid = "ch.inofix.timetracker.web.configuration.TimetrackerConfiguration",
    immediate = true, 
    property = { 
        "com.liferay.portlet.css-class-wrapper=portlet-timetracker",
        "com.liferay.portlet.display-category=category.inofix",
        "com.liferay.portlet.footer-portlet-javascript=/js/main.js",
        "com.liferay.portlet.header-portlet-css=/css/main.css", 
        "com.liferay.portlet.instanceable=false",
        "com.liferay.portlet.scopeable=true",
        "javax.portlet.display-name=Timetracker", 
        "javax.portlet.init-param.template-path=/",
        "javax.portlet.init-param.view-template=/view.jsp",
        "javax.portlet.name=" + PortletKeys.TIMETRACKER,
        "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user" 
    }, 
    service = Portlet.class
)
public class TimetrackerPortlet extends MVCPortlet {
}
