<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.tzc.bean.Status"%>
<%@page import="com.tzc.spider.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>My JSP 'index.jsp' starting page</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	</head>

	<body>
		<%
			String problemId = request.getParameter("problemId");
			String language = request.getParameter("language");
			String ojName = request.getParameter("OJ");
			if (language.equals("C")) {
				language = "1";
			} else {
				language = "2";
			}
			String source = new String(request.getParameter("source").getBytes(
					"iso-8859-1"), "gb2312");
			String answer = new SpiderJudge().judge("tzcjudge", "tzcjudge",
					problemId, language, source, ojName).toString();
		%>
		<br />
		<hr />
		<p>
			problemId =
			<%=problemId%>
		</p>
		<p>
			language =
			<%=language%>
		</p>
		<code>
			source =

			<%=source%>
		</code>
		<p>
			<%=answer%>
		</p>
	</body>
</html>
