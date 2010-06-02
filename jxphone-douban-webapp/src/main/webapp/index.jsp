<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.jxphone.douban.*" %>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.IOException"%>
<%@page import="oauth.signpost.exception.OAuthException"%>
<%!
	String apikey = "042bc009d7d4a04d0c83401d877de0e7";
	String secret = "a9bb2d7f8cc00110";
	String sessionKey = "douban.oauth";
	
	void logout(HttpServletResponse response, HttpSession session) throws IOException{
		session.removeAttribute(sessionKey);
		response.sendRedirect("/");
	}
	
	boolean login(HttpServletResponse response, HttpSession session, String oauthToken) throws IOException, OAuthException {
		DoubanOAuthConsumer consumer = getConsumer(session);
		if(consumer == null) {
			//log("consumer == null");
			return false;
		}
		
		if(!consumer.getToken().equals(oauthToken)) {
			//log("token 不一致");
			return false;
		}
			
		DoubanOAuthProvider provider = new DoubanOAuthProvider();
		try {
			provider.retrieveAccessToken(consumer, null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		session.setAttribute(sessionKey, consumer);
		return true;
		
	}
	
	String getProfile(HttpSession session) throws IOException, OAuthException {
		DoubanOAuthConsumer consumer = (DoubanOAuthConsumer) session.getAttribute(sessionKey);
		if(consumer == null) return null;
		
		String url = "http://api.douban.com/people/%40me";
		url = consumer.sign(url);
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(
				new URL(url).openStream(), "UTF-8"));
			char[] buf = new char[4096];
			while(true) {
				int n = reader.read(buf);
				if(n == -1) break;
				sb.append(buf, 0, n);
			}
			return sb.toString();
		} catch(Exception e) {
			return null;
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
	}
	
	String getLogintUrl(HttpServletRequest request, HttpSession session) throws IOException, OAuthException {
		DoubanOAuthProvider provider = new DoubanOAuthProvider();
		DoubanOAuthConsumer consumer = new DoubanOAuthConsumer(apikey, secret);
		String url = provider.retrieveRequestToken(consumer, request.getRequestURL().toString());
		session.setAttribute(sessionKey, consumer);
		return url;
	}
	
	DoubanOAuthConsumer getConsumer(HttpSession session) {
		return (DoubanOAuthConsumer) session.getAttribute(sessionKey);
	}
	
	
%>
<%
	if(request.getParameter("logout") != null) {
		logout(response, session);
		return;
	}

	String profile = getProfile(session);
	
	if(profile != null) {
		profile = profile.replace("&", "&amp;").replace("<", "&lt;");
%>
	
<ul>
		<li><a href="?logout">登出</a></li>
		<li>token: <%= getConsumer(session).getToken() %></li>
		<li>tokenSecret: <%= getConsumer(session).getTokenSecret() %></li>
		<li>profile
			<pre><%= profile %>
			</pre>
		</li>
	</ul>
<%			
		return;
	}

	String oauthToken = request.getParameter("oauth_token");
	if(oauthToken != null) {
		if(login(response, session, oauthToken)) {
			response.sendRedirect("/");
		} else {
			out.write("登录失败");
		}
		return;
	}
%>

<ul>
	<li><a href="<%= getLogintUrl(request, session) %>">登录</a></li>
</ul>
