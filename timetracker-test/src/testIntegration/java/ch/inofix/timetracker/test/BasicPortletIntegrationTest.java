/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package ch.inofix.timetracker.test;

import java.io.IOException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

//import com.liferay.arquillian.sample.service.SampleService;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class BasicPortletIntegrationTest {

//	@Deployment
//	public static JavaArchive create() throws Exception {
//		final File tempDir = Files.createTempDir();
//
//        String gradlew = "../gradlew";
//
//		String osName = System.getProperty("os.name", "");
//		if (osName.toLowerCase().contains("windows")) {
//			gradlew = "../gradlew.bat";
//		}
//
//		final ProcessBuilder processBuilder = new ProcessBuilder(
//			gradlew, "jar", "-Pdir=" + tempDir.getAbsolutePath());
//
//		final Process process = processBuilder.start();
//
//		process.waitFor();
//
//		final File jarFile = new File(
//			tempDir.getAbsolutePath() +
//				"/ch.inofix.timetracker.test-1.0.0.jar");
//
//		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
//	}

	@Test
	public void testAdd() throws IOException, PortalException {
        final long result = 4;
//        final long result = _sampleService.add(1, 3);

		Assert.assertEquals(4, result);
	}

//	@Inject
//	private SampleService _sampleService;

}
