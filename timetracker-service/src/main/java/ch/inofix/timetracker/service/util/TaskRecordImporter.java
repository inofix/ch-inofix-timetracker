package ch.inofix.timetracker.service.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
// TODO
//import org.apache.commons.lang.time.StopWatch;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import ch.inofix.timetracker.model.impl.TaskRecordImpl;

/**
 *
 * @author Christian Berndt
 * @created 2017-04-17 15:21
 * @modified 2017-04-17 15:21
 * @version 1.0.0
 */
public class TaskRecordImporter {

    public void importReferences(long userId, long groupId, boolean privateLayout, Map<String, String[]> parameterMap,
            File file, ServiceContext serviceContext) throws PortalException {

        User user = UserLocalServiceUtil.getUser(userId);

        // Import into the user's group
        Group group = user.getGroup();
        if (group != null) {
            groupId = group.getGroupId();
        }

        long bibliographyId = GetterUtil.getLong(ArrayUtil.getValue(parameterMap.get("bibliographyId"), 0));
        String description = GetterUtil.getString(ArrayUtil.getValue(parameterMap.get("description"), 0), null);
        String fileName = GetterUtil.getString(ArrayUtil.getValue(parameterMap.get("fileName"), 0), null);
        String title = GetterUtil.getString(ArrayUtil.getValue(parameterMap.get("title"), 0),
                LanguageUtil.get(serviceContext.getLocale(), "new-bibliography"));
        boolean updateExisting = GetterUtil.getBoolean(ArrayUtil.getValue(parameterMap.get("updateExisting"), 0));
        String urlTitle = GetterUtil.getString(ArrayUtil.getValue(parameterMap.get("urlTitle"), 0),
                LanguageUtil.get(serviceContext.getLocale(), "new-bibliography-url-title"));


        try {

            int numProcessed = 0;
            int numImported = 0;
            int numIgnored = 0;
            int numUpdated = 0;

            // TODO
//            StopWatch stopWatch = new StopWatch();

//            stopWatch.start();

            InputStream inputStream = new FileInputStream(file);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            // TODO: retrieve preamble from file
            String preamble = null;


            _log.info("Start import");

            com.liferay.portal.kernel.xml.Document document =
                    SAXReaderUtil.read(file);

                List<Node> nodes =
                    document.selectNodes("/taskRecords/" +
                        TaskRecordImpl.class.getName());

//                XStream xstream = new XStream();

//            Collection<BibTeXEntry> bibTeXEntries = database.getEntries().values();

//            _log.info("bibTeXEntries.size() = " + bibTeXEntries.size());

//            if (bibTeXEntries.size() == 0) {
//
//                throw new NoReferencesException();
//
//            }

            for (Node node : nodes) {

//                if (value != null) {

                        if (updateExisting) {

//                                    reference = ReferenceServiceUtil.updateReference(referenceId, userId, bibTeX,
//                                            serviceContext);

                            numUpdated++;

                        } else {

                            numIgnored++;

                        }
//                            } else {

                        // reference exists, but belongs to another user

                        _log.info("adding reference");

                        numImported++;
                    }

//                } else {

                    // no bibshare-id

                    _log.info("adding reference");

                    numImported++;

//                    reference = ReferenceServiceUtil.addReference(userId, bibTeX, bibliographyIds, serviceContext);
//                }

                if (numProcessed % 100 == 0 && numProcessed > 0) {

                    float completed = ((Integer) numProcessed).floatValue()
                            / ((Integer) nodes.size()).floatValue() * 100;

                    // TODO
//                    _log.info("Processed " + numProcessed + " of " + nodes.size() + " taskRecords in "
//                            + stopWatch.getTime() + " ms (" + completed + "%).");
                }

                numProcessed++;

//            }

                // TODO
//            _log.info("Import took " + stopWatch.getTime() + " ms");
            _log.info("Processed " + numProcessed + " references.");
            _log.info("Imported " + numImported + " references.");
            _log.info("Ignored " + numIgnored + " references.");
            _log.info("Updated " + numUpdated + " references.");

        } catch (IOException ioe) {
            _log.error(ioe);
        } catch (Exception e) {
            _log.error(e);
        }
    }


    private static Log _log = LogFactoryUtil.getLog(TaskRecordImporter.class.getName());

}
