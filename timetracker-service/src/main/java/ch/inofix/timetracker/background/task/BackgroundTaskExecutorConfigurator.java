package ch.inofix.timetracker.background.task;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.util.HashMapDictionary;

/**
 *
 * @author Christian Berndt
 * @created 2017-04-04 23:55
 * @modified 2017-04-04 23:55
 * @version 1.0.0
 *
 */
@Component(immediate = true, service = BackgroundTaskExecutorConfigurator.class)
public class BackgroundTaskExecutorConfigurator {

    @Activate
    protected void activate(BundleContext bundleContext) {
        BackgroundTaskExecutor taskRecordExportBackgroundTaskExecutor = new TaskRecordExportBackgroundTaskExecutor();

        registerBackgroundTaskExecutor(bundleContext, taskRecordExportBackgroundTaskExecutor);

        BackgroundTaskExecutor taskRecordImportBackgroundTaskExecutor = new TaskRecordImportBackgroundTaskExecutor();

        registerBackgroundTaskExecutor(bundleContext, taskRecordImportBackgroundTaskExecutor);

    }

    @Deactivate
    protected void deactivate() {
        for (ServiceRegistration<BackgroundTaskExecutor> serviceRegistration : _serviceRegistrations) {

            serviceRegistration.unregister();
        }
    }

    protected void registerBackgroundTaskExecutor(BundleContext bundleContext,
            BackgroundTaskExecutor backgroundTaskExecutor) {

        Dictionary<String, Object> properties = new HashMapDictionary<>();

        Class<?> clazz = backgroundTaskExecutor.getClass();

        properties.put("background.task.executor.class.name", clazz.getName());

        ServiceRegistration<BackgroundTaskExecutor> serviceRegistration = bundleContext
                .registerService(BackgroundTaskExecutor.class, backgroundTaskExecutor, properties);

        _serviceRegistrations.add(serviceRegistration);
    }

    private final Set<ServiceRegistration<BackgroundTaskExecutor>> _serviceRegistrations = new HashSet<>();

}
