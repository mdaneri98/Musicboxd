<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <spring:message var="pageTitle" code="page.title.search"/>
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
    <h1><spring:message code="webpage.name"/></h1>
    <div class="search-tabs">
        <span class="search-tab active" data-type="music"><spring:message code="search.tab.music"/></span>
        <span class="search-tab" data-type="users"><spring:message code="search.tab.users"/></span>
    </div>
    <div class="search-wrapper">
        <input type="text" class="search-input" id="searchInput" placeholder="<spring:message code="search.placeholder"/>">
        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
        </svg>
        <div id="autocompleteList" class="autocomplete-items"></div>
    </div>
</div>

<script>
    var imgUrl = "<c:url value='/images/'/>";
    <c:url var="searchUrl" value="/search"/>

    let isErrorMessageShown = false;

    document.addEventListener('DOMContentLoaded', function() {
        var s_artists = [];
        var s_albums = [];
        var s_songs = [];
        var s_users = [];

        var searchUrl = "${searchUrl}";
        var currentFocus = -1;

        function searchAndDisplay(substring) {
            Promise.all([
                makeAjaxCall("/artist", substring),
                makeAjaxCall("/album", substring),
                makeAjaxCall("/song", substring),
                makeAjaxCall("/user", substring)
            ]).then(([artists, albums, songs, users]) => {
                s_artists = artists;
                s_albums = albums;
                s_songs = songs;
                s_users = users;
                updateAutocompleteResults(substring);
            }).catch(error => {
                console.error("Error al obtener datos:", error);
                showErrorMessage("Error fetching results");
            });
        }

        function makeAjaxCall(endpoint, substring) {
            return fetch(searchUrl + endpoint + "?s=" + encodeURIComponent(substring))
                .then(response => response.json())
                .catch(error => {
                    console.error("Error en llamada AJAX:", error);
                    return [];
                });
        }

        function handleTabClick(event) {
            document.querySelectorAll('.search-tab').forEach(tab => tab.classList.remove('active'));
            event.target.classList.add('active');
            closeAllLists();
        }

        document.querySelectorAll('.search-tab').forEach(tab => {
            tab.addEventListener('click', handleTabClick);
        });

        function autocomplete(inp) {
            inp.addEventListener("input", function(e) {
                if (e.keyCode != 40 && e.keyCode != 38 && e.keyCode != 13) {
                    var val = this.value;
                    if (!val) {
                        closeAllLists();
                        return false;
                    }
                    searchAndDisplay(val);
                }
            });

            inp.addEventListener("keydown", function(e) {
                var x = document.getElementById(this.id + "autocomplete-list");
                if (x) x = x.getElementsByClassName("autocomplete-item");
                if (e.keyCode == 40) {
                    currentFocus++;
                    addActive(x);
                } else if (e.keyCode == 38) {
                    currentFocus--;
                    addActive(x);
                } else if (e.keyCode == 13) {
                    e.preventDefault();
                    if (x && x.length > 0) {
                        if (currentFocus > -1) {
                            x[currentFocus].click();
                        } else {
                            x[0].click();
                        }
                    } else {
                        showErrorMessage("No se encontraron resultados");
                    }
                }
            });
        }

        function updateAutocompleteResults(val) {
            closeAllLists();
            var a = document.createElement("DIV");
            a.setAttribute("id", "searchInputautocomplete-list");
            a.setAttribute("class", "autocomplete-items");
            document.getElementById('searchInput').parentNode.appendChild(a);

            var activeTab = document.querySelector('.search-tab.active').dataset.type;
            var searchArray = (activeTab === 'music') ? [...s_artists, ...s_albums, ...s_songs] : s_users;
            searchArray = sortBySubstring(searchArray, val);

            if (searchArray.length === 0) {
                showErrorMessage("No se encontraron resultados");
                return;
            }

            <c:url var="elementUrl" value="/"/>
            searchArray.slice(0, 7).forEach(function (item) {
                var b = document.createElement("DIV");
                b.className = "autocomplete-item";
                b.innerHTML = createAutocompleteItem(item);
                b.addEventListener("click", function (e) {
                    item.url = "${elementUrl}" + item.type + "/" + item.id;
                    document.getElementById('searchInput').value = item.name;
                    window.location.href = item.url;
                });
                a.appendChild(b);
            });
        }

        function showErrorMessage(message) {
            closeAllLists();
            var errorDiv = document.createElement("DIV");
            errorDiv.setAttribute("class", "autocomplete-error");
            errorDiv.textContent = message;

            var searchWrapper = document.querySelector('.search-wrapper');
            searchWrapper.appendChild(errorDiv);

            isErrorMessageShown = true;
        }

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

        function createAutocompleteItem(item) {
            return `
                <img src="` + imgUrl + item.imgId + `" alt="`+ item.name +`">
                <div class="autocomplete-item-info">
                    <span class="autocomplete-item-name">` + item.name + `</span>
                    <span class="autocomplete-item-type">` + item.type.charAt(0).toUpperCase() + item.type.slice(1) + `</span>
                </div>
            `;
        }

        function sortBySubstring(arr, substring) {
            const sub = substring.toLowerCase();
            return arr.filter(item => item.name.toLowerCase().includes(sub))
                      .sort((a, b) => {
                          const nameA = a.name.toLowerCase();
                          const nameB = b.name.toLowerCase();

                          const startsWithA = nameA.startsWith(sub);
                          const startsWithB = nameB.startsWith(sub);

                          if (startsWithA && !startsWithB) return -1;
                          if (!startsWithA && startsWithB) return 1;

                          const indexA = nameA.indexOf(sub);
                          const indexB = nameB.indexOf(sub);

                          return indexA - indexB;
                      });
        }

        function closeAllLists(elmnt) {
            var x = document.getElementsByClassName("autocomplete-items");
            for (var i = 0; i < x.length; i++) {
                if (elmnt != x[i] && elmnt != document.getElementById('searchInput')) {
                    x[i].parentNode.removeChild(x[i]);
                }
            }
            var errorMsg = document.querySelector('.autocomplete-error');
            if (errorMsg) {
                errorMsg.remove();
                isErrorMessageShown = false;
            }
        }

        document.addEventListener("click", function (e) {
            closeAllLists(e.target);
        });

        autocomplete(document.getElementById("searchInput"));
    });
</script>
</body>
</html>