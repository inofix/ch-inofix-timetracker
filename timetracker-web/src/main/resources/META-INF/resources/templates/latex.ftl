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
<#list taskRecords as taskRecord>
${taskRecord.userName} & \textbf{up:} ${taskRecord.description}  & ${taskRecord.fromDate?date}  & ${taskRecord.duration?number / 60 / 1000 / 60}  \\
</#list>
\hline
 & Periodentotal & & 116.25\\
 & Übertrag Vorperiode (sep. Abrg) & & 0.00\\
\hline
 & Total & & 116.25\\
\hline
\hline
\end{supertabular}
\begin{minipage}[t]{5cm}
{\scriptsize
\textbf{Mitarbeitende (MA):}
\begin{deflist}[aaaa]
 \item[cb] Christian Berndt
\end{deflist}
}
\end{minipage}
\begin{minipage}[t]{5cm}
{\scriptsize
\textbf{Kategorien:}
\begin{deflist}[aaa]
 \item[up] Upgrade
 \item[ne] Newsletter
 \item[se] Service
 \item[pm] Pm
 \item[in] Inofix-theme
 \item[te] Testing
 \item[we] Web
\end{deflist}
}
\end{minipage}