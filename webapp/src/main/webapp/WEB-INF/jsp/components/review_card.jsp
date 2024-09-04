<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="card" style="width: 18rem;">
    <c:url var="reviewImgUrl" value="/images/${param.imgId}" />
    <img src="${reviewImgUrl}" class="card-img-top" alt="Imagen de la reseña">
    <div class="card-body">
        <h5 class="card-title"><c:out value="${param.title}"/></h5>
        <p class="card-text"><c:out value="${param.description}"/></p>
        <c:url var="profileURL" value="/profile/${param.userId}"/>
        <!-- <a href="{profileURL}" class="btn btn-primary">View User</a>     Add $ to {profileURL} when removing comment-->
    </div>
</div>
