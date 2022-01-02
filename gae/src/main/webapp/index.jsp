<%--

    Copyright 2006-2022 the original author or authors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="org.objenesis.gae.JspReporter" %>
<%@ page import="org.objenesis.gae.JspWriterListener" %>
<%@ page import="org.objenesis.strategy.PlatformDescription" %>
<%@ page import="org.objenesis.tck.Main" %>
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
  for(Map.Entry<Object, Object> property : new TreeMap<>(System.getProperties()).entrySet()) {
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
    Main.run(reporter);
  }
  catch(Exception e) {
    out.println("<pre>");
    e.printStackTrace(new PrintWriter(out));
    out.println("</pre>");
  }
%>
</body>
</html>
