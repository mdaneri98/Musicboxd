<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <spring:message var="pageTitle" code="page.title.moderator"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${pageTitle}"/>
    </jsp:include>
</head>
<body>
    <div class="main-container">
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp">
            <jsp:param name="loggedUserImgId" value="${loggedUser.image.id}"/>
            <jsp:param name="moderator" value="${loggedUser.moderator}"/>
        </jsp:include>

        <main class="content-wrapper">
            <div class="search-container">
                <h1 class="search-title">
                    <spring:message code="label.moderator" />
                </h1>

                <div class="tabs">
                    <span class="tab active" data-type="artists"><spring:message code="label.artist"/></span>
                    <span class="tab" data-type="albums"><spring:message code="label.album"/></span>
                    <span class="tab" data-type="songs"><spring:message code="label.song"/></span>
                </div>

                <div class="search-wrapper" style="display: none;">
                    <input type="text"
                           class="form-control search-input"
                           id="searchInput"
                           placeholder="<spring:message code="search.placeholder"/>"
                           autocomplete="off">
                    <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                        <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
                    </svg>
                    <div id="autocompleteList" class="search-results"></div>
                </div>

                <div class="mod-actions">
                    <button id="redirectButton" onclick="redirect()" class="btn btn-primary">
                        <span class="artist-text">
                            <spring:message code="button.mod.artist" />
                        </span>
                        <span class="album-text" style="display: none;">
                            <spring:message code="button.mod.album" />
                        </span>
                        <span class="song-text" style="display: none;">
                            <spring:message code="button.mod.song" />
                        </span>
                    </button>
                </div>
            </div>
        </main>
    </div>

    <c:url var="addArtistUrl" value="/mod/add/artist/"/>
    <c:url var="addAlbumUrlBase" value="/mod/add/artist/"/>
    <c:url var="addSongUrlBase" value="/mod/add/album/"/>
    <c:url var="imageUrl" value="/images/"/>

    <script>
        var selected_item;
        var imgUrl = "${imageUrl}";
        <c:url var="searchUrl" value="/search"/>

        let isErrorMessageShown = false;

    function redirect() {
        var addArtistUrl = "${addArtistUrl}";
        var addAlbumUrlBase = "${addAlbumUrlBase}";
        var addSongUrlBase = "${addSongUrlBase}";

        const activeTab = document.querySelector('.tab.active').dataset.type;

        switch(activeTab) {
            case 'artists':
                window.location.href = addArtistUrl;
                break;
            case 'albums':
                if (selected_item) {
                    window.location.href = addAlbumUrlBase + selected_item.id + "/album";
                }
                break;
            case 'songs':
                if (selected_item) {
                    window.location.href = addSongUrlBase + selected_item.id + "/song";
                }
                break;
        }
    }

        document.addEventListener('DOMContentLoaded', function() {
            var s_artists = [];
            var s_albums = [];

            var searchUrl = "${searchUrl}";
            var currentFocus = -1;

            function searchAndDisplay(substring) {
                Promise.all([
                    makeAjaxCall("/artist", substring),
                    makeAjaxCall("/album", substring),
                ]).then(([artists, albums]) => {
                    s_artists = artists;
                    s_albums = albums;
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
                document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
                event.target.classList.add('active');
                
                const activeTab = event.target.dataset.type;
                
                const searchWrapper = document.querySelector('.search-wrapper');
                searchWrapper.style.display = activeTab === 'artists' ? 'none' : 'block';

                document.querySelector('.artist-text').style.display = activeTab === 'artists' ? 'block' : 'none';
                document.querySelector('.album-text').style.display = activeTab === 'albums' ? 'block' : 'none';
                document.querySelector('.song-text').style.display = activeTab === 'songs' ? 'block' : 'none';
                
                closeAllLists();
                updateSearchInputVisibility();
            }

            document.querySelectorAll('.tab').forEach(tab => {
                tab.addEventListener('click', handleTabClick);
            });

            function updateSearchInputVisibility() {
                const activeTab = document.querySelector('.tab.active').dataset.type;
                const searchInput = document.getElementById('searchInput');
                const searchIcon = document.querySelector('.search-icon');
                
                if (activeTab === 'artists') {
                    searchInput.style.display = 'none';
                    searchIcon.style.display = 'none';
                } else {
                    searchInput.style.display = 'block';
                    searchIcon.style.display = 'block';
                    searchInput.placeholder = activeTab === 'albums' ? '<spring:message code="label.search.artist"/>' : '<spring:message code="label.search.album"/>';
                }
            }

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
                    if (x) x = x.getElementsByClassName("search-result-item");
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
                a.setAttribute("class", "search-results-list");
                document.getElementById('searchInput').parentNode.appendChild(a);

                var activeTab = document.querySelector('.tab.active').dataset.type;
                var searchArray;
                if (activeTab === 'albums') {
                    searchArray = s_artists;
                } else if (activeTab === 'songs') {
                    searchArray = s_albums;
                } else {
                    searchArray = [];
                }
                searchArray = sortBySubstring(searchArray, val);

                if (searchArray.length === 0) {
                    showErrorMessage("No se encontraron resultados");
                    return;
                }

                <c:url var="elementUrl" value="/"/>
                searchArray.slice(0, 7).forEach(function (item) {
                    var b = document.createElement("DIV");
                    b.className = "search-result-item";
                    b.innerHTML = createAutocompleteItem(item);
                    b.addEventListener("click", function (e) {
                        item.url = "${elementUrl}" + item.type + "/" + item.id;
                        document.getElementById('searchInput').value = item.name;
                        selected_item = item
                        closeAllLists();
                    });
                    a.appendChild(b);
                });
            }

            function showErrorMessage(message) {
                closeAllLists();
                var errorDiv = document.createElement("DIV");
                errorDiv.setAttribute("class", "search-error");
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
                x[currentFocus].classList.add("search-result-active");
            }

            function removeActive(x) {
                for (var i = 0; i < x.length; i++) {
                    x[i].classList.remove("search-result-active");
                }
            }

            function createAutocompleteItem(item) {
                return `
                    <div class="search-result-content" data-type="${item.type}">
                        <img src="` + imgUrl + item.imgId + `" alt="`+ item.name +`" class="search-result-image">
                        <div class="search-result-info">
                            <span class="search-result-name">` + item.name + `</span>
                            <span class="search-result-type">` + item.type.charAt(0).toUpperCase() + item.type.slice(1) + `</span>
                        </div>
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
                var x = document.getElementsByClassName("search-results-list");
                for (var i = 0; i < x.length; i++) {
                    if (elmnt != x[i] && elmnt != document.getElementById('searchInput')) {
                        x[i].parentNode.removeChild(x[i]);
                    }
                }
                var errorMsg = document.querySelector('.search-error');
                if (errorMsg) {
                    errorMsg.remove();
                    isErrorMessageShown = false;
                }
            }

            document.addEventListener("click", function (e) {
                closeAllLists(e.target);
            });

            autocomplete(document.getElementById("searchInput"));
            updateSearchInputVisibility();
        });
    </script>
</body>
</html>