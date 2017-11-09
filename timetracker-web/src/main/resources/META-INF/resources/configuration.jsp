<%--
    configuration.jsp: configuration of the timetracker portlet.
    
    Created:    2017-03-09 14:20 by Stefan Lübbers
    Modified:   2017-11-09 18:30 by Christian Berndt
    Version:    1.1.4
--%>

<%@ include file="/init.jsp"%>

<%
    PortletURL portletURL = renderResponse.createRenderURL();

    TaskRecordSearch searchContainer = new TaskRecordSearch(liferayPortletRequest, portletURL);
    List<String> headerList = searchContainer.getHeaderNames();
    
    String namespace = liferayPortletResponse.getNamespace();
%>

<style>
<!--
    .portlet-timetracker .panel-title {
        position: relative;
    }
-->
</style>

<liferay-portlet:actionURL portletConfiguration="<%=true%>"
    var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%=true%>"
    var="configurationRenderURL" />

<aui:form action="<%=configurationActionURL%>" method="post" name="fm"
    onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveConfiguration();" %>'>

    <div class="portlet-configuration-body-content">

        <div class="container-fluid-1280">

            <liferay-ui:panel collapsible="<%=true%>"
                id="timetrackerColumnsPanel"
                markupView="<%=markupView%>" persistState="<%=true%>"
                title="columns">

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

                <liferay-ui:input-move-boxes
                    leftBoxName="currentColumns"
                    leftList="<%=leftList%>"
                    leftReorder="<%=Boolean.TRUE.toString()%>"
                    leftTitle="current" rightBoxName="availableColumns"
                    rightList="<%=rightList%>" rightTitle="available" />

            </liferay-ui:panel>

            <liferay-ui:panel collapsible="<%=true%>"
                id="timetrackerExportPanel" markupView="<%=markupView%>"
                persistState="<%=true%>" title="export">

                <aui:fieldset id="<%=namespace + "export"%>"
                    cssClass="export">

                    <%
                        for (int i = 0; i < exportNames.length; i++) {

                            String exportName = null;
                            if (exportNames.length > i)
                                exportName = exportNames[i];
                            
                            String exportFileName = null;
                            if (exportFileNames.length > i)
                                exportFileName = exportFileNames[i];
                            
                            String exportScript = null;
                            if (exportScripts.length > i)
                                exportScript = exportScripts[i];
                    %>
                    <div class="lfr-form-row">

                        <div class="row-fields">

                            <aui:input helpMessage="export-name-help"
                                name="exportName"
                                required="<%=true%>"
                                value="<%=exportName%>" />

                            <aui:input
                                helpMessage="export-file-name-help"
                                name="exportFileName"
                                value="<%=exportFileName%>" />

                            <aui:input helpMessage="export-script-help"
                                name="exportScript"
                                type="textarea"
                                value="<%=exportScript%>" />

                        </div>
                    </div>

                    <%
                        }
                    %>
                </aui:fieldset>

            </liferay-ui:panel>

            <liferay-ui:panel collapsible="<%=true%>"
                id="timetrackerMiscellaneousPanel"
                markupView="<%=markupView%>" persistState="<%=true%>"
                title="miscellaneous">

                <aui:fieldset cssClass="col-md-6">

                    <aui:input
                        checked="<%="lexicon".equals(markupView)%>"
                        helpMessage="markup-view-help"
                        label="use-lexicon" name="preferences--markupView--"
                        type="checkbox" value="lexicon" />

                    <aui:input helpMessage="show-search-speed-help"
                        label="show-search-speed"
                        name="preferences--showSearchSpeed--" type="checkbox"
                        value="<%=showSearchSpeed%>" />

                </aui:fieldset>


                <aui:fieldset cssClass="col-md-6">

                    <aui:input name="preferences--maxLength--"
                        value="<%=String.valueOf(maxLength)%>"
                        helpMessage="max-length-help" />

                    <aui:field-wrapper label="time-format"
                        helpMessage="time-format-help"
                        inlineField="false">

                        <br />

                        <aui:input name="preferences--timeFormat--" type="radio"
                            value="time-format"
                            checked="<%=Validator.equals(timeFormat, "time-format")%>"
                            label="duration" inlineField="true" />

                        <aui:input name="preferences--timeFormat--"
                            type="radio" value="from-until"
                            checked="<%=Validator.equals(timeFormat, "from-until")%>"
                            label="from-until" inlineField="true" />

                    </aui:field-wrapper>
                </aui:fieldset>

            </liferay-ui:panel>

        </div>

        <aui:button-row>
            <aui:button type="submit" />
        </aui:button-row>
    </div>
</aui:form>

<aui:script>
    function <portlet:namespace />saveConfiguration() {
        var Util = Liferay.Util;

        var form = AUI.$(document.<portlet:namespace />fm);

        form.fm('columns').val(Util.listSelect(form.fm('currentColumns')));

        submitForm(form);
    }
</aui:script>

<%-- Configure auto-fields --%>
<aui:script use="liferay-auto-fields">

    var exportAutoFields = new Liferay.AutoFields({
        contentBox : 'fieldset#<portlet:namespace />export',
        namespace : '<portlet:namespace />',
        on : {
            'clone' : function(event) {
                restoreOriginalNames(event);
            }
        }
    }).render();
    
</aui:script>

