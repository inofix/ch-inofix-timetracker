<#--
    latex.ftl: An example template which formats task-records 
    as a latex-report.
    
    Created: 2017-06-16 16:13 by Christian Berndt   
    Modified: 2017-06-16 16:13 by Christian Berndt
    Version: 1.0.0
-->

\begin{supertabular}{p{0.6cm}|p{12cm}|p{1.8cm}|R|}
MA & Beschreibung & Zeitraum & Menge \\
\hline

<#assign sumPeriod = 0 />

<#list taskRecords as taskRecord>
    <#assign duration = taskRecord.duration / 1000 / 60 / 60 />
    <#assign fromDate = taskRecord.fromDate?date />
    <#assign initials = "" />
    <#list taskRecord.userName?split(" ") as token>
        <#assign initials = initials + token?substring(0,1) />
    </#list>
    <#assign sumPeriod = sumPeriod + duration />
${initials?lower_case} & \textbf{op:} ${taskRecord.description} & ${fromDate?string["yyyy-MM-dd"]} & ${duration?string(",##0.00")} \\
</#list>

\hline
 & Periodentotal & & ${sumPeriod?string(",##0.00")}\\
 & Übertrag Vorperiode (sep. Abrg) & & 0.00\\
\hline
 & Total & & 0.00\\
\hline
\hline
\end{supertabular}
\begin{minipage}[t]{5cm}
{\scriptsize
\textbf{Mitarbeitende (MA):}
\begin{deflist}[aaaa]
 \item[ml] Michael Lustenberger
 \item[cb] Christian Berndt
\end{deflist}
}
\end{minipage}
\begin{minipage}[t]{5cm}
{\scriptsize
\textbf{Kategorien:}
\begin{deflist}[aaa]
 \item[su] Support
 \item[op] Operations
\end{deflist}
}
\end{minipage}
