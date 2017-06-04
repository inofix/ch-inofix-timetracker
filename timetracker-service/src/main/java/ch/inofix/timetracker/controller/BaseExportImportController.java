package ch.inofix.timetracker.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.liferay.exportimport.kernel.xstream.XStreamAlias;
import com.liferay.exportimport.kernel.xstream.XStreamConverter;
import com.liferay.exportimport.kernel.xstream.XStreamType;
import com.liferay.exportimport.xstream.ConverterAdapter;
import com.liferay.exportimport.xstream.XStreamStagedModelTypeHierarchyPermission;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.xstream.configurator.XStreamConfigurator;
import com.liferay.xstream.configurator.XStreamConfiguratorRegistryUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

/**
 *
 * @author Christian Berndt
 * @created 2017-06-04 21:37
 * @modified 2017-06-04 21:37
 *
 */
public abstract class BaseExportImportController {

    protected void initXStream() {

        _xStream = new XStream(null, new XppDriver(), new ClassLoaderReference(
                XStreamConfiguratorRegistryUtil.getConfiguratorsClassLoader(XStream.class.getClassLoader())));

        _xStream.omitField(HashMap.class, "cache_bitmask");

        Set<XStreamConfigurator> xStreamConfigurators = XStreamConfiguratorRegistryUtil.getXStreamConfigurators();

        if (SetUtil.isEmpty(xStreamConfigurators)) {
            return;
        }

        List<String> allowedTypeNames = new ArrayList<>();

        for (XStreamConfigurator xStreamConfigurator : xStreamConfigurators) {
            List<XStreamAlias> xStreamAliases = xStreamConfigurator.getXStreamAliases();

            if (ListUtil.isNotEmpty(xStreamAliases)) {
                for (XStreamAlias xStreamAlias : xStreamAliases) {
                    _xStream.alias(xStreamAlias.getName(), xStreamAlias.getClazz());
                }
            }

            List<XStreamConverter> xStreamConverters = xStreamConfigurator.getXStreamConverters();

            if (ListUtil.isNotEmpty(xStreamConverters)) {
                for (XStreamConverter xStreamConverter : xStreamConverters) {
                    _xStream.registerConverter(new ConverterAdapter(xStreamConverter), XStream.PRIORITY_VERY_HIGH);
                }
            }

            List<XStreamType> xStreamTypes = xStreamConfigurator.getAllowedXStreamTypes();

            if (ListUtil.isNotEmpty(xStreamTypes)) {
                for (XStreamType xStreamType : xStreamTypes) {
                    allowedTypeNames.add(xStreamType.getTypeExpression());
                }
            }
        }

        // For default permissions, first wipe than add default

        _xStream.addPermission(NoTypePermission.NONE);

        // Add permissions

        _xStream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        _xStream.addPermission(XStreamStagedModelTypeHierarchyPermission.STAGED_MODELS);

        _xStream.allowTypes(_XSTREAM_DEFAULT_ALLOWED_TYPES);

        _xStream.allowTypeHierarchy(List.class);
        _xStream.allowTypeHierarchy(Map.class);
        _xStream.allowTypeHierarchy(Timestamp.class);
        _xStream.allowTypeHierarchy(Set.class);

        _xStream.allowTypes(allowedTypeNames.toArray(new String[0]));

        _xStream.allowTypesByWildcard(new String[] { "com.thoughtworks.xstream.mapper.DynamicProxyMapper*" });
    }

    protected static final Class<?>[] _XSTREAM_DEFAULT_ALLOWED_TYPES = { boolean[].class, byte[].class, Date.class,
            Date[].class, double[].class, float[].class, int[].class, Locale.class, long[].class, Number.class,
            Number[].class, short[].class, String.class, String[].class };

    protected transient XStream _xStream;
}
