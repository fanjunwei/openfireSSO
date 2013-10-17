package org.fjw.openfire.plugin.sso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.jivesoftware.database.DbConnectionManager;

public class HttpParmDecry {

	HttpServletRequest request;
	String sid;
	String key;
	boolean enable = false;

	public static final boolean HTTP_ENCRYED = true;

	public HttpParmDecry(HttpServletRequest request) throws ServerDisableException {
		this.request = request;
		if (HTTP_ENCRYED) {
			sid = request.getParameter("sid");
			key = getKey(sid);
			System.out.println("sid=" + sid);
			System.out.println("key=" + key);
		}
	}

	private String getKey(String sid) throws ServerDisableException {
		String outkey = null;
		try {
			Connection connection = DbConnectionManager.getConnection();
			String sql = "SELECT `key`,`enable` FROM ssoServer where serverID=?";
			PreparedStatement pre = connection.prepareStatement(sql);
			pre.setString(1, sid);
			ResultSet result = pre.executeQuery();
			if (result.next()) {
				outkey = result.getString(1);
				enable = result.getBoolean(2);
			}
			result.close();
			pre.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(!enable)
			throw new ServerDisableException();
		return outkey;
	}

	public String getParameter(String parm) {
		String str = request.getParameter(parm);
		if (HTTP_ENCRYED) {
			if (key == null)
				return null;
			else
				return RC4.decry_RC4(str, key);
		} else {
			return str;
		}

	}
}
