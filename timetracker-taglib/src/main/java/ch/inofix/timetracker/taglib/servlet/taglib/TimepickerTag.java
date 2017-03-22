package ch.inofix.timetracker.taglib.servlet.taglib;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class TimepickerTag {

    // Configurable parameter
    private int deltaMinutes = 5;
    private int firstHour = 6;
    private int hour = -1;
    private int minute = -1;
    private boolean nullable = true;
    private String prefix = "";

    // private boolean _isTrimNewLines = true;
    private static final String _PAGE = "/taglib/util/time_picker/page.jsp";

    // TODO
//    @Override
//    public int doEndTag() throws JspException {
//
//        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
//
//        setAttributes(request);
//
//        try {
//            include(_PAGE);
//        } catch (Exception e) {
//            _log.error(e);
//        }
//
//        return EVAL_PAGE;
//
//    };

    /**
     * Include and execute the jsp-page.
     *
     * @param page
     *            the path to the jsp page.
     * @since 1.0
     * @throws Exception
     */
    // TODO
//    // From com.liferay.taglib.util.IncludeTag
//    protected void include(String page) throws Exception {
//
//        ServletContext servletContext = pageContext.getServletContext();
//
//        RequestDispatcher requestDispatcher = DirectRequestDispatcherFactoryUtil.getRequestDispatcher(servletContext,
//                page);
//
//        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
//
//        HttpServletResponse response = new PipingServletResponse(pageContext);
//
//        requestDispatcher.include(request, response);
//
//        request.removeAttribute(WebKeys.SERVLET_CONTEXT_INCLUDE_FILTER_STRICT);
//    }

    /**
     * Store the tag configuration parameters as request attributes.
     *
     * @param request
     * @since 1.0
     */
    protected void setAttributes(HttpServletRequest request) {

        request.setAttribute("inofix-util:time-picker:deltaMinutes", deltaMinutes);
        request.setAttribute("inofix-util:time-picker:firstHour", firstHour);
        request.setAttribute("inofix-util:time-picker:hour", hour);
        request.setAttribute("inofix-util:time-picker:minute", minute);
        request.setAttribute("inofix-util:time-picker:nullable", nullable);
        request.setAttribute("inofix-util:time-picker:prefix", prefix);

    }

    // Getters and setters for the configurable
    // parameters.
    public int getDeltaMinutes() {

        return deltaMinutes;
    }

    public void setDeltaMinutes(int deltaMinutes) {

        this.deltaMinutes = deltaMinutes;
    }

    public int getFirstHour() {

        return firstHour;
    }

    public void setFirstHour(int firstHour) {

        this.firstHour = firstHour;
    }

    public int getHour() {

        return hour;
    }

    public void setHour(int hour) {

        this.hour = hour;
    }

    public int getMinute() {

        return minute;
    }

    public void setMinute(int minute) {

        this.minute = minute;
    }

    public boolean isNullable() {

        return nullable;
    }

    public void setNullable(boolean nullable) {

        this.nullable = nullable;
    }

    public String getPrefix() {

        return prefix;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

    private static final Log _log = LogFactoryUtil.getLog(TimepickerTag.class.getName());

}
