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
<div class="v-container">
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

    <div class="container">
        <c:if test="${top_artists.size() > 0}">
            <h4>Popular artists</h4>
            <div class="carousel-container">
                <div class="carousel">
                    <c:forEach var="artist" items="${top_artists}" varStatus="status">
                        <c:url var="artistUrl" value="/artist/${artist.id}"/>
                        <div class="item">
                            <a href="${artistUrl}" class="artist">
                                <c:url var="artistImgUrl" value="/images/${artist.imgId}"/>
                                <img src="${artistImgUrl}" alt="Artist ${status.index + 1}">
                                <p><c:out value="${artist.name}"/></p>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>

    <div class="container">
        <c:if test="${top_albums.size() > 0}">
            <h4>Popular albums</h4>
            <div class="carousel-container">
                <div class="carousel">
                    <c:forEach var="album" items="${top_albums}" varStatus="status">
                        <c:url var="albumUrl" value="/album/${album.id}"/>
                        <div class="item">
                            <a href="${albumUrl}" class="album">
                                <c:url var="albumImgURL" value="/images/${album.imgId}"/>
                                <img src="${albumImgURL}" alt="Album ${status.index + 1}">
                                <p><c:out value="${album.title}"/></p>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>
</div>


<script>
    var imgUrl = "<c:url value='/images/'/>";
    <c:url var="searchUrl" value="/search"/>


    document.addEventListener('DOMContentLoaded', function() {
        // Variables globales para almacenar los resultados de búsqueda
        var s_artists = [];
        var s_albums = [];
        var s_songs = [];
        var s_users = [];

        // URL base para las búsquedas. Actualiza esto con tu URL real
        var searchUrl = "${searchUrl}";

        // Función para realizar búsquedas y mostrar resultados
        function searchAndDisplay(substring) {
            // Función interna para realizar llamadas AJAX
            function makeAjaxCall(endpoint, successCallback) {
                fetch(searchUrl + endpoint + "?s=" + encodeURIComponent(substring))
                    .then(response => response.json())
                    .then(data => successCallback(data))
                    .catch(error => console.error("Error al obtener datos:", error));
            }

            // Realizar llamadas AJAX para cada tipo de dato
            makeAjaxCall("/artist", data => s_artists = data);
            makeAjaxCall("/album", data => s_albums = data);
            makeAjaxCall("/song", data => s_songs = data);
            makeAjaxCall("/user", data => s_users = data);
        }

        // Manejar clics en las pestañas de búsqueda
        function handleTabClick(event) {
            document.querySelectorAll('.search-tab').forEach(tab => tab.classList.remove('active'));
            event.target.classList.add('active');
            const searchInput = document.getElementById('searchInput');
            searchInput.value = '';
            closeAllLists();
        }

        // Agregar event listeners a las pestañas de búsqueda
        document.querySelectorAll('.search-tab').forEach(tab => {
            tab.addEventListener('click', handleTabClick);
        });

        // Función principal de autocompletado
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
                var searchArray = (activeTab === 'music') ? [...s_artists, ...s_albums, ...s_songs] : s_users;
                searchArray = sortBySubstring(searchArray, val).slice(0,6);
                <c:url var="elementUrl" value="/"/>
                searchArray.forEach(function (item) {
                    if (item.name.toUpperCase().includes(val.toUpperCase())) {
                        b = document.createElement("DIV");
                        b.innerHTML = createAutocompleteItem(item);
                        b.addEventListener("click", function (e) {
                            item.url = "${elementUrl}" + item.type + "/" + item.id

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

            function removeActive(x) {
                for (var i = 0; i < x.length; i++) {
                    x[i].classList.remove("autocomplete-active");
                }
            }
        }

        function createAutocompleteItem(item) {
            return `
        <div class="autocomplete-item">
            <img src="` + imgUrl + item.imgId + `" alt="`+ item.name +`">
            <div class="autocomplete-item-info">
                <span class="autocomplete-item-name">` + item.name + `</span>
                <span class="autocomplete-item-type">` + item.type.charAt(0).toUpperCase() + item.type.slice(1) + `</span>
            </div>
        </div>
    `;
        }

        function sortBySubstring(arr, substring) {
            return arr.sort((a, b) => {
                const nameA = a.name.toLowerCase();
                const nameB = b.name.toLowerCase();
                const sub = substring.toLowerCase();

                // Si el nombre comienza con el substring, darle mayor prioridad
                const startsWithA = nameA.startsWith(sub);
                const startsWithB = nameB.startsWith(sub);

                if (startsWithA && !startsWithB) return -1; // a tiene prioridad sobre b
                if (!startsWithA && startsWithB) return 1;  // b tiene prioridad sobre a

                // Si ambos contienen el substring pero no al principio, ordenar por posición
                const indexA = nameA.indexOf(sub);
                const indexB = nameB.indexOf(sub);

                if (indexA !== -1 && indexB !== -1) {
                    return indexA - indexB;  // El que tiene la coincidencia antes tiene prioridad
                }

                // Si solo uno de ellos contiene el substring, darle prioridad
                if (indexA !== -1) return -1;
                if (indexB !== -1) return 1;

                // Si ninguno lo contiene, mantener el orden original
                return 0;
            });
        }



        // Cerrar todas las listas de autocompletado
        function closeAllLists(elmnt) {
            var x = document.getElementsByClassName("autocomplete-items");
            for (var i = 0; i < x.length; i++) {
                if (elmnt != x[i] && elmnt != document.getElementById('searchInput')) {
                    x[i].parentNode.removeChild(x[i]);
                }
            }
        }

        function debounce(func, delay) {
            let debounceTimer;
            return function() {
                const context = this;
                const args = arguments;
                clearTimeout(debounceTimer);
                debounceTimer = setTimeout(() => func.apply(context, args), delay);
            };
        }

        // Event listener para cerrar listas al hacer clic fuera
        document.addEventListener("click", function (e) {
            closeAllLists(e.target);
        });

        // Inicializar autocompletado
        autocomplete(document.getElementById("searchInput"));

        // Agregar event listener para la búsqueda
        document.getElementById('searchInput').addEventListener('input', debounce(function() {
            var substring = this.value;
            searchAndDisplay(substring);
        }, 50));

    });
</script>
</body>
</html>