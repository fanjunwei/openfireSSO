package org.fjw.openfire.plugin.sso;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;
import org.fjw.openfire.plugin.SSOPlugin;
import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.Presence;

import com.alibaba.fastjson.JSON;

public class SSOStatusServlet extends HttpServlet {

	private SSOPlugin plugin;

	public SSOStatusServlet() {

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		plugin = (SSOPlugin) XMPPServer.getInstance().getPluginManager()
				.getPlugin("sso");
		AuthCheckFilter.addExclude("sso/status");
	}

	@Override
	public void destroy() {
		super.destroy();
		AuthCheckFilter.removeExclude("sso/status");
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ResultObject outobj = new ResultObject();
		try {
			HttpParmDecry parmdecry = new HttpParmDecry(request);
			String jid = parmdecry.getParameter("jid");
			String token = parmdecry.getParameter("token");
			System.out.println("jid=" + jid);
			System.out.println("token=" + token);
			try {
				Presence presence = plugin.getPresence(jid);
				if (presence == null) {
					outobj.success = false;
					outobj.status = "401";
					outobj.message = "没有登录";
				} else {
					Element tokenEle = presence.getChildElement("token",
							"com:sso");
					String userToken = tokenEle.getText();
					if (token != null && userToken != null
							&& userToken.equals(MD5Helper.MD5(token))) {
						outobj.success = true;
						outobj.status = "200";
						outobj.message = null;
					} else {
						outobj.success = false;
						outobj.status = "402";
						outobj.message = "token认证错误";
					}
				}
			} catch (UserNotFoundException e) {
				outobj.success = false;
				outobj.status = "404";
				outobj.message = "无此用户";
			} catch (IllegalArgumentException e) {
				outobj.success = false;
				outobj.status = "404";
				outobj.message = "无此用户";
			}
		} catch (ServerDisableException ex) {
			outobj.success = false;
			outobj.status = "501";
			outobj.message = "ServerID错误或不可用";
		}
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		String json = JSON.toJSONString(outobj);
		response.getWriter().print(json);

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}