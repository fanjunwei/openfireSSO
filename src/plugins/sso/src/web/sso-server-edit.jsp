<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="java.sql.*,
org.jivesoftware.database.*,
java.util.UUID"
	errorPage="error.jsp"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<head>
<meta name="pageID" content="sso-server-manage" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title><fmt:message key="sso.title" /></title>
<script type="text/javascript">
	function newkey() {
		var s = [];
		var hexDigits = "0123456789abcdef";
		for ( var i = 0; i < 32; i++) {
			s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
		}
		var key = s.join("");
		return key;
	}
	function resetkeyvalue() {
		var txtKey = document.getElementById("key");
		txtKey.value = newkey();
	}
</script>
<%
	Connection connection = DbConnectionManager.getConnection();
	String serverID = request.getParameter("id");
	String isPost = request.getParameter("post");
	String serverName = "";
	String key = "";
	boolean enable = false;
	boolean success = false;

	if ("1".equals(isPost)) {

		serverName = request.getParameter("serverName");
		key = request.getParameter("key");
		String strEnable = request.getParameter("enable");
		if (strEnable != null && strEnable != "") {
			enable = true;
		} else {
			enable = false;
		}

		String sql = "update ssoServer set serverName=? ,`key`=? ,`enable`=? where serverID=?";
		PreparedStatement pre = connection.prepareStatement(sql);
		pre.setString(1, serverName);
		pre.setString(2, key);
		pre.setBoolean(3, enable);
		pre.setString(4, serverID);
		if (pre.executeUpdate() > 0) {
			success = true;
		} else {
			success = false;
		}
		pre.close();
		connection.close();
		if (success)
		{
		
			response.sendRedirect("sso-server.jsp");
		}
	} else {

		String sql = "SELECT serverName,`key`,`enable` FROM ssoServer where serverID=?";
		PreparedStatement pre = connection.prepareStatement(sql);
		pre.setString(1, serverID);
		ResultSet result = pre.executeQuery();
		if (result.next()) {
			serverName = result.getString(1);
			key = result.getString(2);
			enable = result.getBoolean(3);
		}
		result.close();
		pre.close();
		connection.close();

	}
%>
</head>

<body>
	<style type="text/css">
@import "style/style.css";
</style>
	<%
		if (success) {
	%>
	<h2 style="color: green;">
		<fmt:message key="sso.editsuccess" />
	</h2>
	<%
		}
	%>
<a href="sso-server.jsp"><h3><fmt:message key="sso.back" /></h3></a>
	<form action="sso-server-edit.jsp">
		<input type="hidden" name="post" value="1"> <input
			type="hidden" name="id" value="<%=serverID%>">
		<table border="0">
			<tr>
				<td><fmt:message key="sso.serverid" /></td>
				<td><%=serverID%></td>
			</tr>
			<tr>
				<td><fmt:message key="sso.servername" /></td>
				<td><input type="text" name="serverName" size="20"
					value='<%=serverName%>'></td>
			</tr>
			<tr>
				<td><fmt:message key="sso.serverkey" /></td>
				<td><input type="text" name="key" id="key" size="40"
					value='<%=key%>' maxlength="32" readonly="readonly"> <a
					href="javascript:resetkeyvalue()"> <img
						src="images/refresh-16x16.gif" /></a></td>
			</tr>
			<tr>
				<td><fmt:message key="sso.serverenable" /></td>
				<td><input type="checkbox" name="enable"
					<%=enable ? "checked='checked'" : ""%>></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value='<fmt:message key="sso.submit" />'></td>
			</tr>
		</table>
	</form>
</body>
