<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="java.sql.*,
org.jivesoftware.database.*"
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
	function deleteConfim(url) {
		if (confirm('<fmt:message key="sso.deleteConfim" />')) {
			window.location.href = url;
		}
	}
</script>
<%
	String method = request.getParameter("method");
	String id = request.getParameter("id");
	boolean delSuccess = false;
%>
</head>

<body>
	<style type="text/css">
@import "style/style.css";
</style>
	<%
		Connection connection = DbConnectionManager.getConnection();
		if ("delete".equals(method) && id != null) {
			String deleteSql = "delete from ssoServer where serverID=?";
			PreparedStatement preDel = connection
					.prepareStatement(deleteSql);
			preDel.setString(1, id);
			if (preDel.executeUpdate() > 0) {
				delSuccess = true;
			} else {
				delSuccess = false;
			}
			preDel.close();
		}

		String sql = "SELECT serverID,serverName,`key`,`enable` FROM ssoServer";
		PreparedStatement pre = connection.prepareStatement(sql);
		ResultSet result = pre.executeQuery();
	%>
	<%
		if (delSuccess) {
	%>
	<h2 style="color: green;">
		<fmt:message key="sso.delsuccess" />
	</h2>
	<%
		}
	%>
	<table class="jive-table" cellpadding="3" cellspacing="0" border="0"
		width="100%">
		<thead>
			<tr>
				<th nowrap=""><fmt:message key="sso.servername" /></th>
				<th nowrap=""><fmt:message key="sso.serverid" /></th>
				<th nowrap=""><fmt:message key="sso.serverkey" /></th>
				<th nowrap=""><fmt:message key="sso.serverenable" /></th>
				<th nowrap=""><fmt:message key="sso.edit" /></th>
				<th nowrap=""><fmt:message key="sso.delete" /></th>
			</tr>
		</thead>

		<tbody>
			<%
				while (result.next()) {
			%>
			<tr class="c1">
				<td>
					<table>
						<tr>
							<td>
								<%
									if (result.getBoolean(4)) {
								%> <img src="images/bullet-green-14x14.gif" width="14"
								height="14" border="0"
								alt='<fmt:message key="sso.serverenable" />' />
							</td>
							<%
								} else {
							%>
							<img src="images/bullet-red-14x14.gif" width="14" height="14"
								border="0" alt='<fmt:message key="sso.serverdisable" />' />
							</td>
							<%
								}
							%>
							<td><a href="sso-server-edit.jsp?id=<%=result.getString(1)%>">
							<%=result.getString(2)%></a></td>
						</tr>
					</table>
				</td>
				<td><%=result.getString(1)%></td>
				<td><%=result.getString(3)%></td>
				<td><%=result.getBoolean(4) ? "YES" : "NO"%></td>
				<td><a href="sso-server-edit.jsp?id=<%=result.getString(1)%>" title="Click to edit..."> <img
						src="images/edit-16x16.gif" width="16" height="16" border="0"
						alt="">
				</a></td>
				<td><a
					href='javascript:deleteConfim("sso-server.jsp?method=delete&id=<%=result.getString(1)%>")'
					title="Click to delete..."> <img src="images/delete-16x16.gif"
						width="16" height="16" border="0" alt="">
				</a></td>
			</tr>
			<%
				}
				result.close();
				pre.close();
				connection.close();
			%>
		</tbody>
	</table>
</body>
