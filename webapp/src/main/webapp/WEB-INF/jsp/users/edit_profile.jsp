<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <spring:message var="pageTitle" code="edit.profile.title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>
</head>
<body>
    <div class="main-container">
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
            <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
            <jsp:param name="unreadNotificationCount" value="${loggedUser.unreadNotificationCount}"/>
        </jsp:include>

        <main class="content-wrapper">
            <div class="mod-form-container">
                <h1 class="mod-form-title">
                    <spring:message code="label.edit.profile"/>
                </h1>

                <c:url var="editProfileUrl" value="/user/edit" />
                <form:form modelAttribute="userProfileForm" action="${editProfileUrl}" method="post" enctype="multipart/form-data">
                    <div class="mod-form">

                        <c:url var="userImgURL" value="/images/${loggedUser.image.id}"/>
                        <form:input path="profilePicture" id="userImageInput" type="file" accept=".jpg,.jpeg,.png" cssClass="hidden-input" onchange="previewImage(event)"/>
                        <img id="imagePreview" src="${userImgURL}" class="entity-image mod-editable-image" style="cursor: pointer;" onclick="document.getElementById('userImageInput').click();" alt="Profile Image"/>
                            
                        <div class="mod-entity-details">
                            <div>
                                <label class="mod-label">
                                    <spring:message code="label.username" />
                                </label>
                                <form:errors path="username" cssClass="form-error"/>
                                <form:input path="username" type="text" cssClass="form-control"/>
                            </div>

                            <div>
                                <label class="mod-label">
                                    <spring:message code="label.name"/>
                                </label>
                                <form:errors path="name" cssClass="form-error"/>
                                <form:input path="name" type="text" cssClass="form-control"/>
                            </div>

                            <div>
                                <label class="mod-label">
                                    <spring:message code="label.desc"/>
                                </label>
                                <form:errors path="bio" cssClass="form-error"/>
                                <form:textarea path="bio" rows="4" cssClass="form-control"/>
                            </div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <c:url value="/user/profile" var="discard_changes_url" />
                        <a href="${discard_changes_url}" class="btn btn-secondary">
                            <spring:message code="label.discard.changes"/>
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <spring:message code="label.update"/>
                        </button>
                    </div>
                </form:form>
            </div>

            <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
        </main>
    </div>

    <script>
        function previewImage(event) {
            const file = event.target.files[0];
            const preview = document.getElementById('imagePreview');

            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                }
                reader.readAsDataURL(file);
            }
        }
    </script>
</body>
</html>
