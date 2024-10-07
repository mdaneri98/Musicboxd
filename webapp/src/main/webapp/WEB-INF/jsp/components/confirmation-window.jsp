<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:url var="cssUrl" value='/static/css/confirmation-window.css'/>
<link rel="stylesheet" href="${cssUrl}">

<!-- Overlay -->
<div id="modalOverlay${param.id}" class="modal-overlay"></div>

<!-- Modal -->
<div id="confirmationModal${param.id}" class="modal">
  <div class="modal-content">
    <p>${param.message}</p>
    <div>
      <button id="modalYes${param.id}"><spring:message code="confirmation.window.yes"/></button>
      <button id="modalNo${param.id}"><spring:message code="confirmation.window.no"/></button>
    </div>
  </div>
</div>