<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<c:url var="cssUrl" value='/static/css/confirmation-window.css'/>
<link rel="stylesheet" href="${cssUrl}">
<div id="confirmationModal" class="modal">
  <div class="modal-content">
    <p>${param.message}</p>
    <div>
      <button id="modalYes">Yes</button>
      <button id="modalNo">No</button>
    </div>
  </div>
</div>