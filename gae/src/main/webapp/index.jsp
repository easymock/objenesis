<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="main.java.org.objenesis.gae.JspReporter" %>
<%@ page import="main.java.org.objenesis.gae.JspWriterListener" %>
<%@ page import="org.objenesis.strategy.PlatformDescription" %>
<%@ page import="org.objenesis.tck.Main" %>
<%@ page import="org.objenesis.tck.candidates.SerializableNoConstructor" %>
<%@ page import="org.objenesis.tck.search.SearchWorkingInstantiator" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
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
<h2>Platform description</h2>
<p>
<%= PlatformDescription.describePlatform()%>
</p>
<h2>Properties</h2>
<table style="width:100%">
    <tr>
        <th>Key</th>
        <th>Value</th>
    </tr>
<%
  for(Map.Entry<Object, Object> property : new TreeMap<Object, Object>(System.getProperties()).entrySet()) {
%>
  <%= "<tr><td>" + property.getKey() + "</td><td>" + property.getValue() + "</td></tr>" %>
<%
  }
%>
</table>
<h2>Supported instantiator</h2>
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
<h2>TCK</h2>
<table style="width:100%">
</table>
<%
  JspReporter reporter = new JspReporter(out, out);
  try {
    boolean result = Main.run(reporter);
    reporter.printResult(result);
  }
  catch(Exception e) {
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    e.printStackTrace(new PrintWriter(out));
  }
%>
</body>
</html>
