<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <spring:message var="pageTitle" text="Moderator"/>
    <title>${pageTitle}</title>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>

    <c:url var="cssUrl" value="/static/css/search.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
<div>
    <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
        <jsp:param name="loggedUserImgId" value="${loggedUser.imgId}"/>
        <jsp:param name="moderator" value="${loggedUser.moderator}"/>
    </jsp:include>
</div>
<div class="search-container">
    <h1>Moderator</h1>
    <div class="search-tabs">
        <div class="search-tab active" data-type="artists">
            <span>Artist</span>
        </div>
        <div class="search-tab" data-type="albums">
            <span>Album</span>
        </div>
        <div class="search-tab" data-type="songs">
            <span>Song</span>
        </div>
    </div>
    <div class="search-wrapper">
        <input type="text" style="display: none" class="search-input" id="searchInput" placeholder="Search Musicboxd...">
        <svg class="search-icon" id="searchIcon" style="display: none" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
        </svg>
        <div id="autocompleteList" class="autocomplete-items"></div>
    </div>
    <div>
        <button id="redirectButton" onclick="redirect()">Add Artist</button>
    </div>
</div>

<script>
    var selected_item;

    // Define los arrays de datos
    var artists = [
        <c:forEach items="${artists}" var="artist" varStatus="status">
            {   id: ${artist.id},
                name: "${artist.name}",
                type: "Artist",
                url: "#",
                imgUrl: "<c:url value='/images/${artist.imgId}'/>" }
        <c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];

    var albums = [
        <c:forEach items="${albums}" var="album" varStatus="status">
        {id: ${album.id}, name: "${album.title}", type: "Album", url: "#", imgUrl: "<c:url value='/images/${album.imgId}'/>" }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];

    var songs = [
        <c:forEach items="${songs}" var="song" varStatus="status">
        {id: ${song.id}, name: "${song.title}", type: "Song", url: "#", imgUrl: "<c:url value='/images/${song.album.imgId}'/>"}<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];

    function show_button() {
        var activeTab = document.querySelector('.search-tab.active').dataset.type;
        var button = document.getElementById('redirectButton');

        // Cambia el texto del botón dependiendo de la pestaña
        switch (activeTab) {
            case 'artists':
                button.textContent = "Add Artist";
                break;
            case 'albums':
                button.textContent = "Add Album";
                break;
            case 'songs':
                button.textContent = "Add Song";
                break;
        }
        button.style.display = 'inline-block';
    }

    function hide_button() {
        // Oculta el botón al cambiar de pestaña o limpiar el input
        document.getElementById('redirectButton').style.display = 'none';
    }

    function redirect() {
        <c:url var="addArtistUrl" value="/mod/add/artist"/>
        <c:url var="addAlbumUrlBase" value="/mod/add/artist/"/>
        <c:url var="addSongUrlBase" value="/mod/add/album/"/>

        var addArtistUrl = "${addArtistUrl}";
        var addAlbumUrlBase = "${addAlbumUrlBase}";
        var addSongUrlBase = "${addSongUrlBase}";

        var activeTab = document.querySelector('.search-tab.active').dataset.type;
        switch (activeTab) {
            case 'artists':
                console.log("Changed href");
                window.location.href = addArtistUrl;
                break;
            case 'albums':
                if (selected_item) {
                    var url = addAlbumUrlBase + selected_item.id + "/album";
                    window.location.href = url;
                }
                break;
            case 'songs':
                if (selected_item) {
                    var url = addSongUrlBase + selected_item.id + "/song";
                    window.location.href = url;
                }
                break;
        }
    }

    // Funcionalidad de las pestañas
    document.querySelectorAll('.search-tab').forEach(tab => {
        tab.addEventListener('click', function () {
            // Eliminar la clase activa de todas las pestañas
            document.querySelectorAll('.search-tab').forEach(t => t.classList.remove('active'));
            // Agregar clase activa a la pestaña clickeada
            this.classList.add('active');
            // Cambiar boton a la clase activa
            show_button();
            // Limpiar el campo de entrada
            document.getElementById('searchInput').value = '';
            closeAllLists();

            // Verificar el tipo de pestaña y mostrar/ocultar el input
            const activeTab = this.dataset.type;
            const searchInput = document.getElementById('searchInput');
            const searchIcon = document.getElementById('searchIcon');
            if (activeTab === 'artists') {
                searchInput.style.display = 'none'; // Oculta el input
                searchIcon.style.display = 'none'; // Oculta el input
            } else {
                searchInput.style.display = 'block'; // Muestra el input
                searchIcon.style.display = 'inline-block'; // Mostrar el ícono de búsqueda
            }
        });
    });

    function autocomplete(inp) {
        var currentFocus;
        inp.addEventListener("input", function(e) {
            var a, val = this.value;
            closeAllLists();
            if (!val) { return false; }
            currentFocus = -1;
            a = document.createElement("DIV");
            a.setAttribute("id", this.id + "autocomplete-list");
            a.setAttribute("class", "autocomplete-items");
            this.parentNode.appendChild(a);

            var activeTab = document.querySelector('.search-tab.active').dataset.type;
            var searchArray;
            switch(activeTab) {
                case 'albums':
                    searchArray = artists;
                    break;
                case 'songs':
                    searchArray = albums;
                    break;
                default:
                    searchArray = [];
            }

            searchArray.forEach(function (item) {
                if (item.name.toLowerCase().includes(val.toLowerCase())) {
                    var b = document.createElement("DIV");
                    b.innerHTML = createAutocompleteItem(item);
                    b.addEventListener("click", function(e) {
                        inp.value = item.name
                        selected_item = item
                        show_button()
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

        function removeActive(x) {
            for (var i = 0; i < x.length; i++) {
                x[i].classList.remove("autocomplete-active");
            }
        }
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