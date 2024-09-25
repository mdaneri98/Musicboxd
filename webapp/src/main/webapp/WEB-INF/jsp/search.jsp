<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <spring:message var="pageTitle" text="Search"/>
    <title>${pageTitle}</title>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #141414;
            color: #ffffff;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .search-container {
            width: 90%;
            max-width: 500px;
            background-color: #1f1f1f;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
            font-size: 2.5em;
            margin-bottom: 30px;
        }
        .search-tabs {
            display: flex;
            justify-content: center;
            margin-bottom: 20px;
        }
        .search-tab {
            padding: 10px 20px;
            cursor: pointer;
            border: none;
            background-color: transparent;
            color: #ffffff;
            font-size: 1.2em;
            transition: all 0.3s ease;
        }
        .search-tab.active {
            border-bottom: 2px solid #1DB954;
        }
        .search-wrapper {
            position: relative;
        }
        .search-input {
            width: 100%;
            padding: 15px 40px 15px 15px;
            border: none;
            background-color: #2c2c2c;
            color: #ffffff;
            font-size: 1.1em;
            border-radius: 10px;
            box-sizing: border-box;
        }
        .search-icon {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            width: 20px;
            height: 20px;
            fill: #ffffff;
        }
        .autocomplete-items {
            position: absolute;
            border-radius: 10px;
            z-index: 99;
            top: 100%;
            left: 0;
            right: 0;
            margin-top: 10px;
            background-color: #2c2c2c;
            overflow: hidden;
            max-height: 300px;
            overflow-y: auto;
        }
        .autocomplete-item {
            padding: 10px 15px;
            cursor: pointer;
            display: flex;
            align-items: center;
            transition: background-color 0.3s ease;
        }
        .autocomplete-item:hover {
            background-color: #3c3c3c;
        }
        .autocomplete-item img {
            width: 40px;
            height: 40px;
            object-fit: cover;
            border-radius: 5px;
            margin-right: 15px;
        }
        .autocomplete-item-info {
            display: flex;
            flex-direction: column;
        }
        .autocomplete-item-name {
            font-weight: bold;
        }
        .autocomplete-item-type {
            font-size: 0.8em;
            color: #a7a7a7;
        }
    </style>
</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
    </jsp:include>
</div>
<div class="search-container">
    <h1>Musicboxd</h1>
    <div class="search-tabs">
        <span class="search-tab active" data-type="music">Music</span>
        <span class="search-tab" data-type="users">Users</span>
    </div>
    <div class="search-wrapper">
        <input type="text" class="search-input" id="searchInput" placeholder="Search Musicboxd...">
        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
        </svg>
        <div id="autocompleteList" class="autocomplete-items"></div>
    </div>
</div>

<script>
    // Datos de ejemplo (reemplaza esto con tus datos reales)
    var artists = [
        <c:forEach items="${artists}" var="artist" varStatus="status">
        {id: ${artist.id}, name: "${artist.name}", type: "Artist", url: "<c:url value="/artist/${artist.id}"/>", imgUrl: "<c:url value="/images/${artist.imgId}"/>" }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
    var albums = [
        <c:forEach items="${albums}" var="album" varStatus="status">
        {id: ${album.id}, name: "${album.title}", type: "Album", url: "<c:url value="/album/${album.id}"/>", imgUrl: "<c:url value="/images/${album.imgId}"/>" }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
    var songs = [
        <c:forEach items="${songs}" var="song" varStatus="status">
        {id: ${song.id}, name: "${song.title}", type: "Song", url: "<c:url value="/song/${song.id}"/>", imgUrl: "<c:url value="/images/${song.album.imgId}"/>"}<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
    var users = [
        <c:forEach items="${users}" var="user" varStatus="status">
        {id: ${user.id}, name: "${user.username}", type: "", url: "<c:url value="/user/${user.id}"/>", imgUrl: "<c:url value="/images/${user.imgId}"/>"}<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];

    function handleTabClick(event) {
        document.querySelectorAll('.search-tab').forEach(tab => tab.classList.remove('active'));
        event.target.classList.add('active');
        document.getElementById('searchInput').placeholder = `Search ${event.target.textContent}...`;
        closeAllLists();
    }

    document.querySelectorAll('.search-tab').forEach(tab => {
        tab.addEventListener('click', handleTabClick);
    });

    function autocomplete(inp) {
        var currentFocus;
        inp.addEventListener("input", function(e) {
            var a, b, i, val = this.value;
            closeAllLists();
            if (!val) {
                return false;
            }
            currentFocus = -1;
            a = document.createElement("DIV");
            a.setAttribute("id", this.id + "autocomplete-list");
            a.setAttribute("class", "autocomplete-items");
            this.parentNode.appendChild(a);

            var activeTab = document.querySelector('.search-tab.active').dataset.type;
            var searchArray = (activeTab === 'music') ? [...artists, ...albums, ...songs] : users;

            searchArray.forEach(function (item) {
                if (item.name.substr(0, val.length).toUpperCase() == val.toUpperCase()) {
                    var b = document.createElement("DIV");
                    b.innerHTML = createAutocompleteItem(item);
                    b.addEventListener("click", function (e) {
                        inp.value = item.name;
                        window.location.href = item.url;
                        closeAllLists();
                    });
                    a.appendChild(b);
                }
            });
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

        function createAutocompleteItem(item) {
            return `
        <div class="autocomplete-item">
            <img src="`+ item.imgUrl + `" alt="`+ item.name +`">
            <div class="autocomplete-item-info">
                <span class="autocomplete-item-name">` + item.name + `</span>
                <span class="autocomplete-item-type">` + item.type + `</span>
            </div>
        </div>
    `;
        }

        function removeActive(x) {
            for (var i = 0; i < x.length; i++) {
                x[i].classList.remove("autocomplete-active");
            }
        }
    }

    function closeAllLists(elmnt) {
        var x = document.getElementsByClassName("autocomplete-items");
        for (var i = 0; i < x.length; i++) {
            if (elmnt != x[i] && elmnt != document.getElementById('searchInput')) {
                x[i].parentNode.removeChild(x[i]);
            }
        }
    }


    document.addEventListener("click", function (e) {
        closeAllLists(e.target);
    });

    autocomplete(document.getElementById("searchInput"));
</script>
</body>
</html>