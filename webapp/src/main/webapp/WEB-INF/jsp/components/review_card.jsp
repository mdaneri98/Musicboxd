<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<div class="card" style="width: 18rem;">
    <c:url var="reviewImgUrl" value="/images/${param.imgId}" />
    <img src="${reviewImgUrl}" class="card-img-top" alt="...">
    <div class="card-body">
        <h5 class="card-title">${param.title}</h5>
        <p class="card-text"><c:out value="${param.description}"/></p>
        <c:url var="profileURL" value="/webapp_war/profile/${param.userId}"/>
        <a href="${profileURL}" class="btn btn-primary">Ver usuario</a>
    </div>
</div>