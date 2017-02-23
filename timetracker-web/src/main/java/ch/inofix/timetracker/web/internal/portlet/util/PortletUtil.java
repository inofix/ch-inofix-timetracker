package ch.inofix.timetracker.web.internal.portlet.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Utility methods used by the TimetrackerPortlet. Based on the model of the
 * ActionUtil of Liferay proper.
 * 
 * @author Christian Berndt
 * @created 2016-03-21 18:43
 * @modified 2016-11-26 17:39
 * @version 1.0.1
 */
public class PortletUtil {

    /**
     * @param key
     * @return
     * @since 1.0.0
     */
    public static String translate(String key) {

        return translate(key, null);
    }

    /**
     * @param key
     * @param object
     * @return
     * @since 1.0.0
     */
    public static String translate(String key, Object object) {

        return translate(key, new Object[] { object });
    }

    /**
     * @param key
     * @param objects
     * @return
     * @since 1.0.0
     */
    public static String translate(String key, Object[] objects) {

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Language");
            return MessageFormat.format(bundle.getString(key), objects);
        } catch (MissingResourceException mre) {
            log.warn(mre.getMessage());
            return key;
        }
    }

    private static Log log = LogFactoryUtil.getLog(PortletUtil.class.getName());

}
