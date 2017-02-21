/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package ch.inofix.timetracker.service.impl;

import aQute.bnd.annotation.ProviderType;

import ch.inofix.timetracker.service.base.TaskRecordLocalServiceBaseImpl;

/**
 * The implementation of the task record local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link ch.inofix.timetracker.service.TaskRecordLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Christian Berndt
 * @see TaskRecordLocalServiceBaseImpl
 * @see ch.inofix.timetracker.service.TaskRecordLocalServiceUtil
 */
@ProviderType
public class TaskRecordLocalServiceImpl extends TaskRecordLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link ch.inofix.timetracker.service.TaskRecordLocalServiceUtil} to access the task record local service.
	 */
}