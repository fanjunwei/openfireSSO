<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="java.sql.*,
org.jivesoftware.database.*,
java.util.UUID"
 errorPage="error.jsp"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<head>
<meta name="pageID" content="sso-server-add" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title><fmt:message key="sso.title" /></title>
<%
	String add = request.getParameter("add");
	String serverName = request.getParameter("serverName");
	String strEnable = request.getParameter("serverEnable");
	String serverID = null;
	String key = null;
	boolean isPost = false;
	boolean success = false;
	boolean serverEnable = false;
	if ("1".equals(add)) {
		isPost = true;
		if (strEnable != null && strEnable != "") {
			serverEnable = true;
		} else {
			serverEnable = false;
		}
		serverID = UUID.randomUUID().toString().replace("-", "");
		key = UUID.randomUUID().toString().replace("-", "");
		Connection connection = DbConnectionManager.getConnection();
		String sql = "insert into ssoServer (serverID,serverName,`key`,`enable`) values (?,?,?,?)";
		PreparedStatement pre = connection.prepareStatement(sql);
		pre.setString(1, serverID);
		pre.setString(2, serverName);
		pre.setString(3, key);
		pre.setBoolean(4, serverEnable);

		int result = pre.executeUpdate();
		if (result > 0) {
			success = true;
		} else {
			success = false;
		}
		pre.close();
		connection.close();
	}
%>
</head>

<body>
	<style type="text/css">
@import "style/style.css";
</style>
<%if(isPost) {%>

<%if(success){ %>
<h2 style="color: green;">
<fmt:message key="sso.addsuccess" />
</h2>
<%}else{ %>
<h2 style="color: red;">
<fmt:message key="sso.adderror" />
</h2>
<%} %>

<%} %>
	<form action="sso-server-add.jsp">
		<input type="hidden" name="add" value="1">
		<table border="0">
			<tr>
				<td><fmt:message key="sso.servername" /></td>
				<td><input type="text" name="serverName" size="20"></td>
			</tr>
			<tr>
				<td><fmt:message key="sso.serverenable" /></td>
				<td><input type="checkbox" name="serverEnable" size="20">
				</td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value='<fmt:message key="sso.submit" />'></td>
			</tr>
		</table>
	</form>
</body>
