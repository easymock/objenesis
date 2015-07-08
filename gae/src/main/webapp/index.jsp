<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="main.java.org.objenesis.gae.JspWriterListener" %>
<%@ page import="org.objenesis.strategy.PlatformDescription" %>
<%@ page import="org.objenesis.tck.candidates.SerializableNoConstructor" %>
<%@ page import="org.objenesis.tck.search.SearchWorkingInstantiator" %>
<html>
<head>
  <title>Search Objenesis Working Instantiator</title>
  <style>
    table, th, td {
      border: 1px solid black;
      border-collapse: collapse;
    }
  </style>
</head>
<body>
<p>
  <%= PlatformDescription.describePlatform()%>
</p>
<table style="width:100%">
<tr>
  <th>Instantiator</th>
  <th>Result</th>
</tr>
 <%
    SearchWorkingInstantiator i = new SearchWorkingInstantiator(new JspWriterListener(out));
    i.searchForInstantiator(SerializableNoConstructor.class);
    out.flush();
  %>
</table>
</body>
</html>
