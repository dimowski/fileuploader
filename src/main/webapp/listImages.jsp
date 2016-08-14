<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
    <title>Все фото</title>
</head>
<body>
    <c:forEach var="tempImage" items="${IMAGES_LIST}">
        <h3><c:out value="${tempImage}"/></h3>
        <img src="upload/<c:out value="${tempImage}"/>"/>
    </c:forEach>
    <br/>
    <button onclick="location.href='index.html'">
        Вернуться к загрузке</button>
</body>
</html>