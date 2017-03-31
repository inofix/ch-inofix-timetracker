<%--
    configuration.jsp: configuration of the timetracker portlet.
    
    Created:    2017-03-09 14:20 by Stefan Lübbers
    Modified:   2017-03-31 19:22 by Christian Berndt
    Version:    1.0.2
--%>

<%@ include file="/init.jsp"%>

<%
    String[] columns = new String[0];
    String maxLength = "";
    String timeFormat = "";
    if (Validator.isNotNull(timetrackerConfiguration)) {
        columns = portletPreferences.getValues("columns", timetrackerConfiguration.columns());
        maxLength = portletPreferences.getValue("max-length", timetrackerConfiguration.maxLength());
        timeFormat = portletPreferences.getValue("time-format", timetrackerConfiguration.timeFormat());
    }
    
    TaskRecordSearch searchContainer = new TaskRecordSearch(liferayPortletRequest, portletURL);
    List<String> headerList = searchContainer.getHeaderNames();
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>"
    var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>"
    var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm"
    onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveConfiguration();" %>'>
    
    <liferay-ui:panel id="timetrackerColumnsPanel" title="columns"
        extended="true">

        <aui:input name="<%=Constants.CMD%>" type="hidden"
            value="<%=Constants.UPDATE%>" />

        <aui:input name="redirect" type="hidden"
            value="<%=configurationRenderURL%>" />

        <aui:input name="columns" type="hidden" />

        <%
            Set<String> availableColumns = SetUtil.fromList(headerList);
        
            List<KeyValuePair> leftList = new ArrayList<KeyValuePair>();
            for (String column : columns) {
                leftList.add(new KeyValuePair(column, LanguageUtil.get(request, column)));
            }

            List<KeyValuePair> rightList = new ArrayList<KeyValuePair>();
            Arrays.sort(columns);
            for (String column : availableColumns) {
                if (Arrays.binarySearch(columns, column) < 0) {
                    rightList.add(new KeyValuePair(column, LanguageUtil.get(request, column)));
                }
            }
            rightList = ListUtil.sort(rightList, new KeyValuePairComparator(false, true));
        %>

        <liferay-ui:input-move-boxes leftBoxName="currentColumns"
            leftList="<%=leftList%>"
            leftReorder="<%=Boolean.TRUE.toString()%>"
            leftTitle="current" rightBoxName="availableColumns"
            rightList="<%=rightList%>" rightTitle="available" />
            
    </liferay-ui:panel>

    <liferay-ui:panel id="timetrackerMiscellaneousPanel"
        title="miscellaneous" extended="true">
        
        <aui:fieldset>
            <aui:field-wrapper label="time-display"
                helpMessage="time-format-help" inlineField="false">
                <aui:input name="time-format" type="radio"
                    value="time-format"
                    checked="<%=Validator.equals(timeFormat, "time-format")%>"
                    label="duration" inlineField="true" />

                <aui:input name="<%="time-format"%>" type="radio"
                    value="from-until"
                    checked="<%=Validator.equals(timeFormat, "from-until")%>"
                    label="from-until" inlineField="true" />

            </aui:field-wrapper>
        </aui:fieldset>

        <aui:input name="max-length" value="<%=maxLength%>"
            helpMessage="max-length-help" />
            
    </liferay-ui:panel>

    <aui:button-row>
        <aui:button type="submit"></aui:button>
    </aui:button-row>
</aui:form>

<aui:script>
    function <portlet:namespace />saveConfiguration() {
        var Util = Liferay.Util;

        var form = AUI.$(document.<portlet:namespace />fm);

        form.fm('columns').val(Util.listSelect(form.fm('currentColumns')));

        submitForm(form);
    }
</aui:script>
