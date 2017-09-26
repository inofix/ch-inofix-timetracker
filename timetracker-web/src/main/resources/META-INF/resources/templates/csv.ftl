<#--
    csv.ftl: An example template which formats task-records 
    as a csv-report.
    
    Created: 2017-09-01 13:12 by Christian Berndt   
    Modified: 2017-09-01 13:12 by Christian Berndt
    Version: 1.0.0
-->

<#list taskRecords?sort_by("fromDate")?reverse as taskRecord>
    <#assign duration = taskRecord.duration / 1000 / 60 / 60 />
    <#assign fromDate = taskRecord.fromDate?date />
"${taskRecord.workPackage}", "${taskRecord.description}", "${fromDate?string["yyyy-MM-dd"]}", "${duration?string(",##0.00")}", "${taskRecord.userName}"
</#list>