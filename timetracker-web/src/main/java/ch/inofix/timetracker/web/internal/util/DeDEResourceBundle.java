package ch.inofix.timetracker.web.internal.util;

import java.util.Enumeration;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.language.UTF8Control;

/**
 * Append the timetrackers's Language_de_DE.properties to Liferay's core
 * Language.properties. This is required to access the social-activtiy keys
 * within Liferay's social activity framework.
 *
 * @author Christian Berndt
 * @created 2017-08-11 22:29
 * @modified 2017-08-11 22:29
 * @version 1.0.0
 */
@Component(property = { "language.id=de_DE" }, service = ResourceBundle.class)
public class DeDEResourceBundle extends ResourceBundle {

    @Override
    protected Object handleGetObject(String key) {
        return _resourceBundle.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return _resourceBundle.getKeys();
    }

    private final ResourceBundle _resourceBundle = ResourceBundle.getBundle("content.Language_de_DE", UTF8Control.INSTANCE);

}
