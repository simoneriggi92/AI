<%--
  Created by IntelliJ IDEA.
  it.polito.ai.utilities.User: simone
  Date: 04/04/18
  Time: 11.40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Pagina di Benvenuto</title>
    <%--<%String saluto = "Ciao"; %>--%>
</head>
<body>
<h1><%="Benvenuto utente: "+request.getSession().getAttribute("user")%></h1>
<form action="position" method="post">
    Latitudine: <input type="text" name="latitude">
    <br>
    Longitudine: <input type="text" name="longitude">
    <br>
    Data: <input type="text" name="date">
    <br>
    <input type="submit" value="Invia">
</form>
</body>
</html>
