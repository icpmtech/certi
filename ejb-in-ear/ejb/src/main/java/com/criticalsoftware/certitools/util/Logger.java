/*
 * $Id: Logger.java,v 1.3 2010/06/30 17:28:35 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/06/30 17:28:35 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

/**
 * The <code>Logger</code> class wraps the <code>Log</code> class. Its expected that <code>Logger</code> be the default
 * class for all the application log. Its fundamental value, its to provide a fixed set of logging methods for the
 * developer to use at will.
 * <p/>
 * Its expected to achieve two things with this class: first, releave the developer of tedious work of logging,
 * executing methods, urls accessed or values being passed as parameters, since this class already has methods that do
 * this specific logging, and second, provide an uniform logging to the log files. It facilitates the reading  of the
 * log by the developer and provides a way to extract metrics in the future if required.
 *
 * @author :    Roberto Cortez
 * @version :   $Revision: 1.3 $
 * @since :     WICCore 2.0
 */

public final class Logger {
    private final Log log;

    /**
     * Wraps the Class c to the <code>Log</code>.
     *
     * @param c a Class to log.
     * @since WICCore 2.0
     */
    private Logger(Class c) {
        log = LogFactory.getLog(c);
    }

    /**
     * Returns an instance of {@link Logger} for the requested Class.
     *
     * @param c the Class to be logged.
     * @return an instance of the {@link Logger} wrapped around a <code>Log</code>.
     *
     * @since WICCore 2.0
     */
    public static Logger getInstance(Class c) {
        return new Logger(c);
    }

    /**
     * Wrapped method of {@link Log#debug(Object)} implementation.
     *
     * @param message a <code>String</code> to log.
     * @since WICCore 2.0
     */
    public void debug(String message) {
        if (isDebugEnabled()) {
            log.debug(message);
        }
    }

    /**
     * Wrapped method of {@link Log#isDebugEnabled()} implementation.
     *
     * @return a boolean indicating if the threshold debug is enabled. If <tt>true</tt> is enabled, <tt>false</tt>
     *         otherwise.
     *
     * @since WICCore 2.0
     */
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * Wrapped method of {@link Log#debug(Object)} implementation.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @param uri    a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *               action.
     * @since WICCore 2.0
     */
    public void debug(String login, Method method, String uri) {
        if (isDebugEnabled()) {
            log.debug(buildLogString(login, method, uri, null));
        }
    }

    /**
     * Builds a <code>String</code> to be added as a line in the log with the paramenters login, uri and an
     * additionalInfo added by the developer.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @return a <code>String</code> with the login, uri and additionalInfo formatted for the log.
     *
     * @since WICCore 2.0
     */
    private String buildLogString(String login, Method method, String uri, String additionalInfo) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(login);
        stringBuffer.append(" executing ");
        stringBuffer.append(findMethodName(method));
        stringBuffer.append("][uri: ");
        stringBuffer.append(uri);
        stringBuffer.append("]");
        if (additionalInfo != null) {
            stringBuffer.append("[");
            stringBuffer.append(additionalInfo);
            stringBuffer.append("]");
        }
        return stringBuffer.toString();
    }

    /**
     * Search and return a <code>String</code> with the external Class method name that is executing a method from
     * {@link Logger}.
     *
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @return a <code>String</code> with the Class method executing a {@link Logger} method. If for some reason its
     *         imposible to determine the method name or an <code>Exception</code> is thrown during the search of the
     *         method name, the <code>String</code> "Undetermined" is returned.
     *
     * @since WICCore 2.0
     */
    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    private String findMethodName(Method method) {
        if (method != null) {
            return method.getName();
        }
        try {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();

            return elements[3].getMethodName();
        } catch (Exception e) {
            return "Undetermined";
        }
    }

    /**
     * Wrapped method of {@link Log#debug(Object)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @since WICCore 2.0
     */
    public void debug(String login, Method method, String uri, String additionalInfo) {
        if (isDebugEnabled()) {
            log.debug(buildLogString(login, method, uri, additionalInfo));
        }
    }

    /**
     * Wrapped method of {@link Log#debug(Object,Throwable)} implementation.
     *
     * @param login     a <code>String</code> with the user login executing actions that require log.
     * @param method    the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by
     *                  the stacktrace.
     * @param uri       a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                  action.
     * @param throwable an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void debug(String login, Method method, String uri, Throwable throwable) {
        if (isDebugEnabled()) {
            log.debug(buildLogString(login, method, uri, null), throwable);
        }
    }

    /**
     * Wrapped method of {@link Log#debug(Object,Throwable)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @param throwable      an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void debug(String login, Method method, String uri, String additionalInfo, Throwable throwable) {
        if (isDebugEnabled()) {
            log.debug(buildLogString(login, method, uri, additionalInfo), throwable);
        }
    }

    /**
     * Wrapped method of {@link Log#debug(Object)} implementation.
     *
     * @param request an <code>HttpServletRequest</code> to log.
     * @since WICCore 2.0
     */
    public void debugAttributes(HttpServletRequest request) {
        if (isDebugEnabled()) {
            Collection requestLog = buildDebugLogRequestAttributes(request);
            for (Object aRequestLog : requestLog) {
                String s = (String) aRequestLog;
                log.debug(s);
            }
        }
    }

    /**
     * Builds a <code>Collection</code> to be added as several lines with all the attributes names and values in the
     * <code>HttpServletRequest</code> associated with every user action for the log. The user login is also associated
     * to each attribute.
     *
     * @param request an <code>HttpServletRequest</code> associated with the user action.
     * @return a <code>Collection</code> containing elements of type <code>String</code> with all the attributes names
     *         and values formatted for the log. Each element corresponds to an attribute name and its value.
     *
     * @since WICCore 2.0
     */
    @SuppressWarnings({"unchecked"})
    private Collection buildDebugLogRequestAttributes(HttpServletRequest request) {
        ArrayList requestStringLog = new ArrayList();
        Enumeration enumeration = request.getAttributeNames();

        StringBuffer stringBuffer;
        String name = getUserName(request);

        if (!enumeration.hasMoreElements()) {
            stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            stringBuffer.append(name);
            stringBuffer.append(" REQUEST EMPTY]");
            requestStringLog.add(stringBuffer.toString());
        } else {
            requestStringLog.add(buildSeparator(name));
            stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            stringBuffer.append(name);
            stringBuffer.append(" LOGGING REQUEST ATTRIBUTES ********************]");
            requestStringLog.add(stringBuffer.toString());
            requestStringLog.add(buildSeparator(name));
        }

        while (enumeration.hasMoreElements()) {
            String attributeName = (String) enumeration.nextElement();
            stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            stringBuffer.append(name);
            stringBuffer.append(" request attribute: ");
            stringBuffer.append(attributeName);
            stringBuffer.append(" = ");
            stringBuffer.append(request.getAttribute(attributeName));
            stringBuffer.append(" ]");
            requestStringLog.add(stringBuffer.toString());
            if (!enumeration.hasMoreElements()) {
                requestStringLog.add(buildSeparator(name));
                stringBuffer = new StringBuffer();
                stringBuffer.append("[");
                stringBuffer.append(name);
                stringBuffer.append(" END LOGGING REQUEST ATTRIBUTES ****************]");
                requestStringLog.add(stringBuffer.toString());
                requestStringLog.add(buildSeparator(name));
            }
        }

        return requestStringLog;
    }

    /**
     * Builds a <code>String</code> to be added as a line separator, for the log with the login of the user.
     *
     * @param login a <code>String</code> with the user login executing actions that require log.
     * @return a <code>String</code> with a line separator formatted for the log.
     *
     * @since WICCore 2.0
     */
    private String buildSeparator(String login) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(login);
        stringBuffer.append(" ***********************************************]");
        return stringBuffer.toString();
    }

    /**
     * Wrapped method of {@link Log#debug(Object)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @since WICCore 2.0
     */
    public void debugMethodExecution(String login, Method method, String additionalInfo) {
        if (isDebugEnabled()) {
            log.debug(buildDebugLogMethodExecution(login, method) + "[" + additionalInfo + "]");
        }
    }

    /**
     * Builds a <code>String</code> to be added as a line indicating the executing method name is executing, for the log
     * with the login of the user.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @return a <code>String</code> with the method executing formatted the log.
     *
     * @since WICCore 2.0
     */
    private String buildDebugLogMethodExecution(String login, Method method) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(login);
        stringBuffer.append(" DEBUGGING ");
        stringBuffer.append(findMethodName(method));
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    /**
     * Wrapped method of {@link Log#debug(Object)} implementation.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @since WICCore 2.0
     */
    public void debugMethodExecutionEnd(String login, Method method) {
        if (isDebugEnabled()) {
            log.debug(buildDebugLogMethodExecutionEnd(login, method));
        }
    }

    /**
     * Builds a <code>String</code> to be added as a line indicating the executing method name as ended, for the log
     * with the login of the user.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @return a <code>String</code> with the method ending formatted for the log.
     *
     * @since WICCore 2.0
     */
    private String buildDebugLogMethodExecutionEnd(String login, Method method) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(login);
        stringBuffer.append(" ENDING ");
        stringBuffer.append(findMethodName(method));
        stringBuffer.append(" EXECUTION ");
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    /**
     * Wrapped method of {@link Log#debug(Object)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @since WICCore 2.0
     */
    public void debugMethodExecutionEnd(String login, Method method, String additionalInfo) {
        if (isDebugEnabled()) {
            log.debug(buildDebugLogMethodExecutionEnd(login, method) + "[" + additionalInfo + "]");
        }
    }

    /**
     * Wrapped method of {@link Log#debug(Object)} implementation.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @since WICCore 2.0
     */
    public void debugMethodExecutionStart(String login, Method method) {
        if (isDebugEnabled()) {
            log.debug(buildDebugLogMethodExecutionStart(login, method));
        }
    }

    /**
     * Builds a <code>String</code> to be added as a line indicating the executing method name as started, for the log
     * with the login of the user.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @return a <code>String</code> with the method ending formatted for the log.
     *
     * @since WICCore 2.0
     */
    private String buildDebugLogMethodExecutionStart(String login, Method method) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(login);
        stringBuffer.append(" STARTING ");
        stringBuffer.append(findMethodName(method));
        stringBuffer.append(" EXECUTION ");
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    /**
     * Wrapped method of {@link Log#debug(Object)} implementation.
     *
     * @param request an <code>HttpServletRequest</code> to log.
     * @since WICCore 2.0
     */
    public void debugParameters(HttpServletRequest request) {
        if (isDebugEnabled()) {
            Collection requestLog = buildDebugLogRequestParameters(request);
            for (Object aRequestLog : requestLog) {
                String s = (String) aRequestLog;
                log.debug(s);
            }
        }
    }

    /**
     * Builds a <code>Collection</code> to be added as several lines with all the parameter names and values in the
     * <code>HttpServletRequest</code> associated with every user action, for the log. The user login is also associated
     * to each attribute.
     *
     * @param request an <code>HttpServletRequest</code> associated with the user action.
     * @return a <code>Collection</code> containing elements of type <code>String</code> with all the parameter names
     *         and values formatted for the log. Each element corresponds to a parameter name and its value.
     *
     * @since WICCore 2.0
     */
    @SuppressWarnings({"unchecked"})
    private Collection buildDebugLogRequestParameters(HttpServletRequest request) {
        ArrayList requestStringLog = new ArrayList();
        Enumeration enumeration = request.getParameterNames();
        String name = getUserName(request);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(name);
        stringBuffer.append(" QUERY STRING: ");
        stringBuffer.append(request.getQueryString());
        stringBuffer.append(" ]");
        requestStringLog.add(stringBuffer.toString());

        if (!enumeration.hasMoreElements()) {
            stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            stringBuffer.append(name);
            stringBuffer.append(" REQUEST EMPTY]");
            requestStringLog.add(stringBuffer.toString());
        } else {
            requestStringLog.add(buildSeparator(name));
            stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            stringBuffer.append(name);
            stringBuffer.append(" LOGGING REQUEST PARAMETERS ********************]");
            requestStringLog.add(stringBuffer.toString());
            requestStringLog.add(buildSeparator(name));
        }

        while (enumeration.hasMoreElements()) {
            String parameterName = (String) enumeration.nextElement();
            stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            stringBuffer.append(name);
            stringBuffer.append(" request parameter: ");
            stringBuffer.append(parameterName);
            stringBuffer.append(" = ");
            stringBuffer.append(request.getParameter(parameterName));
            stringBuffer.append(" ]");
            requestStringLog.add(stringBuffer.toString());
            if (!enumeration.hasMoreElements()) {
                requestStringLog.add(buildSeparator(name));
                stringBuffer = new StringBuffer();
                stringBuffer.append("[");
                stringBuffer.append(name);
                stringBuffer.append(" END LOGGING REQUEST PARAMETERS ****************]");
                requestStringLog.add(stringBuffer.toString());
                requestStringLog.add(buildSeparator(name));
            }
        }

        return requestStringLog;
    }

    /**
     * Wrapped method of {@link Log#error(Object)} implementation.
     *
     * @param message a <code>String</code> to log.
     * @since WICCore 2.0
     */
    public void error(String message) {
        log.error(message);
    }

    /**
     * Wrapped method of {@link Log#warn(Object)} implementation.
     *
     * @param e a <code>Exception</code> to log.
     * @since WICCore 2.0
     */
    public void error(Exception e) {
        e.printStackTrace();
        log.error(e);
    }

    /**
     * Wrapped method of {@link Log#error(Object)} implementation.
     *
     * @param message a <code>String</code> to log.
     * @param t       t nested <code>Throwable</code>.
     * @since WICCore 2.0
     */
    public void error(String message, Throwable t) {
        log.error(message, t);
    }

    /**
     * Wrapped method of {@link Log#error(Object)} implementation.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @param uri    a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *               action.
     * @since WICCore 2.0
     */
    public void error(String login, Method method, String uri) {
        log.error(buildLogString(login, method, uri, null));
    }

    /**
     * Wrapped method of {@link Log#error(Object)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @since WICCore 2.0
     */
    public void error(String login, Method method, String uri, String additionalInfo) {
        log.error(buildLogString(login, method, uri, additionalInfo));
    }

    /**
     * Wrapped method of {@link Log#error(Object,Throwable)} implementation.
     *
     * @param login     a <code>String</code> with the user login executing actions that require log.
     * @param method    the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by
     *                  the stacktrace.
     * @param uri       a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                  action.
     * @param throwable an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void error(String login, Method method, String uri, Throwable throwable) {
        log.error(buildLogString(login, method, uri, null), throwable);
    }

    /**
     * Wrapped method of {@link Log#error(Object,Throwable)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @param throwable      an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void error(String login, Method method, String uri, String additionalInfo, Throwable throwable) {
        log.error(buildLogString(login, method, uri, additionalInfo), throwable);
    }

    /**
     * Wrapped method of {@link Log#fatal(Object)} implementation.
     *
     * @param message a <code>String</code> to log.
     * @since WICCore 2.0
     */
    public void fatal(String message) {
        log.fatal(message);
    }

    /**
     * Wrapped method of {@link Log#fatal(Object)} implementation.
     *
     * @param message a <code>String</code> to log.
     * @param t       t nested <code>Throwable</code>.
     * @since WICCore 2.0
     */
    public void fatal(String message, Throwable t) {
        log.fatal(message, t);
    }

    /**
     * Wrapped method of {@link Log#fatal(Object)} implementation.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @param uri    a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *               action.
     * @since WICCore 2.0
     */
    public void fatal(String login, Method method, String uri) {
        log.fatal(buildLogString(login, method, uri, null));
    }

    /**
     * Wrapped method of {@link Log#fatal(Object)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @since WICCore 2.0
     */
    public void fatal(String login, Method method, String uri, String additionalInfo) {
        log.fatal(buildLogString(login, method, uri, additionalInfo));
    }

    /**
     * Wrapped method of {@link Log#fatal(Object,Throwable)} implementation.
     *
     * @param login     a <code>String</code> with the user login executing actions that require log.
     * @param method    the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by
     *                  the stacktrace.
     * @param uri       a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                  action.
     * @param throwable an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void fatal(String login, Method method, String uri, Throwable throwable) {
        log.fatal(buildLogString(login, method, uri, null), throwable);
    }

    /**
     * Wrapped method of {@link Log#fatal(Object,Throwable)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @param throwable      an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void fatal(String login, Method method, String uri, String additionalInfo, Throwable throwable) {
        log.fatal(buildLogString(login, method, uri, additionalInfo), throwable);
    }

    /**
     * Method used to check the User login
     *
     * @param request a <code>HttpServletRequest</code> to log
     * @return a <code>String</code> with the login
     */
    private String getUserName(HttpServletRequest request) {
        String name;

        if (request.getUserPrincipal() == null || StringUtils.isBlank(request.getUserPrincipal().getName())) {
            name = "anounymous";
        } else {
            name = request.getUserPrincipal().getName();
        }
        return name;
    }

    /**
     * Wrapped method of {@link Log#info(Object)} implementation.
     *
     * @param message a <code>String</code> to log.
     * @since WICCore 2.0
     */
    public void info(String message) {
        log.info(message);
    }

    /**
     * Wrapped method of {@link Log#info(Object)} implementation.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @param uri    a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *               action.
     * @since WICCore 2.0
     */
    public void info(String login, Method method, String uri) {
        log.info(buildLogString(login, method, uri, null));
    }

    /**
     * Wrapped method of {@link Log#info(Object)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @since WICCore 2.0
     */
    public void info(String login, Method method, String uri, String additionalInfo) {
        log.info(buildLogString(login, method, uri, additionalInfo));
    }

    /**
     * Wrapped method of {@link Log#info(Object,Throwable)} implementation.
     *
     * @param login     a <code>String</code> with the user login executing actions that require log.
     * @param method    the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by
     *                  the stacktrace.
     * @param uri       a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                  action.
     * @param throwable an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void info(String login, Method method, String uri, Throwable throwable) {
        log.info(buildLogString(login, method, uri, null), throwable);
    }

    /**
     * Wrapped method of {@link Log#info(Object,Throwable)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @param throwable      an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void info(String login, Method method, String uri, String additionalInfo, Throwable throwable) {
        log.info(buildLogString(login, method, uri, additionalInfo), throwable);
    }

    /**
     * Wrapped method of {@link Log#trace(Object)} implementation.
     *
     * @param message a <code>String</code> to log.
     * @since WICCore 2.0
     */
    public void trace(String message) {
        log.trace(message);
    }

    /**
     * Wrapped method of {@link Log#trace(Object)} implementation.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @param uri    a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *               action.
     * @since WICCore 2.0
     */
    public void trace(String login, Method method, String uri) {
        log.trace(buildLogString(login, method, uri, null));
    }

    /**
     * Wrapped method of {@link Log#trace(Object)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @since WICCore 2.0
     */
    public void trace(String login, Method method, String uri, String additionalInfo) {
        log.trace(buildLogString(login, method, uri, additionalInfo));
    }

    /**
     * Wrapped method of {@link Log#trace(Object,Throwable)} implementation.
     *
     * @param login     a <code>String</code> with the user login executing actions that require log.
     * @param method    the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by
     *                  the stacktrace.
     * @param uri       a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                  action.
     * @param throwable an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void trace(String login, Method method, String uri, Throwable throwable) {
        log.trace(buildLogString(login, method, uri, null), throwable);
    }

    /**
     * Wrapped method of {@link Log#trace(Object,Throwable)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @param throwable      an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void trace(String login, Method method, String uri, String additionalInfo, Throwable throwable) {
        log.trace(buildLogString(login, method, uri, additionalInfo), throwable);
    }

    /**
     * Wrapped method of {@link Log#warn(Object)} implementation.
     *
     * @param message a <code>String</code> to log.
     * @since WICCore 2.0
     */
    public void warn(String message) {
        log.warn(message);
    }

    /**
     * Wrapped method of {@link Log#warn(Object)} implementation.
     *
     * @param e a <code>Exception</code> to log.
     * @since WICCore 2.0
     */
    public void warn(Exception e) {
        log.warn(e);
    }

    /**
     * Wrapped method of {@link Log#warn(Object)} implementation.
     *
     * @param login  a <code>String</code> with the user login executing actions that require log.
     * @param method the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by the
     *               stacktrace.
     * @param uri    a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *               action.
     * @since WICCore 2.0
     */
    public void warn(String login, Method method, String uri) {
        log.warn(buildLogString(login, method, uri, null));
    }

    /**
     * Wrapped method of {@link Log#warn(Object)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @since WICCore 2.0
     */
    public void warn(String login, Method method, String uri, String additionalInfo) {
        log.warn(buildLogString(login, method, uri, additionalInfo));
    }

    /**
     * Wrapped method of {@link Log#warn(Object,Throwable)} implementation.
     *
     * @param login     a <code>String</code> with the user login executing actions that require log.
     * @param method    the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name by
     *                  the stacktrace.
     * @param uri       a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                  action.
     * @param throwable an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void warn(String login, Method method, String uri, Throwable throwable) {
        log.warn(buildLogString(login, method, uri, null), throwable);
    }

    /**
     * Wrapped method of {@link Log#warn(Object,Throwable)} implementation.
     *
     * @param login          a <code>String</code> with the user login executing actions that require log.
     * @param method         the <code>Method</code> being executed. If <tt>null</tt> tries to determine the method name
     *                       by the stacktrace.
     * @param uri            a <code>String</code> with the <code>HttpServletRequest</code> uri associated with the user
     *                       action.
     * @param additionalInfo a <code>String</code> with usefull information that the developer might want to log during
     *                       an user action.
     * @param throwable      an <code>Exception</code>, <code>Throwable</code> to log.
     * @since WICCore 2.0
     */
    public void warn(String login, Method method, String uri, String additionalInfo, Throwable throwable) {
        log.warn(buildLogString(login, method, uri, additionalInfo), throwable);
    }

    public boolean isTraceEnabled() {
        return true;
    }

    public void debug(String s, Exception e) {
        log.debug(s, e);    
    }

    public void debug(String s, Throwable ex) {
        log.debug(s, ex);
    }

    public void trace(String s, Exception e) {
        log.trace(s, e);
    }
}

