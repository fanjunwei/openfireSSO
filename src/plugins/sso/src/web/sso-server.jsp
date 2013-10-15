<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.jivesoftware.database.*"%>
<%@page import="java.sql.*"%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>sso服务器管理</title>
</head>

<body>
	<%
Connection connection = DbConnectionManager.getConnection();
String sql = "SELECT serverID,serverName,`key`,`enable` FROM openfile.ssoServer";
PreparedStatement pre = connection.prepareStatement(sql);
ResultSet result = pre.executeQuery();
%>

	<table>
		<tr>
			<td>serverID</td> <td>serverName</td> <td>key</td> <td>enable</td>
		</tr>
		<%while(result.next()){
			%>
		<tr>
		<%="<td>"+result.getString(0)+"</td> <td>"+result.getString(1)+"</td> <td>"+result.getString(2)+"</td> <td>"+result.getString(3)+"</td>"%>
		</tr>
		<%
		}
		result.close();
		pre.close();
		connection.close();
		%>

	</table>
</body>
