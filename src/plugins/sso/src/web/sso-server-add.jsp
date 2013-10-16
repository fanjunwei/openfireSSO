<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.jivesoftware.database.*"%>
<%@page import="java.sql.*"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<head>
<meta name="pageID" content="sso-server-add"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title><fmt:message key="sso.title" /></title>
</head>

<body>
    <style type="text/css">
        @import "style/style.css";
    </style>
	<%
Connection connection = DbConnectionManager.getConnection();
String sql = "SELECT serverID,serverName,`key`,`enable` FROM openfile.ssoServer";
PreparedStatement pre = connection.prepareStatement(sql);
ResultSet result = pre.executeQuery();
%>
<table class="jive-table" cellpadding="3" cellspacing="0" border="0" width="100%">
  <thead>
    <tr>
      <th nowrap="" align="left" colspan="2">Name</th>
      <th nowrap="">Status</th>
      <th nowrap="">Members (Active/Total) </th>
      <th nowrap="">Queues</th>
      <th nowrap="">Users in Queues</th>
      <th nowrap="">Edit</th>
      <th nowrap="">Delete</th>
    </tr>
  </thead>
    
    <tbody><tr class="c1">
      <td width="39%" colspan="2">
        <a href="workgroup-queues.jsp?wgID=demo@workgroup.www.fjw.com">
            <b>demo</b>
          </a>
        
        <span class="jive-description">
          <br>
          Demo workgroup
        </span>
        
      </td>
      <td width="10%" align="center" nowrap="">
     Dstatw
      </td>
      <td width="10%" align="center">
        <a href="workgroup-agents-status.jsp?wgID=demo@workgroup.www.fjw.com">
          0/2
        </a>
      </td>
      <td width="10%" align="center">
        1
      </td>
      <td width="10%" align="center">
        0
      </td>
      <td width="10%" align="center">
        <a href="workgroup-queues.jsp?wgID=demo@workgroup.www.fjw.com" title="Click to edit...">
          <img src="images/edit-16x16.gif" width="16" height="16" border="0" alt="">
        </a>
      </td>
      <td width="10%" align="center">
        <a href="workgroup-delete.jsp?wgID=demo@workgroup.www.fjw.com" title="Click to delete...">
          <img src="images/delete-16x16.gif" width="16" height="16" border="0" alt="">
        </a>
      </td>
    </tr>
    
</tbody></table>

	<table>
		<tr>
			<td>serverID</td> <td>serverName</td> <td>key</td> <td>enable</td>
		</tr>
		<%while(result.next()){
			System.out.println("into");
			%>
		<tr>
		<%="<td>"+result.getString(1)+"</td> <td>"+result.getString(2)+"</td> <td>"+result.getString(3)+"</td> <td>"+result.getString(4)+"</td>"%>
		</tr>
		<%
		}
		result.close();
		pre.close();
		connection.close();
		%>

	</table>
</body>
