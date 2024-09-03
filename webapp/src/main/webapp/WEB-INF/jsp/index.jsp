<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <spring:message var="home" text="Index"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${home}"/>
    </jsp:include>

    <c:url var="cssIndexUrl" value="/static/css/index.css" />
    <link rel="stylesheet" href="${cssIndexUrl}">

</head>
<body>
<div class="search-container">
    <h1>Musicboxd</h1>
    <form class="search-form" action="#" method="get" autocomplete="off">
        <input type="text" class="search-input" id="myInput" placeholder="Search any artist" name="q">
        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
        </svg>
    </form>
</div>

<!-- Cards Container -->
<div class="cards-container">
    <c:forEach var="entry" items="${reviewsWithImg}">
        <jsp:include page="/WEB-INF/jsp/components/review_card.jsp">
            <jsp:param name="title" value="${entry.key.title}"/>
            <jsp:param name="description" value="${entry.key.description}"/>
            <jsp:param name="userId" value="${entry.key.userId}"/>
            <jsp:param name="imgId" value="${entry.value}"/>
        </jsp:include>
    </c:forEach>
</div>

<script>
    var artists = [
        <c:forEach items="${artists}" var="artist" varStatus="status">
        {
            id: ${artist.id},
            img_id: ${artist.imgId},
            name: "<c:out value="${artist.name}"/>",
        }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];

    function autocomplete(inp, arr) {
        var currentFocus;
        inp.addEventListener("input", function(e) {
            var a, b, i, val = this.value;
            closeAllLists();
            if (!val) { return false; }
            currentFocus = -1;
            a = document.createElement("DIV");
            a.setAttribute("id", this.id + "autocomplete-list");
            a.setAttribute("class", "autocomplete-items");
            this.parentNode.appendChild(a);
            for (i = 0; i < arr.length; i++) {
                if (arr[i].name.substr(0, val.length).toUpperCase() == val.toUpperCase()) {
                    b = document.createElement("DIV");
                    b.innerHTML = "<strong>" + arr[i].name.substr(0, val.length) + "</strong>";
                    b.innerHTML += arr[i].name.substr(val.length);
                    b.innerHTML += "<input type='hidden' value='" + arr[i].name + "' data-id='" + arr[i].id + "'>";
                    b.addEventListener("click", function(e) {
                        inp.value = this.getElementsByTagName("input")[0].value;
                        var artistId = this.getElementsByTagName("input")[0].getAttribute("data-id");
                        window.location.href = "/webapp_war/artist/" + artistId;
                        closeAllLists();
                    });
                    a.appendChild(b);
                }
            }
        });
        inp.addEventListener("keydown", function(e) {
            var x = document.getElementById(this.id + "autocomplete-list");
            if (x) x = x.getElementsByTagName("div");
            if (e.keyCode == 40) {
                currentFocus++;
                addActive(x);
            } else if (e.keyCode == 38) {
                currentFocus--;
                addActive(x);
            } else if (e.keyCode == 13) {
                e.preventDefault();
                if (currentFocus > -1) {
                    if (x) x[currentFocus].click();
                }
            }
        });
        function addActive(x) {
            if (!x) return false;
            removeActive(x);
            if (currentFocus >= x.length) currentFocus = 0;
            if (currentFocus < 0) currentFocus = (x.length - 1);
            x[currentFocus].classList.add("autocomplete-active");
        }
        function removeActive(x) {
            for (var i = 0; i < x.length; i++) {
                x[i].classList.remove("autocomplete-active");
            }
        }
        function closeAllLists(elmnt) {
            var x = document.getElementsByClassName("autocomplete-items");
            for (var i = 0; i < x.length; i++) {
                if (elmnt != x[i] && elmnt != inp) {
                    x[i].parentNode.removeChild(x[i]);
                }
            }
        }
        document.addEventListener("click", function (e) {
            closeAllLists(e.target);
        });
    }

    autocomplete(document.getElementById("myInput"), artists);
</script>
</body>
</html>