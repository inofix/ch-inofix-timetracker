<%--
    navigation.jsp: the import navigation.
    
    Created:    2017-06-01 21:08 by Christian Berndt
    Modified:   2017-06-01 21:08 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<aui:nav-bar markupView="lexicon">
    <aui:nav cssClass="navbar-nav">
        <aui:nav-item
            label="processes"
            selected="<%= true %>"
        />
    </aui:nav>
</aui:nav-bar>