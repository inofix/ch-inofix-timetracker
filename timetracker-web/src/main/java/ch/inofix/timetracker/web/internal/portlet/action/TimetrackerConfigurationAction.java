package ch.inofix.timetracker.web.internal.portlet.action;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

import ch.inofix.timetracker.constants.PortletKeys;
import ch.inofix.timetracker.web.configuration.TimetrackerConfiguration;

/**
 * Configuration of Inofix' timetracker.
 *
 * @author Christian Berndt
 * @author Stefan Luebbers
 * @created 2017-03-09 14:20
 * @modified 2017-10-28 17:09
 * @version 1.0.5
 */

@Component(
    configurationPid = "ch.inofix.timetracker.web.configuration.TimetrackerConfiguration",
    configurationPolicy = ConfigurationPolicy.OPTIONAL, 
    immediate = true, 
    property = {"javax.portlet.name=" + PortletKeys.TIMETRACKER }, 
    service = ConfigurationAction.class
)

public class TimetrackerConfigurationAction extends DefaultConfigurationAction {

    @Override
    public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {

        String columns = ParamUtil.getString(actionRequest, "columns");
        String[] exportFileNames = actionRequest.getParameterValues("exportFileName"); 
        String[] exportNames = actionRequest.getParameterValues("exportName"); 
        String[] exportScripts = actionRequest.getParameterValues("exportScript");      

        setPreference(actionRequest, "columns", columns.split(","));
        setPreference(actionRequest, "exportFileName", exportFileNames);
        setPreference(actionRequest, "exportName", exportNames);
        setPreference(actionRequest, "exportScript", exportScripts);

        super.processAction(portletConfig, actionRequest, actionResponse);
    }

    @Override
    public void include(PortletConfig portletConfig, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws Exception {

        httpServletRequest.setAttribute(TimetrackerConfiguration.class.getName(), _timetrackerConfiguration);

        super.include(portletConfig, httpServletRequest, httpServletResponse);
    }

    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {

        _timetrackerConfiguration = ConfigurableUtil.createConfigurable(
                TimetrackerConfiguration.class, properties);
        
    }

    private volatile TimetrackerConfiguration _timetrackerConfiguration;
}
