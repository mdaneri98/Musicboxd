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

    <c:url var="css" value="/static/css/moderator.css" />
    <link rel="stylesheet" href="${css}">

</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
</div>
<div class="container">
    <h1><spring:message code="label.edit.profile"/></h1>

    <c:url var="editProfileUrl" value="/user/edit" />
    <form:form modelAttribute="userProfileForm" action="${editProfileUrl}" method="post" enctype="multipart/form-data">
        <div class="container">
            <div class="info-container">
                <c:url var="userImgURL" value="/images/${loggedUser.imgId}"/>
                <img id="imagePreview" src="${userImgURL}" class="primary-image" style="cursor: pointer;" onclick="document.getElementById('userImageInput').click();"/>
                <form:input path="profilePicture" id="userImageInput" type="file" accept=".jpg,.jpeg,.png" style="display: none;" onchange="previewImage(event,0)"/>
                <div class="data-container element-details-container">
                    <div>
                        <label><spring:message code="label.username" />
                            <form:errors path="username" cssClass="error" element="p" cssStyle="color:red;"/>
                            <form:input path="username" type="text" />
                        </label>
                    </div>
                    <div>
                        <label><spring:message code="label.name"/>
                            <form:errors path="name" cssClass="error" element="p" cssStyle="color:red;"/>
                            <form:input path="name" type="text" />
                        </label>
                    </div>
                    <div>
                        <label><spring:message code="label.desc"/>
                            <form:errors path="bio" cssClass="error" element="p" cssStyle="color:red;"/>
                            <form:textarea path="bio" type="text" />
                        </label>
                    </div>
                </div>
            </div>
        </div>
        <div style="display: flex; gap: 10px;">
            <c:url value="/user/profile" var="discard_changes_url" />
            <a href="${discard_changes_url}" style="flex: 1;">
                <button type="button" style="width: 100%; height: 100%;"><spring:message code="label.discard.changes"/></button>
            </a>
            <button type="submit" style="flex: 1; width: 100%; height: 100%;"><spring:message code="label.update"/></button>
        </div>
    </form:form>

    <script>
        // Previews Image inserted
        function previewImage(event,index) {
            const file = event.target.files[0];
            const preview = document.getElementById('imagePreview-' + index);

            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    preview.src = e.target.result;
                    // Show the image element
                    preview.style.display = 'block';
                }
                // Read the file and convert it to a data URL
                reader.readAsDataURL(file);
            }
        }
    </script>
    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</div>

</body>
</html>
