<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="modalOverlay${param.id}" class="modal-overlay"></div>

<div id="confirmationModal${param.id}" class="confirmation-modal">
    <div class="confirmation-content">
        <p class="confirmation-message">${param.message}</p>
        <div class="confirmation-actions">
            <button id="modalNo${param.id}" class="btn btn-secondary">
                <spring:message code="confirmation.window.no"/>
            </button>
            <button id="modalYes${param.id}" class="btn btn-primary">
                <spring:message code="confirmation.window.yes"/>
            </button>
        </div>
    </div>
</div>