<%--
    configuration.jsp: configuration of the timetracker portlet.
    
    Created:    2017-03-09 14:20 by Stefan Lübbers
    Modified:   2017-03-09 14:20 by Stefan Lübbers
    Version:    1.0.0
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
    //String allColumns = SearchColumns.REFERENCE_SEARCH_COLUMNS;

%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>"
    var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>"
    var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm"
    onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveConfiguration();" %>'>

    <aui:input name="<%=Constants.CMD%>" type=""
        value="<%=Constants.UPDATE%>" />

    <aui:input name="redirect" type=""
        value="<%=configurationRenderURL%>" />
        
    <aui:input name="columns" type=""/>

<%--        <aui:fieldset collapsible="<%=true%>" label="show-columns">
            <%
                Set<String> availableColumns = SetUtil.fromArray(StringUtil.split(allColumns));
                // Left list
                List leftList = new ArrayList();
                for (String column : columns) {
                    leftList.add(new KeyValuePair(column, LanguageUtil.get(request, column)));
                }
                // Right list
                List rightList = new ArrayList();
                Arrays.sort(columns);
                for (String column : availableColumns) {
                    if (Arrays.binarySearch(columns, column) < 0) {
                        rightList.add(new KeyValuePair(column, LanguageUtil.get(request, column)));
                    }
                }
                rightList = ListUtil.sort(rightList, new KeyValuePairComparator(false, true));
            %>

            <liferay-ui:input-move-boxes
                leftBoxName="currentColumns"
                leftList="<%=leftList%>"
                leftReorder="<%=Boolean.TRUE.toString()%>"
                leftTitle="current"
                rightBoxName="availableColumns"
                rightList="<%=rightList%>" rightTitle="available" />
        </aui:fieldset>  --%>
    
    <liferay-ui:panel id="timetrackerMiscellaneousPanel" title="miscellaneous" extended="true">
            <aui:fieldset>
                <aui:field-wrapper label="time-format" helpMessage="time-format-help">
                    <aui:input name="time-format"
                        type="radio" value="time-format"
                        checked="<%=Validator.equals(timeFormat, "time-format")%>"
                        label="duration" inlineField="true"/>
        
                    <aui:input name="<%="time-format"%>"
                        type="radio" value="from-until"
                        checked="<%=Validator.equals(timeFormat, "from-until")%>"
                        label="from-until" inlineField="true" />
        
                </aui:field-wrapper>
            </aui:fieldset> 
            
            <aui:input name="max-length" value="35" helpMessage="max-length-help"/>
    </liferay-ui:panel> 

    <aui:button-row>
        <aui:button type="submit"></aui:button>
    </aui:button-row>
</aui:form>

<aui:script>
    function <portlet:namespace />saveConfiguration() {
//         var Util = Liferay.Util;

        var form = AUI.$(document.<portlet:namespace />fm);

//         form.fm('columns').val(Util.listSelect(form.fm('currentColumns')));

        submitForm(form);
    }
</aui:script>