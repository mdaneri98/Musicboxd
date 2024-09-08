<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: matiasdaneri
  Date: 08/09/2024
  Time: 15:09
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <title>Login page</title>
</head>
<body>

<c:url var="loginUrl" value="/user/login" />
<form method="post" action="${loginUrl}">
    <div>
        <label>Username <input type="text" name="username"/></label>
    </div>
    <div>
        <label>Password <input type="password" name="password"/></label>
    </div>
    <div>
        <label>Remember me <input type="checkbox" name="remember_me"/></label>
    </div>
    <div>
        <label>Submit<input type="submit"/></label>
    </div>
</form>

<c:url var="registerUrl" value="/user/register" />
<a href="${registerUrl}">¿Todavía no tenes una cuenta?</a>

</body>
</html>
