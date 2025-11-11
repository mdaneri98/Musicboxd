<TASKS name="Musicboxd_Frontend_Migration">
  <!-- 
    Este documento lista todas las tareas necesarias para migrar el frontend de Musicboxd
    desde JSP/Spring MVC a Next.js + React + TypeScript.
    
    Estado: [PENDING] | [IN_PROGRESS] | [COMPLETED] | [BLOCKED]
    Prioridad: [CRITICAL] | [HIGH] | [MEDIUM] | [LOW]
  -->

  <Task id="1" title="Project Setup & Infrastructure" priority="CRITICAL" status="COMPLETED">
    <Description>Configurar el proyecto base Next.js con TypeScript y todas las herramientas necesarias</Description>
    <Subtask status="COMPLETED">Initialize Next.js project with TypeScript template (npx create-next-app@latest)</Subtask>
    <Subtask status="COMPLETED">Install core dependencies: react, react-dom, next, typescript</Subtask>
    <Subtask status="COMPLETED">Install state management: @reduxjs/toolkit, react-redux</Subtask>
    <Subtask status="COMPLETED">Install forms: react-hook-form, @hookform/resolvers, yup/zod</Subtask>
    <Subtask status="COMPLETED">Install HTTP client: axios</Subtask>
    <Subtask status="COMPLETED">Install testing: jest, @testing-library/react, @testing-library/jest-dom, nock, sinon</Subtask>
    <Subtask status="COMPLETED">Configure ESLint with Next.js rules</Subtask>
    <Subtask status="COMPLETED">Configure Prettier (use existing .prettierrc if available)</Subtask>
    <Subtask status="COMPLETED">Set up Jest configuration (jest.config.js, setup files)</Subtask>
    <Subtask status="COMPLETED">Create tsconfig.json with strict mode and path aliases</Subtask>
    <Subtask status="COMPLETED">Set up .env.local with API_BASE_URL and other environment variables</Subtask>
    <Subtask status="COMPLETED">Create .gitignore for Next.js (node_modules, .next, out, .env*.local)</Subtask>
    <Deliverables>
      - package.json with all dependencies ✓
      - tsconfig.json configured ✓
      - ESLint + Prettier configured ✓
      - Jest configured and working ✓
      - .env.local template ✓
      - README.md with setup instructions ✓
    </Deliverables>
  </Task>

  <Task id="2" title="Maven Integration" priority="CRITICAL" status="COMPLETED">
    <Description>Integrar el build del frontend con Maven usando frontend-maven-plugin</Description>
    <Subtask status="COMPLETED">Create new Maven module "frontend" in project root</Subtask>
    <Subtask status="COMPLETED">Add frontend-maven-plugin to frontend/pom.xml</Subtask>
    <Subtask status="COMPLETED">Configure plugin to install node/npm (specific versions)</Subtask>
    <Subtask status="COMPLETED">Configure plugin to run "npm install"</Subtask>
    <Subtask status="COMPLETED">Configure plugin to run "npm run build"</Subtask>
    <Subtask status="PENDING">Configure plugin to copy build output to webapp/src/main/resources/static (NOTE: Will be done when webapp module is enabled)</Subtask>
    <Subtask status="PENDING">Add frontend module dependency to webapp module (NOTE: Will be done when webapp module is enabled)</Subtask>
    <Subtask status="COMPLETED">Test full build with "mvn clean package"</Subtask>
    <Subtask status="PENDING">Configure Spring to serve static resources from correct path (NOTE: Will be done when webapp module is enabled)</Subtask>
    <Subtask status="COMPLETED">Document build process in README</Subtask>
    <Deliverables>
      - frontend/pom.xml with frontend-maven-plugin configured ✓
      - Root pom.xml updated with frontend module ✓
      - Spring configuration updated to serve static assets (PENDING until webapp enabled)
      - Successful "mvn clean install" tested ✓
    </Deliverables>
  </Task>

  <Task id="3" title="Folder Structure & Architecture" priority="HIGH" status="PENDING">
    <Description>Crear la estructura de carpetas siguiendo best practices de Next.js + TypeScript</Description>
    <Subtask status="PENDING">Create /pages directory (Next.js pages)</Subtask>
    <Subtask status="PENDING">Create /components directory (reusable components)</Subtask>
    <Subtask status="PENDING">Create /components/layout (Head, Sidebar, Footer)</Subtask>
    <Subtask status="PENDING">Create /components/cards (UserCard, ArtistCard, AlbumCard, etc.)</Subtask>
    <Subtask status="PENDING">Create /components/forms (form components)</Subtask>
    <Subtask status="PENDING">Create /components/ui (buttons, inputs, pagination, etc.)</Subtask>
    <Subtask status="PENDING">Create /store directory (Redux slices)</Subtask>
    <Subtask status="PENDING">Create /repositories directory (API repositories)</Subtask>
    <Subtask status="PENDING">Create /types directory (TypeScript interfaces/types)</Subtask>
    <Subtask status="PENDING">Create /utils directory (helpers, constants, mappers)</Subtask>
    <Subtask status="PENDING">Create /lib directory (ApiClient, auth utils)</Subtask>
    <Subtask status="PENDING">Create /hooks directory (custom React hooks)</Subtask>
    <Subtask status="PENDING">Create /styles directory (will import existing CSS)</Subtask>
    <Subtask status="PENDING">Create /public directory (static assets, images)</Subtask>
    <Subtask status="PENDING">Document folder structure in README</Subtask>
    <Deliverables>
      - Complete folder structure
      - Index files with exports where needed
      - README with folder structure documentation
    </Deliverables>
  </Task>

  <Task id="4" title="Global Styles Integration" priority="HIGH" status="PENDING">
    <Description>Importar y configurar los CSS existentes (base.css, components.css, layout.css, modules.css)</Description>
    <Subtask status="PENDING">Copy existing CSS files to /styles directory</Subtask>
    <Subtask status="PENDING">Import base.css in _app.tsx</Subtask>
    <Subtask status="PENDING">Import layout.css in _app.tsx</Subtask>
    <Subtask status="PENDING">Import components.css in _app.tsx</Subtask>
    <Subtask status="PENDING">Import modules.css in _app.tsx</Subtask>
    <Subtask status="PENDING">Verify CSS classes are applied correctly</Subtask>
    <Subtask status="PENDING">Test responsive behavior</Subtask>
    <Subtask status="PENDING">Document CSS usage and class naming conventions</Subtask>
    <Deliverables>
      - All existing CSS imported and working
      - _app.tsx configured with global styles
      - CSS documentation
    </Deliverables>
  </Task>

  <Task id="5" title="TypeScript Types & Models" priority="HIGH" status="PENDING">
    <Description>Definir todas las interfaces TypeScript para los modelos de dominio y respuestas de la API</Description>
    <Subtask status="PENDING">Create /types/models.ts with User interface (8 fields)</Subtask>
    <Subtask status="PENDING">Create Artist interface (9 fields)</Subtask>
    <Subtask status="PENDING">Create Album interface (11 fields)</Subtask>
    <Subtask status="PENDING">Create Song interface (10 fields)</Subtask>
    <Subtask status="PENDING">Create Review interface (14 fields)</Subtask>
    <Subtask status="PENDING">Create Comment interface (7 fields)</Subtask>
    <Subtask status="PENDING">Create Notification interface (7 fields)</Subtask>
    <Subtask status="PENDING">Create Image interface (2 fields)</Subtask>
    <Subtask status="PENDING">Create /types/api.ts with HALResource<T> interface</Subtask>
    <Subtask status="PENDING">Create Collection<T> interface with pagination</Subtask>
    <Subtask status="PENDING">Create HALLink interface</Subtask>
    <Subtask status="PENDING">Create /types/enums.ts with FilterType enum (popular, recent, oldest, alphabetical)</Subtask>
    <Subtask status="PENDING">Create ItemType type union ('artist' | 'album' | 'song')</Subtask>
    <Subtask status="PENDING">Create NotificationType type union ('follow' | 'like' | 'comment' | 'review')</Subtask>
    <Subtask status="PENDING">Create /types/forms.ts with form data types for all 14 forms</Subtask>
    <Subtask status="PENDING">Document type system and usage patterns</Subtask>
    <Deliverables>
      - Complete type definitions for all domain models
      - HAL/HATEOAS types
      - Enum types
      - Form types
      - Type documentation
    </Deliverables>
  </Task>

  <Task id="6" title="API Client & HATEOAS Support" priority="CRITICAL" status="COMPLETED">
    <Description>Implementar el cliente HTTP con soporte HATEOAS, autenticación JWT y refresh token</Description>
    <Subtask status="COMPLETED">Create /lib/apiClient.ts with axios instance</Subtask>
    <Subtask status="COMPLETED">Implement Authorization header injection from localStorage</Subtask>
    <Subtask status="COMPLETED">Implement 401 interceptor with refresh token logic</Subtask>
    <Subtask status="COMPLETED">Implement single-flight refresh to prevent concurrent refreshes</Subtask>
    <Subtask status="COMPLETED">Implement HAL response parsing (extract data, _links, _embedded)</Subtask>
    <Subtask status="COMPLETED">Create generic request methods: get<T>, post<T>, put<T>, patch<T>, delete<T></Subtask>
    <Subtask status="COMPLETED">Implement error handling and error transformation</Subtask>
    <Subtask status="COMPLETED">Add request/response logging (dev mode only)</Subtask>
    <Subtask status="COMPLETED">Create /utils/halHelpers.ts for HATEOAS link navigation</Subtask>
    <Subtask status="COMPLETED">Implement getLink(resource, rel) helper</Subtask>
    <Subtask status="COMPLETED">Implement followLink(resource, rel) helper</Subtask>
    <Subtask status="PENDING">Write unit tests for ApiClient</Subtask>
    <Subtask status="PENDING">Write unit tests for refresh token flow</Subtask>
    <Subtask status="PENDING">Document ApiClient usage</Subtask>
    <Deliverables>
      - ApiClient with JWT and refresh token support ✓
      - HATEOAS parsing and navigation helpers ✓
      - Error handling ✓
      - Unit tests (100% coverage) (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="7" title="Repository Layer - Auth" priority="CRITICAL" status="COMPLETED">
    <Description>Implementar AuthRepository con los 5 endpoints de autenticación</Description>
    <Subtask status="COMPLETED">Create /repositories/AuthRepository.ts</Subtask>
    <Subtask status="COMPLETED">Implement login(email, password): Promise<LoginResponse></Subtask>
    <Subtask status="COMPLETED">Implement register(userData): Promise<User></Subtask>
    <Subtask status="COMPLETED">Implement refresh(refreshToken): Promise<LoginResponse></Subtask>
    <Subtask status="COMPLETED">Implement logout(refreshToken): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement getCurrentUser(): Promise<User></Subtask>
    <Subtask status="PENDING">Handle password reset endpoints (if available in API)</Subtask>
    <Subtask status="PENDING">Write unit tests mocking API responses</Subtask>
    <Subtask status="PENDING">Document AuthRepository methods</Subtask>
    <Deliverables>
      - AuthRepository with 5 methods ✓
      - Unit tests (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="8" title="Repository Layer - Users" priority="HIGH" status="COMPLETED">
    <Description>Implementar UserRepository con los 13 endpoints de usuarios</Description>
    <Subtask status="COMPLETED">Create /repositories/UserRepository.ts</Subtask>
    <Subtask status="COMPLETED">Implement getUsers(page, size, search?, filter?): Promise<Collection<User>></Subtask>
    <Subtask status="COMPLETED">Implement getUserById(id): Promise<User></Subtask>
    <Subtask status="COMPLETED">Implement createUser(userData): Promise<User></Subtask>
    <Subtask status="COMPLETED">Implement updateUser(id, userData): Promise<User></Subtask>
    <Subtask status="COMPLETED">Implement deleteUser(id): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement getUserReviews(id, page, size, filter?): Promise<Collection<Review>></Subtask>
    <Subtask status="COMPLETED">Implement getFollowers(id, page, size): Promise<Collection<User>></Subtask>
    <Subtask status="COMPLETED">Implement getFollowing(id, page, size): Promise<Collection<User>></Subtask>
    <Subtask status="COMPLETED">Implement followUser(id): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement unfollowUser(id): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement getFavoriteArtists(id): Promise<Artist[]></Subtask>
    <Subtask status="COMPLETED">Implement getFavoriteAlbums(id): Promise<Album[]></Subtask>
    <Subtask status="COMPLETED">Implement getFavoriteSongs(id): Promise<Song[]></Subtask>
    <Subtask status="PENDING">Write unit tests for all methods</Subtask>
    <Subtask status="PENDING">Document UserRepository</Subtask>
    <Deliverables>
      - UserRepository with 13 methods ✓
      - Unit tests (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="9" title="Repository Layer - Music (Artists, Albums, Songs)" priority="HIGH" status="COMPLETED">
    <Description>Implementar repositorios para Artist (10 métodos), Album (10 métodos), Song (8 métodos)</Description>
    <Subtask status="COMPLETED">Create /repositories/ArtistRepository.ts with 10 methods</Subtask>
    <Subtask status="COMPLETED">Implement getArtists, getArtistById, createArtist, updateArtist, deleteArtist</Subtask>
    <Subtask status="COMPLETED">Implement getArtistReviews, createArtistReview</Subtask>
    <Subtask status="COMPLETED">Implement getArtistAlbums, createArtistAlbum</Subtask>
    <Subtask status="COMPLETED">Implement getArtistSongs, addArtistFavorite, removeArtistFavorite</Subtask>
    <Subtask status="COMPLETED">Create /repositories/AlbumRepository.ts with 10 methods</Subtask>
    <Subtask status="COMPLETED">Implement getAlbums, getAlbumById, createAlbum, updateAlbum, deleteAlbum</Subtask>
    <Subtask status="COMPLETED">Implement getAlbumReviews, createAlbumReview</Subtask>
    <Subtask status="COMPLETED">Implement getAlbumSongs, createAlbumSong</Subtask>
    <Subtask status="COMPLETED">Implement addAlbumFavorite, removeAlbumFavorite</Subtask>
    <Subtask status="COMPLETED">Create /repositories/SongRepository.ts with 8 methods</Subtask>
    <Subtask status="COMPLETED">Implement getSongs, getSongById, createSong, updateSong, deleteSong</Subtask>
    <Subtask status="COMPLETED">Implement getSongReviews, createSongReview</Subtask>
    <Subtask status="COMPLETED">Implement addSongFavorite, removeSongFavorite</Subtask>
    <Subtask status="PENDING">Write unit tests for all three repositories</Subtask>
    <Subtask status="PENDING">Document all three repositories</Subtask>
    <Deliverables>
      - ArtistRepository (10 methods) ✓
      - AlbumRepository (10 methods) ✓
      - SongRepository (8 methods) ✓
      - Unit tests for all (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="10" title="Repository Layer - Reviews & Comments" priority="HIGH" status="COMPLETED">
    <Description>Implementar ReviewRepository (8 métodos) y CommentRepository (5 métodos)</Description>
    <Subtask status="COMPLETED">Create /repositories/ReviewRepository.ts</Subtask>
    <Subtask status="COMPLETED">Implement getReviews(page, size, search?, filter?): Promise<Collection<Review>></Subtask>
    <Subtask status="COMPLETED">Implement getReviewById(id): Promise<Review></Subtask>
    <Subtask status="COMPLETED">Implement createReview(reviewData): Promise<Review></Subtask>
    <Subtask status="COMPLETED">Implement updateReview(id, reviewData): Promise<Review></Subtask>
    <Subtask status="COMPLETED">Implement deleteReview(id): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement getReviewLikes(reviewId, page, size): Promise<Collection<User>></Subtask>
    <Subtask status="COMPLETED">Implement likeReview(reviewId): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement unlikeReview(reviewId): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement blockReview(reviewId): Promise<void> (moderator)</Subtask>
    <Subtask status="COMPLETED">Implement unblockReview(reviewId): Promise<void> (moderator)</Subtask>
    <Subtask status="COMPLETED">Create /repositories/CommentRepository.ts</Subtask>
    <Subtask status="COMPLETED">Implement getComments(page, size, search?, filter?): Promise<Collection<Comment>></Subtask>
    <Subtask status="COMPLETED">Implement getCommentById(id): Promise<Comment></Subtask>
    <Subtask status="COMPLETED">Implement createComment(commentData): Promise<Comment></Subtask>
    <Subtask status="COMPLETED">Implement updateComment(id, commentData): Promise<Comment></Subtask>
    <Subtask status="COMPLETED">Implement deleteComment(id): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement getReviewComments(reviewId, page, size) in ReviewRepository</Subtask>
    <Subtask status="PENDING">Write unit tests for both repositories</Subtask>
    <Subtask status="PENDING">Document both repositories</Subtask>
    <Deliverables>
      - ReviewRepository (10 methods) ✓
      - CommentRepository (5 methods) ✓
      - Unit tests (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="11" title="Repository Layer - Notifications & Images" priority="MEDIUM" status="COMPLETED">
    <Description>Implementar NotificationRepository (7 métodos) y ImageRepository (1 método)</Description>
    <Subtask status="COMPLETED">Create /repositories/NotificationRepository.ts</Subtask>
    <Subtask status="COMPLETED">Implement getNotifications(page, size): Promise<Collection<Notification>></Subtask>
    <Subtask status="COMPLETED">Implement getNotificationById(id): Promise<Notification></Subtask>
    <Subtask status="COMPLETED">Implement createNotification(data): Promise<Notification></Subtask>
    <Subtask status="COMPLETED">Implement updateNotification(id, data): Promise<Notification></Subtask>
    <Subtask status="COMPLETED">Implement deleteNotification(id): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement markAsRead(id): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement markAllAsRead(): Promise<void></Subtask>
    <Subtask status="COMPLETED">Implement getUnreadCount(): Promise<number></Subtask>
    <Subtask status="COMPLETED">Create /repositories/ImageRepository.ts</Subtask>
    <Subtask status="COMPLETED">Implement getImageUrl(id): string (URL constructor)</Subtask>
    <Subtask status="COMPLETED">Implement uploadImage(file): Promise<Image> (if upload endpoint exists)</Subtask>
    <Subtask status="PENDING">Write unit tests for both repositories</Subtask>
    <Subtask status="PENDING">Document both repositories</Subtask>
    <Deliverables>
      - NotificationRepository (8 methods) ✓
      - ImageRepository (3 methods) ✓
      - Unit tests (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="12" title="Redux Store - Setup & Configuration" priority="CRITICAL" status="COMPLETED">
    <Description>Configurar Redux Toolkit con el store principal y middleware</Description>
    <Subtask status="COMPLETED">Create /store/index.ts with configureStore</Subtask>
    <Subtask status="COMPLETED">Set up Redux DevTools integration</Subtask>
    <Subtask status="COMPLETED">Configure middleware (thunk, logger for dev)</Subtask>
    <Subtask status="COMPLETED">Create /store/hooks.ts with typed useDispatch and useSelector</Subtask>
    <Subtask status="COMPLETED">Set up store provider in _app.tsx</Subtask>
    <Subtask status="COMPLETED">Create /store/types.ts with RootState and AppDispatch types</Subtask>
    <Subtask status="PENDING">Document store setup and usage</Subtask>
    <Deliverables>
      - Redux store configured ✓
      - Typed hooks ✓
      - Provider in _app.tsx ✓
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="13" title="Redux Store - AuthStore" priority="CRITICAL" status="COMPLETED">
    <Description>Implementar AuthStore (slice) con state, actions y selectors para autenticación</Description>
    <Subtask status="COMPLETED">Create /store/slices/authSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define state: currentUser, accessToken, refreshToken, isAuthenticated, isModerator, loading, error</Subtask>
    <Subtask status="COMPLETED">Implement loginAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement registerAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement logoutAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement refreshTokenAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement getCurrentUserAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement checkAuthAsync thunk (on app init)</Subtask>
    <Subtask status="COMPLETED">Create reducers for all actions</Subtask>
    <Subtask status="COMPLETED">Create selectors: selectCurrentUser, selectIsAuthenticated, selectIsModerator, etc.</Subtask>
    <Subtask status="COMPLETED">Implement localStorage sync (save/load tokens)</Subtask>
    <Subtask status="PENDING">Write unit tests for reducers and thunks</Subtask>
    <Subtask status="PENDING">Document AuthStore usage</Subtask>
    <Deliverables>
      - AuthStore slice with 6 thunks ✓
      - Selectors (9 total) ✓
      - localStorage sync (via authRepository) ✓
      - Unit tests (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="14" title="Redux Store - UserStore" priority="HIGH" status="COMPLETED">
    <Description>Implementar UserStore con state y actions para usuarios, seguidores y favoritos</Description>
    <Subtask status="COMPLETED">Create /store/slices/userSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define state: users (by ID map), currentProfile, followers, following, favorites, loading, error</Subtask>
    <Subtask status="COMPLETED">Implement fetchUsersAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement fetchUserByIdAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement updateUserAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement deleteUserAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement fetchFollowersAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement fetchFollowingAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement followUserAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement unfollowUserAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement fetchFavoriteArtistsAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement fetchFavoriteAlbumsAsync thunk</Subtask>
    <Subtask status="COMPLETED">Implement fetchFavoriteSongsAsync thunk</Subtask>
    <Subtask status="COMPLETED">Create reducers for all actions</Subtask>
    <Subtask status="COMPLETED">Create selectors: selectUserById, selectCurrentProfile, selectFollowers, etc.</Subtask>
    <Subtask status="PENDING">Write unit tests for reducers and thunks</Subtask>
    <Subtask status="PENDING">Document UserStore usage</Subtask>
    <Deliverables>
      - UserStore slice with 12 thunks ✓
      - Selectors (17 total) ✓
      - Normalized state ✓
      - Unit tests (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="15" title="Redux Store - Music Stores (Artist, Album, Song)" priority="HIGH" status="COMPLETED">
    <Description>Implementar stores para Artist, Album y Song con sus respectivas acciones</Description>
    <Subtask status="COMPLETED">Create /store/slices/artistSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define ArtistStore state: artists, artistList, albums, songs, reviews, loading, error</Subtask>
    <Subtask status="COMPLETED">Implement artist CRUD thunks (fetch, create, update, delete, search)</Subtask>
    <Subtask status="COMPLETED">Implement fetchArtistAlbumsAsync, fetchArtistSongsAsync, fetchArtistReviewsAsync</Subtask>
    <Subtask status="COMPLETED">Create selectors for ArtistStore</Subtask>
    <Subtask status="COMPLETED">Create /store/slices/albumSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define AlbumStore state: albums, albumList, songs, reviews, loading, error</Subtask>
    <Subtask status="COMPLETED">Implement album CRUD thunks (fetch, create, update, delete, search)</Subtask>
    <Subtask status="COMPLETED">Implement fetchAlbumSongsAsync, fetchAlbumReviewsAsync</Subtask>
    <Subtask status="COMPLETED">Create selectors for AlbumStore</Subtask>
    <Subtask status="COMPLETED">Create /store/slices/songSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define SongStore state: songs, songList, reviews, loading, error</Subtask>
    <Subtask status="COMPLETED">Implement song CRUD thunks (fetch, create, update, delete, search)</Subtask>
    <Subtask status="COMPLETED">Implement fetchSongReviewsAsync</Subtask>
    <Subtask status="COMPLETED">Create selectors for SongStore</Subtask>
    <Subtask status="PENDING">Write unit tests for all three stores</Subtask>
    <Subtask status="PENDING">Document all three stores</Subtask>
    <Deliverables>
      - ArtistStore (10 thunks, 14 selectors) ✓
      - AlbumStore (9 thunks, 11 selectors) ✓
      - SongStore (8 thunks, 9 selectors) ✓
      - All CRUD and related thunks ✓
      - Normalized state for all three ✓
      - Unit tests (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="16" title="Redux Store - Review & Comment Stores" priority="HIGH" status="COMPLETED">
    <Description>Implementar ReviewStore y CommentStore con acciones para likes y comentarios</Description>
    <Subtask status="COMPLETED">Create /store/slices/reviewSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define state: reviews, reviewList, comments, likes, loading, error</Subtask>
    <Subtask status="COMPLETED">Implement review CRUD thunks</Subtask>
    <Subtask status="COMPLETED">Implement likeReviewAsync, unlikeReviewAsync</Subtask>
    <Subtask status="COMPLETED">Implement fetchReviewCommentsAsync</Subtask>
    <Subtask status="COMPLETED">Implement fetchReviewLikesAsync</Subtask>
    <Subtask status="COMPLETED">Implement blockReviewAsync, unblockReviewAsync (moderator)</Subtask>
    <Subtask status="COMPLETED">Create selectors for ReviewStore</Subtask>
    <Subtask status="COMPLETED">Create /store/slices/commentSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define state: comments, commentList, loading, error</Subtask>
    <Subtask status="COMPLETED">Implement comment CRUD thunks</Subtask>
    <Subtask status="COMPLETED">Create selectors for CommentStore</Subtask>
    <Subtask status="PENDING">Write unit tests for both stores (PENDING - will be done in testing phase)</Subtask>
    <Subtask status="PENDING">Document both stores (PENDING - will be done in documentation phase)</Subtask>
    <Deliverables>
      - ReviewStore with likes and comments support ✓
      - CommentStore ✓
      - Unit tests (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="17" title="Redux Store - Notification, Search & UI Stores" priority="MEDIUM" status="COMPLETED">
    <Description>Implementar stores auxiliares para notificaciones, búsqueda y estado de UI</Description>
    <Subtask status="COMPLETED">Create /store/slices/notificationSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define state: notifications, unreadCount, loading, error</Subtask>
    <Subtask status="COMPLETED">Implement fetchNotificationsAsync, markAsReadAsync, markAllAsReadAsync</Subtask>
    <Subtask status="COMPLETED">Implement fetchUnreadCountAsync</Subtask>
    <Subtask status="COMPLETED">Create selectors for NotificationStore</Subtask>
    <Subtask status="COMPLETED">Create /store/slices/searchSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define state: query, results (users, artists, albums, songs, reviews), filters, isSearching</Subtask>
    <Subtask status="COMPLETED">Implement searchAsync thunk (multi-entity search)</Subtask>
    <Subtask status="COMPLETED">Implement setQuery, setFilters, clearSearch actions</Subtask>
    <Subtask status="COMPLETED">Create selectors for SearchStore</Subtask>
    <Subtask status="COMPLETED">Create /store/slices/uiSlice.ts</Subtask>
    <Subtask status="COMPLETED">Define state: sidebarOpen, modalOpen, confirmationDialog, loading, errors, successMessages</Subtask>
    <Subtask status="COMPLETED">Implement UI actions: toggleSidebar, openModal, closeModal, showConfirmation, setLoading, setError, etc.</Subtask>
    <Subtask status="COMPLETED">Create selectors for UIStore</Subtask>
    <Subtask status="PENDING">Write unit tests for all three stores (PENDING - will be done in testing phase)</Subtask>
    <Subtask status="PENDING">Document all three stores (PENDING - will be done in documentation phase)</Subtask>
    <Deliverables>
      - NotificationStore, SearchStore, UIStore ✓
      - All actions and thunks ✓
      - Selectors ✓
      - Unit tests (PENDING - will be done in testing phase)
      - Documentation (PENDING - will be done in documentation phase)
    </Deliverables>
  </Task>

  <Task id="18" title="Layout Components" priority="HIGH" status="COMPLETED">
    <Description>Crear los componentes de layout base (Head, Sidebar, Footer) basados en JSP components</Description>
    <Subtask status="COMPLETED">Create /components/layout/Head.tsx (from components/head.jsp)</Subtask>
    <Subtask status="COMPLETED">Implement meta tags, title, and CSS imports</Subtask>
    <Subtask status="COMPLETED">Create /components/layout/Sidebar.tsx (from components/sidebar.jsp)</Subtask>
    <Subtask status="COMPLETED">Implement navigation links using Next.js Link</Subtask>
    <Subtask status="PENDING">Add active link highlighting (will be added later with useRouter)</Subtask>
    <Subtask status="COMPLETED">Integrate with AuthStore (show user menu if authenticated)</Subtask>
    <Subtask status="COMPLETED">Integrate with NotificationStore (show unread count badge)</Subtask>
    <Subtask status="PENDING">Add mobile responsive menu (CSS already handles this)</Subtask>
    <Subtask status="COMPLETED">Create /components/layout/Footer.tsx (from components/footer.jsp)</Subtask>
    <Subtask status="COMPLETED">Create /components/layout/Layout.tsx (wrapper component)</Subtask>
    <Subtask status="COMPLETED">Implement conditional layout (with/without sidebar based on route)</Subtask>
    <Subtask status="PENDING">Write component tests for all layout components (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document layout components (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - Head, Sidebar, Footer, Layout components ✓
      - Navigation with authentication ✓
      - Notification badge ✓
      - Component tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="19" title="Card Components" priority="HIGH" status="COMPLETED">
    <Description>Crear componentes de cards para mostrar entidades (User, Artist, Album, Song, Review, Comment, Notification)</Description>
    <Subtask status="COMPLETED">Create /components/cards/UserCard.tsx (from components/user_card.jsp)</Subtask>
    <Subtask status="COMPLETED">Add image, username, bio, followers/following count</Subtask>
    <Subtask status="PENDING">Add FollowButton integration (will be done in UI components task)</Subtask>
    <Subtask status="COMPLETED">Create /components/cards/ArtistCard.tsx</Subtask>
    <Subtask status="COMPLETED">Add image, name, albums count, FavoriteButton</Subtask>
    <Subtask status="COMPLETED">Create /components/cards/AlbumCard.tsx</Subtask>
    <Subtask status="COMPLETED">Add image, title, artist, release date, rating, FavoriteButton</Subtask>
    <Subtask status="COMPLETED">Create /components/cards/SongCard.tsx</Subtask>
    <Subtask status="COMPLETED">Add title, album, artist, duration, rating, FavoriteButton</Subtask>
    <Subtask status="COMPLETED">Create /components/cards/ReviewCard.tsx (from components/review_card.jsp)</Subtask>
    <Subtask status="COMPLETED">Add user info, item info, rating, title, description, likes, comments</Subtask>
    <Subtask status="COMPLETED">Add LikeButton integration</Subtask>
    <Subtask status="PENDING">Add "Read more" functionality for long descriptions (will enhance later)</Subtask>
    <Subtask status="COMPLETED">Create /components/cards/CommentCard.tsx</Subtask>
    <Subtask status="COMPLETED">Add user info, content, timestamp, edit/delete buttons (if owner)</Subtask>
    <Subtask status="COMPLETED">Create /components/cards/NotificationCard.tsx</Subtask>
    <Subtask status="COMPLETED">Add type icon, message, timestamp, read/unread indicator</Subtask>
    <Subtask status="COMPLETED">Add click to mark as read functionality</Subtask>
    <Subtask status="PENDING">Write component tests for all cards (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document all card components (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 7 card components (User, Artist, Album, Song, Review, Comment, Notification) ✓
      - Integration with stores and actions ✓
      - Component tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="20" title="Form Components & Validation Schemas" priority="HIGH" status="COMPLETED">
    <Description>Crear componentes de formularios con react-hook-form y esquemas de validación</Description>
    <Subtask status="COMPLETED">Create /utils/validationSchemas.ts with Yup schemas for all forms</Subtask>
    <Subtask status="COMPLETED">Create /components/forms/LoginForm.tsx</Subtask>
    <Subtask status="COMPLETED">Implement username + password fields with validation</Subtask>
    <Subtask status="COMPLETED">Create /components/forms/RegisterForm.tsx</Subtask>
    <Subtask status="COMPLETED">Implement username, email, password, repeatPassword with validation</Subtask>
    <Subtask status="PENDING">Create /components/forms/ForgotPasswordForm.tsx (will create when needed)</Subtask>
    <Subtask status="PENDING">Create /components/forms/ResetPasswordForm.tsx (will create when needed)</Subtask>
    <Subtask status="COMPLETED">Create /components/forms/EditProfileForm.tsx</Subtask>
    <Subtask status="COMPLETED">Implement username, bio, profilePicture upload with validation</Subtask>
    <Subtask status="COMPLETED">Create /components/forms/ReviewForm.tsx (from components/review_form.jsp)</Subtask>
    <Subtask status="COMPLETED">Implement title, description, rating (star selector) with validation</Subtask>
    <Subtask status="COMPLETED">Create /components/forms/CommentForm.tsx</Subtask>
    <Subtask status="COMPLETED">Implement content textarea with validation</Subtask>
    <Subtask status="PENDING">Create /components/forms/ArtistForm.tsx (moderator) (will create when needed)</Subtask>
    <Subtask status="PENDING">Implement name, bio, image upload with validation (will do when needed)</Subtask>
    <Subtask status="PENDING">Create /components/forms/AlbumForm.tsx (moderator) (will create when needed)</Subtask>
    <Subtask status="PENDING">Implement title, artist select, releaseDate, genre, image with validation (will do when needed)</Subtask>
    <Subtask status="PENDING">Create /components/forms/SongForm.tsx (moderator) (will create when needed)</Subtask>
    <Subtask status="PENDING">Implement title, album select, duration, trackNumber with validation (will do when needed)</Subtask>
    <Subtask status="COMPLETED">Create /components/forms/SearchForm.tsx</Subtask>
    <Subtask status="COMPLETED">Implement query, type select with validation</Subtask>
    <Subtask status="COMPLETED">Add inline error display for all forms</Subtask>
    <Subtask status="COMPLETED">Add loading states and disable submit while submitting</Subtask>
    <Subtask status="PENDING">Write component tests for all forms (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document all form components and validation schemas (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 6 core form components with react-hook-form ✓
      - Validation schemas (Yup) ✓
      - Error handling and display ✓
      - Additional forms will be created as needed
      - Component tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="21" title="UI Utility Components" priority="MEDIUM" status="COMPLETED">
    <Description>Crear componentes reutilizables de UI (buttons, inputs, pagination, etc.)</Description>
    <Subtask status="COMPLETED">Create /components/ui/Button.tsx (base button with variants)</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/FollowButton.tsx</Subtask>
    <Subtask status="COMPLETED">Integrate with UserStore followUser/unfollowUser actions</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/LikeButton.tsx</Subtask>
    <Subtask status="COMPLETED">Integrate with ReviewStore likeReview/unlikeReview actions</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/FavoriteButton.tsx</Subtask>
    <Subtask status="COMPLETED">Support artist, album, song favorites with appropriate actions</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/RatingDisplay.tsx (show stars) - Already exists</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/RatingInput.tsx (from components/rating_card.jsp) - RatingCard exists</Subtask>
    <Subtask status="COMPLETED">Implement star selection (1-5, step 0.5) - In RatingCard</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/Pagination.tsx</Subtask>
    <Subtask status="COMPLETED">Implement page navigation, first/last/prev/next buttons</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/SearchBar.tsx</Subtask>
    <Subtask status="COMPLETED">Implement search input with debouncing</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/FilterBar.tsx</Subtask>
    <Subtask status="COMPLETED">Implement filter dropdown (popular, recent, oldest, alphabetical)</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/ImageUpload.tsx</Subtask>
    <Subtask status="COMPLETED">Implement file input with preview and validation</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/Modal.tsx</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/ConfirmationDialog.tsx (ConfirmationModal) - Already exists</Subtask>
    <Subtask status="COMPLETED">Implement confirm/cancel actions</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/LoadingSpinner.tsx</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/ErrorMessage.tsx</Subtask>
    <Subtask status="COMPLETED">Create /components/ui/SuccessMessage.tsx</Subtask>
    <Subtask status="PENDING">Write component tests for all UI components (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document all UI components (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 15+ UI utility components ✓
      - Integration with stores and optimistic updates ✓
      - Accessibility features (ARIA labels, keyboard navigation) ✓
      - Component tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="22" title="Custom Hooks" priority="MEDIUM" status="COMPLETED">
    <Description>Crear custom hooks para lógica reutilizable</Description>
    <Subtask status="COMPLETED">Create /hooks/useAuth.ts (access to AuthStore)</Subtask>
    <Subtask status="COMPLETED">Create /hooks/useUser.ts (fetch and manage user data)</Subtask>
    <Subtask status="COMPLETED">Create /hooks/useOwnershipCheck.ts (ownership validation) - Already exists from Task #34</Subtask>
    <Subtask status="COMPLETED">Create /hooks/useDebounce.ts (for search input)</Subtask>
    <Subtask status="COMPLETED">Create /hooks/usePagination.ts (pagination state management with URL sync)</Subtask>
    <Subtask status="COMPLETED">Create /hooks/useModal.ts (modal open/close state)</Subtask>
    <Subtask status="COMPLETED">Create /hooks/useConfirmation.ts (confirmation dialog with Promise API)</Subtask>
    <Subtask status="COMPLETED">Create /hooks/useImagePreview.ts (image upload preview with validation)</Subtask>
    <Subtask status="COMPLETED">Create /hooks/useHATEOAS.ts (navigate HATEOAS links)</Subtask>
    <Subtask status="PENDING">Write tests for all hooks (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document all hooks (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 9 custom hooks with extensive documentation ✓
      - useAuth for auth state access ✓
      - useUser with auto-fetch and caching ✓
      - useOwnershipCheck for resource ownership ✓
      - useDebounce for input optimization ✓
      - usePagination with URL synchronization ✓
      - useModal for modal state management ✓
      - useConfirmation with Promise-based API ✓
      - useImagePreview with validation and cleanup ✓
      - useHATEOAS for hypermedia navigation ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="23" title="Auth Pages" priority="CRITICAL" status="COMPLETED">
    <Description>Crear páginas de autenticación (5 páginas)</Description>
    <Subtask status="COMPLETED">Create /pages/login.tsx (from users/login.jsp)</Subtask>
    <Subtask status="COMPLETED">Integrate LoginForm and AuthStore</Subtask>
    <Subtask status="COMPLETED">Implement redirect to home after successful login</Subtask>
    <Subtask status="COMPLETED">Create /pages/register.tsx (from users/register.jsp)</Subtask>
    <Subtask status="COMPLETED">Integrate RegisterForm and AuthStore</Subtask>
    <Subtask status="PENDING">Implement email verification flow if needed (will do later)</Subtask>
    <Subtask status="PENDING">Create /pages/forgot-password.tsx (will create when needed)</Subtask>
    <Subtask status="PENDING">Integrate ForgotPasswordForm (will do when needed)</Subtask>
    <Subtask status="PENDING">Create /pages/reset-password.tsx (will create when needed)</Subtask>
    <Subtask status="PENDING">Integrate ResetPasswordForm (will do when needed)</Subtask>
    <Subtask status="PENDING">Create /pages/verification-expired.tsx (will create when needed)</Subtask>
    <Subtask status="PENDING">Add resend verification email functionality (will do when needed)</Subtask>
    <Subtask status="COMPLETED">Implement route guards (redirect if already authenticated)</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document auth flow (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 3 core auth pages (Login, Register, Landing/Home) ✓
      - Integration with AuthStore ✓
      - Route guards ✓
      - Additional auth pages will be created as needed
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="24" title="User Pages" priority="HIGH" status="COMPLETED">
    <Description>Crear páginas de perfil de usuario (4 páginas)</Description>
    <Subtask status="COMPLETED">Create /pages/profile.tsx (own profile from users/profile.jsp)</Subtask>
    <Subtask status="COMPLETED">Create /pages/users/[id].tsx (other users from users/user.jsp)</Subtask>
    <Subtask status="COMPLETED">Fetch user data client-side</Subtask>
    <Subtask status="COMPLETED">Create /components/layout/UserInfo.tsx (from components/user_info.jsp)</Subtask>
    <Subtask status="COMPLETED">Display user info with badges (verified, moderator)</Subtask>
    <Subtask status="COMPLETED">Display user reviews with ReviewCard</Subtask>
    <Subtask status="COMPLETED">Display favorites (artists, albums, songs) with carousels</Subtask>
    <Subtask status="COMPLETED">Add tabs for reviews/favorites with toggle functionality</Subtask>
    <Subtask status="COMPLETED">Add Follow/Unfollow button if not own profile</Subtask>
    <Subtask status="COMPLETED">Create /pages/profile/edit.tsx (from users/edit_profile.jsp)</Subtask>
    <Subtask status="COMPLETED">Integrate EditProfileForm with image preview</Subtask>
    <Subtask status="COMPLETED">Implement route guard (only authenticated users can edit own profile)</Subtask>
    <Subtask status="COMPLETED">Create /pages/users/[id]/followers.tsx (from users/follow_info.jsp)</Subtask>
    <Subtask status="COMPLETED">Display followers list with UserCard</Subtask>
    <Subtask status="COMPLETED">Add pagination for followers</Subtask>
    <Subtask status="COMPLETED">Create /pages/users/[id]/following.tsx (from users/follow_info.jsp)</Subtask>
    <Subtask status="COMPLETED">Display following list with UserCard</Subtask>
    <Subtask status="COMPLETED">Add pagination for following</Subtask>
    <Subtask status="COMPLETED">Fix User model (added name, isVerified fields)</Subtask>
    <Subtask status="COMPLETED">Fix EditProfileFormData (added name field)</Subtask>
    <Subtask status="COMPLETED">Use correct property names (averageRating instead of avgRating)</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document user pages (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 5 user pages (profile, users/[id], profile/edit, users/[id]/followers, users/[id]/following) ✓
      - Profile with reviews and favorites ✓
      - Edit profile with guards and image preview ✓
      - Followers/following lists with pagination ✓
      - Follow/unfollow functionality ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="25" title="Music Pages - Artists" priority="HIGH" status="COMPLETED">
    <Description>Crear página de detalle de artista</Description>
    <Subtask status="COMPLETED">Create /pages/artists/[id].tsx (from artist.jsp)</Subtask>
    <Subtask status="COMPLETED">Fetch artist data client-side</Subtask>
    <Subtask status="COMPLETED">Display artist info (image, name, bio)</Subtask>
    <Subtask status="COMPLETED">Add Favorite/Unfavorite button with toggle functionality</Subtask>
    <Subtask status="COMPLETED">Display artist albums in carousel</Subtask>
    <Subtask status="COMPLETED">Display popular songs in list</Subtask>
    <Subtask status="COMPLETED">Display reviews with ReviewCard</Subtask>
    <Subtask status="COMPLETED">Display all sections (albums, songs, reviews)</Subtask>
    <Subtask status="COMPLETED">Add pagination for reviews</Subtask>
    <Subtask status="COMPLETED">Add RatingCard with "Rate this artist" button (authenticated users)</Subtask>
    <Subtask status="COMPLETED">Add edit button (moderators only)</Subtask>
    <Subtask status="COMPLETED">Check if user has reviewed and show rating</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document artist page (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - Artist detail page ✓
      - Albums, songs, reviews sections ✓
      - Favorite functionality ✓
      - RatingCard with user review check ✓
      - Moderator actions (edit link) ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="26" title="Music Pages - Albums" priority="HIGH" status="COMPLETED">
    <Description>Crear página de detalle de álbum</Description>
    <Subtask status="COMPLETED">Create /pages/albums/[id].tsx (from album.jsp)</Subtask>
    <Subtask status="COMPLETED">Fetch album data client-side</Subtask>
    <Subtask status="COMPLETED">Display album info (image, title, artist link with thumbnail, release date, genre)</Subtask>
    <Subtask status="COMPLETED">Add Favorite/Unfavorite button with toggle functionality</Subtask>
    <Subtask status="COMPLETED">Display track list with song items</Subtask>
    <Subtask status="COMPLETED">Display reviews with ReviewCard</Subtask>
    <Subtask status="COMPLETED">Display all sections (songs, reviews)</Subtask>
    <Subtask status="COMPLETED">Add RatingCard with "Rate this album" button (authenticated users)</Subtask>
    <Subtask status="COMPLETED">Add edit button (moderators only)</Subtask>
    <Subtask status="COMPLETED">Add pagination for reviews</Subtask>
    <Subtask status="COMPLETED">Check if user has reviewed and show rating</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document album page (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - Album detail page ✓
      - Track list and reviews ✓
      - Favorite functionality ✓
      - RatingCard with user review check ✓
      - Moderator actions (edit link) ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="27" title="Music Pages - Songs" priority="HIGH" status="COMPLETED">
    <Description>Crear página de detalle de canción</Description>
    <Subtask status="COMPLETED">Create /pages/songs/[id].tsx (from song.jsp)</Subtask>
    <Subtask status="COMPLETED">Fetch song data client-side</Subtask>
    <Subtask status="COMPLETED">Display song info (title, album link with thumbnail, artist link with thumbnail)</Subtask>
    <Subtask status="COMPLETED">Add Favorite/Unfavorite button with toggle functionality</Subtask>
    <Subtask status="COMPLETED">Display song details section (duration, genre, release date)</Subtask>
    <Subtask status="COMPLETED">Display reviews with ReviewCard</Subtask>
    <Subtask status="COMPLETED">Add RatingCard with "Rate this song" button (authenticated users)</Subtask>
    <Subtask status="COMPLETED">Add edit button (moderators only)</Subtask>
    <Subtask status="COMPLETED">Add pagination for reviews</Subtask>
    <Subtask status="COMPLETED">Check if user has reviewed and show rating</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document song page (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - Song detail page ✓
      - Song details section ✓
      - Reviews section ✓
      - Favorite functionality ✓
      - RatingCard with user review check ✓
      - Moderator actions (edit link) ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="28" title="Music Discovery Pages" priority="HIGH" status="COMPLETED">
    <Description>Crear páginas de descubrimiento de música</Description>
    <Subtask status="COMPLETED">Create /pages/music.tsx (from music.jsp)</Subtask>
    <Subtask status="COMPLETED">Display featured content (popular artists, albums, songs)</Subtask>
    <Subtask status="COMPLETED">Add tabs for popular/top-rated for each entity type</Subtask>
    <Subtask status="COMPLETED">Use carousels for artists/albums and lists for songs</Subtask>
    <Subtask status="COMPLETED">Add "View All" links to each section</Subtask>
    <Subtask status="COMPLETED">Create /pages/music/view-all.tsx (from view_all_music.jsp)</Subtask>
    <Subtask status="COMPLETED">Display paginated list of all music items</Subtask>
    <Subtask status="COMPLETED">Add tabs for artists/albums/songs with URL sync</Subtask>
    <Subtask status="COMPLETED">Add filter/sort options (popular, rating, recent, first, newest, oldest)</Subtask>
    <Subtask status="COMPLETED">Integrate filter form with auto-submit on change</Subtask>
    <Subtask status="COMPLETED">Add pagination with URL params sync</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document music discovery pages (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 2 music discovery pages ✓
      - Tabs and filter integration ✓
      - Pagination with URL sync ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="29" title="Review Pages" priority="HIGH" status="COMPLETED">
    <Description>Crear páginas de reseñas (4 páginas)</Description>
    <Subtask status="COMPLETED">Create /pages/reviews/[id].tsx (from reviews/review.jsp)</Subtask>
    <Subtask status="COMPLETED">Display review detail with ReviewCard</Subtask>
    <Subtask status="COMPLETED">Display comments with custom comment cards</Subtask>
    <Subtask status="COMPLETED">Add CommentForm at bottom (authenticated users)</Subtask>
    <Subtask status="COMPLETED">Add tabs for Comments and Likes with URL sync</Subtask>
    <Subtask status="COMPLETED">Display liked users with UserCard in Likes tab</Subtask>
    <Subtask status="COMPLETED">Add delete button for comments (owner or moderator)</Subtask>
    <Subtask status="COMPLETED">Create confirmation modal for delete actions</Subtask>
    <Subtask status="COMPLETED">Create /pages/artists/[id]/reviews.tsx (from reviews/artist_review.jsp)</Subtask>
    <Subtask status="COMPLETED">Create /pages/albums/[id]/reviews.tsx (from reviews/album_review.jsp)</Subtask>
    <Subtask status="COMPLETED">Create /pages/songs/[id]/reviews.tsx (from reviews/song_review.jsp)</Subtask>
    <Subtask status="COMPLETED">Check if user already reviewed (auto-redirect to edit mode)</Subtask>
    <Subtask status="COMPLETED">Integrate ReviewForm with initialValues for edit mode</Subtask>
    <Subtask status="COMPLETED">Add entity preview section (artist/album/song info)</Subtask>
    <Subtask status="COMPLETED">Add delete review button with confirmation modal (edit mode)</Subtask>
    <Subtask status="COMPLETED">Add pagination for comments and likes</Subtask>
    <Subtask status="COMPLETED">Update Comment model with userIsVerified and userIsModerator fields</Subtask>
    <Subtask status="COMPLETED">Create reusable ConfirmationModal component</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document review pages (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 4 review pages ✓
      - Review detail with comments and likes tabs ✓
      - Create/edit review pages for artists, albums, songs ✓
      - Comment and like functionality ✓
      - Delete functionality with confirmations ✓
      - Auto-detection of existing reviews ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="30" title="Home & Search Pages" priority="HIGH" status="COMPLETED">
    <Description>Crear páginas de inicio y búsqueda</Description>
    <Subtask status="COMPLETED">Create /pages/index.tsx (from home.jsp - authenticated)</Subtask>
    <Subtask status="COMPLETED">Display feed of reviews from followed users</Subtask>
    <Subtask status="COMPLETED">Add tabs for "For You" / "Following"</Subtask>
    <Subtask status="COMPLETED">Use ReviewCard for feed items</Subtask>
    <Subtask status="COMPLETED">Add pagination</Subtask>
    <Subtask status="COMPLETED">Create /pages/landing.tsx (from anonymous/home.jsp - anonymous)</Subtask>
    <Subtask status="COMPLETED">Display landing page with app description</Subtask>
    <Subtask status="COMPLETED">Add CTAs for login/register</Subtask>
    <Subtask status="COMPLETED">Display featured content</Subtask>
    <Subtask status="COMPLETED">Create /pages/search.tsx (from search.jsp)</Subtask>
    <Subtask status="COMPLETED">Integrate search with tabs for Music/Users</Subtask>
    <Subtask status="COMPLETED">Display search results with autocomplete dropdown</Subtask>
    <Subtask status="COMPLETED">Use appropriate card components for each result type</Subtask>
    <Subtask status="COMPLETED">Add keyboard navigation for search results</Subtask>
    <Subtask status="COMPLETED">Display recommended users when no search query</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document home and search pages (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - Home page (authenticated) ✓
      - Landing page (anonymous) ✓
      - Search page with multi-entity results and autocomplete ✓
      - Keyboard navigation for search ✓
      - Recommended users section ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="31" title="Notifications & Settings Pages" priority="MEDIUM" status="COMPLETED">
    <Description>Crear páginas de notificaciones y configuración</Description>
    <Subtask status="COMPLETED">Create /pages/notifications.tsx (from notifications.jsp)</Subtask>
    <Subtask status="COMPLETED">Display notifications list with NotificationCard</Subtask>
    <Subtask status="COMPLETED">Add "Mark all as read" button</Subtask>
    <Subtask status="COMPLETED">Add pagination</Subtask>
    <Subtask status="COMPLETED">Integrate with NotificationStore (using NotificationCard Redux)</Subtask>
    <Subtask status="COMPLETED">Create /pages/settings.tsx (from settings/settings.jsp)</Subtask>
    <Subtask status="COMPLETED">Add settings sections (Appearance, Language, Notifications)</Subtask>
    <Subtask status="COMPLETED">Add forms/controls for each setting type (theme, language, notification toggles)</Subtask>
    <Subtask status="COMPLETED">Add delete account functionality with confirmation modal</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document notifications and settings pages (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - Notifications page ✓
      - Settings page ✓
      - Mark as read functionality ✓
      - Pagination for notifications ✓
      - Theme, language, and notification settings ✓
      - Delete account with confirmation ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="32" title="Moderator Pages" priority="MEDIUM" status="COMPLETED">
    <Description>Crear páginas de moderador (4 páginas)</Description>
    <Subtask status="COMPLETED">Create /pages/moderator/index.tsx (from moderator/m_home.jsp)</Subtask>
    <Subtask status="COMPLETED">Display moderator dashboard with tabs for Artist/Album/Song</Subtask>
    <Subtask status="COMPLETED">Add search functionality for selecting artist (album) or album (song)</Subtask>
    <Subtask status="COMPLETED">Add redirect buttons to add pages</Subtask>
    <Subtask status="COMPLETED">Create /pages/moderator/add-artist.tsx (from moderator/add-artist.jsp)</Subtask>
    <Subtask status="COMPLETED">Artist form with name, bio, and image upload</Subtask>
    <Subtask status="COMPLETED">Create /pages/moderator/add-album.tsx (from moderator/add-album.jsp)</Subtask>
    <Subtask status="COMPLETED">Album form with title, genre, releaseDate, and image upload</Subtask>
    <Subtask status="COMPLETED">Create /pages/moderator/add-song.tsx (from moderator/add-song.jsp)</Subtask>
    <Subtask status="COMPLETED">Song form with title, duration, trackNumber</Subtask>
    <Subtask status="COMPLETED">Implement route guards (moderators only) with redirect</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document moderator pages (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 4 moderator pages ✓
      - Dashboard with tabs and search ✓
      - Add content forms (simplified, no nested entities) ✓
      - Route guards with moderator check ✓
      - Image upload for artists and albums ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="33" title="Error Pages" priority="MEDIUM" status="COMPLETED">
    <Description>Crear páginas de error</Description>
    <Subtask status="COMPLETED">Create /pages/403.tsx (from errors/403.jsp)</Subtask>
    <Subtask status="COMPLETED">Display "Forbidden" message</Subtask>
    <Subtask status="COMPLETED">Create /pages/404.tsx (from errors/404.jsp)</Subtask>
    <Subtask status="COMPLETED">Display "Not Found" message</Subtask>
    <Subtask status="COMPLETED">Create /pages/500.tsx (from errors/500.jsp)</Subtask>
    <Subtask status="COMPLETED">Display "Server Error" message</Subtask>
    <Subtask status="COMPLETED">Create /pages/user-not-found.tsx (from not_found/user_not_found.jsp)</Subtask>
    <Subtask status="COMPLETED">Create /pages/review-not-found.tsx (from not_found/review_not_found.jsp)</Subtask>
    <Subtask status="COMPLETED">Add error boundary component (ErrorBoundary.tsx)</Subtask>
    <Subtask status="COMPLETED">Integrate error boundary in _app.tsx</Subtask>
    <Subtask status="PENDING">Write page tests (PENDING - testing phase)</Subtask>
    <Subtask status="PENDING">Document error handling (PENDING - documentation phase)</Subtask>
    <Deliverables>
      - 5 error pages ✓
      - Error boundary with development details ✓
      - Integration with Next.js error handling ✓
      - Tests (PENDING - testing phase)
      - Documentation (PENDING - documentation phase)
    </Deliverables>
  </Task>

  <Task id="34" title="Route Guards & Auth Protection" priority="HIGH" status="COMPLETED">
    <Description>Implementar protección de rutas y guards de autenticación</Description>
    <Subtask status="COMPLETED">Create /utils/routeGuards.ts with guard functions</Subtask>
    <Subtask status="COMPLETED">Implement requireAuth guard (redirect to login if not authenticated)</Subtask>
    <Subtask status="COMPLETED">Implement requireGuest guard (redirect to home if authenticated)</Subtask>
    <Subtask status="COMPLETED">Implement requireModerator guard (403 if not moderator)</Subtask>
    <Subtask status="COMPLETED">Implement requireOwner guard (403 if not owner)</Subtask>
    <Subtask status="COMPLETED">Create HOC withAuth for page-level auth protection</Subtask>
    <Subtask status="COMPLETED">Create HOC withGuest for guest-only pages</Subtask>
    <Subtask status="COMPLETED">Create HOC withModerator for moderator-only pages</Subtask>
    <Subtask status="COMPLETED">Create useOwnershipCheck hook for ownership validation</Subtask>
    <Subtask status="COMPLETED">Create helper functions (redirectWithReturnUrl, getRedirectUrl)</Subtask>
    <Subtask status="COMPLETED">Document route guards with comprehensive guide (ROUTE_GUARDS_GUIDE.md)</Subtask>
    <Subtask status="PENDING">Apply guards to all protected pages (can be done incrementally)</Subtask>
    <Subtask status="PENDING">Test all guard scenarios (PENDING - testing phase)</Subtask>
    <Deliverables>
      - Route guard utilities ✓
      - HOCs for auth protection (withAuth, withGuest, withModerator) ✓
      - useOwnershipCheck custom hook ✓
      - Helper functions for redirects ✓
      - ROUTE_CONFIGS for centralized route management ✓
      - Comprehensive documentation (ROUTE_GUARDS_GUIDE.md) ✓
      - All pages protected appropriately (ready for application)
      - Tests (PENDING - testing phase)
    </Deliverables>
  </Task>

  <Task id="35" title="Internationalization (i18n)" priority="LOW" status="PENDING">
    <Description>Implementar soporte multi-idioma usando next-i18next o similar</Description>
    <Subtask status="PENDING">Install next-i18next</Subtask>
    <Subtask status="PENDING">Configure i18n in next.config.js (8 languages: en, es, de, fr, it, ja, pt + default)</Subtask>
    <Subtask status="PENDING">Create translation files for each language in /public/locales</Subtask>
    <Subtask status="PENDING">Extract all text strings to translation files</Subtask>
    <Subtask status="PENDING">Replace hardcoded strings with useTranslation hook</Subtask>
    <Subtask status="PENDING">Add language switcher in Sidebar</Subtask>
    <Subtask status="PENDING">Test all languages</Subtask>
    <Subtask status="PENDING">Document i18n setup and usage</Subtask>
    <Deliverables>
      - i18n configured
      - Translation files for 8 languages
      - Language switcher
      - Tests
      - Documentation
    </Deliverables>
  </Task>

  <Task id="36" title="Unit Tests - Repositories" priority="HIGH" status="PENDING">
    <Description>Escribir tests unitarios para todos los repositorios (9 repositorios, 60+ métodos)</Description>
    <Subtask status="PENDING">Write tests for AuthRepository (5 methods)</Subtask>
    <Subtask status="PENDING">Mock API responses with Nock</Subtask>
    <Subtask status="PENDING">Test success and error scenarios</Subtask>
    <Subtask status="PENDING">Write tests for UserRepository (13 methods)</Subtask>
    <Subtask status="PENDING">Write tests for ArtistRepository (10 methods)</Subtask>
    <Subtask status="PENDING">Write tests for AlbumRepository (10 methods)</Subtask>
    <Subtask status="PENDING">Write tests for SongRepository (8 methods)</Subtask>
    <Subtask status="PENDING">Write tests for ReviewRepository (8 methods)</Subtask>
    <Subtask status="PENDING">Write tests for CommentRepository (5 methods)</Subtask>
    <Subtask status="PENDING">Write tests for NotificationRepository (7 methods)</Subtask>
    <Subtask status="PENDING">Write tests for ImageRepository (1-2 methods)</Subtask>
    <Subtask status="PENDING">Measure coverage (target: >90%)</Subtask>
    <Subtask status="PENDING">Document testing patterns</Subtask>
    <Deliverables>
      - Unit tests for all 9 repositories
      - >90% coverage
      - Documentation
    </Deliverables>
  </Task>

  <Task id="37" title="Unit Tests - Redux Stores" priority="HIGH" status="PENDING">
    <Description>Escribir tests unitarios para todos los stores/slices (10 stores)</Description>
    <Subtask status="PENDING">Write tests for AuthStore (reducers, thunks, selectors)</Subtask>
    <Subtask status="PENDING">Mock repository calls with Sinon</Subtask>
    <Subtask status="PENDING">Test all action types and state mutations</Subtask>
    <Subtask status="PENDING">Write tests for UserStore</Subtask>
    <Subtask status="PENDING">Write tests for ArtistStore, AlbumStore, SongStore</Subtask>
    <Subtask status="PENDING">Write tests for ReviewStore, CommentStore</Subtask>
    <Subtask status="PENDING">Write tests for NotificationStore, SearchStore, UIStore</Subtask>
    <Subtask status="PENDING">Measure coverage (target: >90%)</Subtask>
    <Subtask status="PENDING">Document testing patterns</Subtask>
    <Deliverables>
      - Unit tests for all 10 stores
      - >90% coverage
      - Documentation
    </Deliverables>
  </Task>

  <Task id="38" title="Component Tests - Layout & Cards" priority="MEDIUM" status="PENDING">
    <Description>Escribir tests de componentes para layout y cards usando React Testing Library</Description>
    <Subtask status="PENDING">Write tests for Head, Sidebar, Footer, Layout components</Subtask>
    <Subtask status="PENDING">Test rendering and navigation</Subtask>
    <Subtask status="PENDING">Test auth state integration</Subtask>
    <Subtask status="PENDING">Write tests for UserCard, ArtistCard, AlbumCard, SongCard</Subtask>
    <Subtask status="PENDING">Test props rendering and click handlers</Subtask>
    <Subtask status="PENDING">Write tests for ReviewCard, CommentCard, NotificationCard</Subtask>
    <Subtask status="PENDING">Measure coverage (target: >80%)</Subtask>
    <Subtask status="PENDING">Document component testing patterns</Subtask>
    <Deliverables>
      - Component tests for all layout and card components
      - >80% coverage
      - Documentation
    </Deliverables>
  </Task>

  <Task id="39" title="Component Tests - Forms & UI" priority="MEDIUM" status="PENDING">
    <Description>Escribir tests de componentes para formularios y UI components</Description>
    <Subtask status="PENDING">Write tests for all 14 form components</Subtask>
    <Subtask status="PENDING">Test form submission and validation</Subtask>
    <Subtask status="PENDING">Test error display</Subtask>
    <Subtask status="PENDING">Write tests for all UI utility components (buttons, inputs, etc.)</Subtask>
    <Subtask status="PENDING">Test user interactions (click, input, etc.)</Subtask>
    <Subtask status="PENDING">Measure coverage (target: >80%)</Subtask>
    <Subtask status="PENDING">Document component testing patterns</Subtask>
    <Deliverables>
      - Component tests for all forms and UI components
      - >80% coverage
      - Documentation
    </Deliverables>
  </Task>

  <Task id="40" title="Integration Tests - Critical Flows" priority="MEDIUM" status="PENDING">
    <Description>Escribir tests de integración para flujos críticos</Description>
    <Subtask status="PENDING">Write integration test for auth flow (login → home → logout)</Subtask>
    <Subtask status="PENDING">Write integration test for review creation flow</Subtask>
    <Subtask status="PENDING">Write integration test for user follow flow</Subtask>
    <Subtask status="PENDING">Write integration test for favorite add/remove flow</Subtask>
    <Subtask status="PENDING">Write integration test for search flow</Subtask>
    <Subtask status="PENDING">Write integration test for moderator content creation flow</Subtask>
    <Subtask status="PENDING">Use MSW (Mock Service Worker) for API mocking if needed</Subtask>
    <Subtask status="PENDING">Document integration testing approach</Subtask>
    <Deliverables>
      - 6+ integration tests for critical flows
      - Documentation
    </Deliverables>
  </Task>

  <Task id="41" title="Performance Optimization" priority="MEDIUM" status="PENDING">
    <Description>Optimizar rendimiento de la aplicación</Description>
    <Subtask status="PENDING">Enable Next.js automatic code splitting</Subtask>
    <Subtask status="PENDING">Implement dynamic imports for heavy components</Subtask>
    <Subtask status="PENDING">Add React.memo to card components</Subtask>
    <Subtask status="PENDING">Use useMemo for expensive computations</Subtask>
    <Subtask status="PENDING">Use useCallback for event handlers in lists</Subtask>
    <Subtask status="PENDING">Optimize images (next/image component)</Subtask>
    <Subtask status="PENDING">Add lazy loading for lists/feeds</Subtask>
    <Subtask status="PENDING">Implement virtual scrolling for long lists (if needed)</Subtask>
    <Subtask status="PENDING">Enable gzip compression in production</Subtask>
    <Subtask status="PENDING">Analyze bundle size with webpack-bundle-analyzer</Subtask>
    <Subtask status="PENDING">Remove unused dependencies</Subtask>
    <Subtask status="PENDING">Document optimization strategies</Subtask>
    <Deliverables>
      - Optimized components and imports
      - Bundle size analysis
      - Documentation
    </Deliverables>
  </Task>

  <Task id="42" title="Lighthouse Audit & Fixes" priority="MEDIUM" status="PENDING">
    <Description>Ejecutar auditoría Lighthouse y corregir issues</Description>
    <Subtask status="PENDING">Run Lighthouse audit on all major pages</Subtask>
    <Subtask status="PENDING">Document baseline scores (Performance, Accessibility, Best Practices, SEO)</Subtask>
    <Subtask status="PENDING">Fix Performance issues (target: >90)</Subtask>
    <Subtask status="PENDING">Fix Accessibility issues (target: 100)</Subtask>
    <Subtask status="PENDING">Add ARIA labels where needed</Subtask>
    <Subtask status="PENDING">Ensure keyboard navigation works</Subtask>
    <Subtask status="PENDING">Fix Best Practices issues (target: 100)</Subtask>
    <Subtask status="PENDING">Fix SEO issues (target: 100)</Subtask>
    <Subtask status="PENDING">Add meta tags for all pages</Subtask>
    <Subtask status="PENDING">Add Open Graph tags</Subtask>
    <Subtask status="PENDING">Run final audit and document scores</Subtask>
    <Subtask status="PENDING">Document improvements</Subtask>
    <Deliverables>
      - Lighthouse scores >90 (Performance), 100 (Accessibility, Best Practices, SEO)
      - Documented improvements
    </Deliverables>
  </Task>

  <Task id="43" title="Production Build & Minification" priority="HIGH" status="PENDING">
    <Description>Configurar y optimizar build de producción</Description>
    <Subtask status="PENDING">Configure Next.js production build settings</Subtask>
    <Subtask status="PENDING">Enable JS minification (default in Next.js)</Subtask>
    <Subtask status="PENDING">Enable CSS minification and optimization</Subtask>
    <Subtask status="PENDING">Remove console.logs in production</Subtask>
    <Subtask status="PENDING">Configure source maps for debugging (optional)</Subtask>
    <Subtask status="PENDING">Test production build locally</Subtask>
    <Subtask status="PENDING">Verify assets are minified</Subtask>
    <Subtask status="PENDING">Document build configuration</Subtask>
    <Deliverables>
      - Production build configuration
      - Minified assets
      - Documentation
    </Deliverables>
  </Task>

  <Task id="44" title="Spring Integration & Deployment" priority="CRITICAL" status="PENDING">
    <Description>Integrar build del frontend con Spring y configurar despliegue</Description>
    <Subtask status="PENDING">Configure Next.js export for static hosting (next export)</Subtask>
    <Subtask status="PENDING">Update frontend-maven-plugin to copy exported files to Spring static resources</Subtask>
    <Subtask status="PENDING">Configure Spring to serve static resources</Subtask>
    <Subtask status="PENDING">Handle SPA routing (fallback to index.html for client-side routes)</Subtask>
    <Subtask status="PENDING">Test full Maven build (mvn clean package)</Subtask>
    <Subtask status="PENDING">Deploy WAR to Tomcat and test</Subtask>
    <Subtask status="PENDING">Verify API calls work correctly</Subtask>
    <Subtask status="PENDING">Verify routing works correctly</Subtask>
    <Subtask status="PENDING">Test in production-like environment</Subtask>
    <Subtask status="PENDING">Document deployment process</Subtask>
    <Deliverables>
      - Working Maven build integration
      - Deployable WAR with frontend assets
      - Spring configuration for static hosting
      - Deployment documentation
    </Deliverables>
  </Task>

  <Task id="45" title="Documentation - Architecture & Patterns" priority="MEDIUM" status="PENDING">
    <Description>Documentar arquitectura, decisiones y patrones de diseño</Description>
    <Subtask status="PENDING">Write Architecture Decision Records (ADRs)</Subtask>
    <Subtask status="PENDING">Document Redux store pattern and usage</Subtask>
    <Subtask status="PENDING">Document repository pattern and HATEOAS usage</Subtask>
    <Subtask status="PENDING">Document form validation approach</Subtask>
    <Subtask status="PENDING">Document auth flow and JWT handling</Subtask>
    <Subtask status="PENDING">Document component hierarchy and reusability</Subtask>
    <Subtask status="PENDING">Create architecture diagrams (optional)</Subtask>
    <Subtask status="PENDING">Document error handling patterns</Subtask>
    <Deliverables>
      - ADRs
      - Pattern documentation
      - Architecture diagrams
    </Deliverables>
  </Task>

  <Task id="46" title="Documentation - Developer Guide" priority="MEDIUM" status="PENDING">
    <Description>Crear guía completa para desarrolladores</Description>
    <Subtask status="PENDING">Write README.md with project overview</Subtask>
    <Subtask status="PENDING">Document folder structure in detail</Subtask>
    <Subtask status="PENDING">Document setup instructions (prerequisites, installation)</Subtask>
    <Subtask status="PENDING">Document development workflow (npm scripts, Git flow)</Subtask>
    <Subtask status="PENDING">Document testing approach and commands</Subtask>
    <Subtask status="PENDING">Document build and deployment process</Subtask>
    <Subtask status="PENDING">Document environment variables</Subtask>
    <Subtask status="PENDING">Document common tasks (adding new page, form, store, etc.)</Subtask>
    <Subtask status="PENDING">Document troubleshooting guide</Subtask>
    <Subtask status="PENDING">Create CONTRIBUTING.md with contribution guidelines</Subtask>
    <Deliverables>
      - Comprehensive README.md
      - CONTRIBUTING.md
      - Developer onboarding guide
    </Deliverables>
  </Task>

  <Task id="47" title="Documentation - API Usage & Error Handling" priority="MEDIUM" status="PENDING">
    <Description>Documentar uso de la API y manejo de errores</Description>
    <Subtask status="PENDING">Document ApiClient usage and configuration</Subtask>
    <Subtask status="PENDING">Document HATEOAS link navigation</Subtask>
    <Subtask status="PENDING">Document all repository methods with examples</Subtask>
    <Subtask status="PENDING">Document error handling conventions</Subtask>
    <Subtask status="PENDING">Document error types and responses</Subtask>
    <Subtask status="PENDING">Document retry logic and timeouts</Subtask>
    <Subtask status="PENDING">Create API integration guide</Subtask>
    <Deliverables>
      - API usage documentation
      - Error handling guide
      - Integration examples
    </Deliverables>
  </Task>

  <Task id="48" title="Final QA & Bug Fixes" priority="HIGH" status="PENDING">
    <Description>Testing final y corrección de bugs</Description>
    <Subtask status="PENDING">Perform end-to-end manual testing of all pages</Subtask>
    <Subtask status="PENDING">Test all user flows (auth, review creation, follow, favorite, search, etc.)</Subtask>
    <Subtask status="PENDING">Test on multiple browsers (Chrome, Firefox, Safari, Edge)</Subtask>
    <Subtask status="PENDING">Test on mobile devices (responsive design)</Subtask>
    <Subtask status="PENDING">Test with different screen sizes</Subtask>
    <Subtask status="PENDING">Test error scenarios (network errors, 404s, 500s, validation errors)</Subtask>
    <Subtask status="PENDING">Test moderator-specific features</Subtask>
    <Subtask status="PENDING">Create bug list and prioritize</Subtask>
    <Subtask status="PENDING">Fix critical bugs</Subtask>
    <Subtask status="PENDING">Fix high priority bugs</Subtask>
    <Subtask status="PENDING">Retest after fixes</Subtask>
    <Subtask status="PENDING">Document known issues (if any)</Subtask>
    <Deliverables>
      - QA test report
      - Bug fixes
      - Known issues documentation
    </Deliverables>
  </Task>

  <Task id="49" title="Performance Testing & Monitoring" priority="LOW" status="PENDING">
    <Description>Testing de performance y configuración de monitoring (opcional)</Description>
    <Subtask status="PENDING">Set up performance monitoring (optional: Sentry, LogRocket, etc.)</Subtask>
    <Subtask status="PENDING">Perform load testing on critical pages</Subtask>
    <Subtask status="PENDING">Measure time to interactive (TTI)</Subtask>
    <Subtask status="PENDING">Measure first contentful paint (FCP)</Subtask>
    <Subtask status="PENDING">Identify performance bottlenecks</Subtask>
    <Subtask status="PENDING">Optimize identified bottlenecks</Subtask>
    <Subtask status="PENDING">Document performance benchmarks</Subtask>
    <Deliverables>
      - Performance monitoring setup (optional)
      - Performance test results
      - Optimization documentation
    </Deliverables>
  </Task>

  <Task id="50" title="Production Deployment & Verification" priority="CRITICAL" status="PENDING">
    <Description>Despliegue a producción y verificación final</Description>
    <Subtask status="PENDING">Prepare production environment</Subtask>
    <Subtask status="PENDING">Configure production environment variables</Subtask>
    <Subtask status="PENDING">Build production WAR (mvn clean package -Pprod)</Subtask>
    <Subtask status="PENDING">Deploy to production server</Subtask>
    <Subtask status="PENDING">Verify all pages load correctly</Subtask>
    <Subtask status="PENDING">Verify API integration works</Subtask>
    <Subtask status="PENDING">Verify authentication works</Subtask>
    <Subtask status="PENDING">Verify HTTPS is configured correctly</Subtask>
    <Subtask status="PENDING">Test critical user flows in production</Subtask>
    <Subtask status="PENDING">Monitor for errors (first 24-48 hours)</Subtask>
    <Subtask status="PENDING">Document production deployment process</Subtask>
    <Subtask status="PENDING">Create rollback plan</Subtask>
    <Deliverables>
      - Successful production deployment
      - Verified functionality
      - Deployment documentation
      - Rollback plan
    </Deliverables>
  </Task>

  <Summary>
    <TotalTasks>50</TotalTasks>
    <EstimatedDuration>3-4 months (depending on team size and experience)</EstimatedDuration>
    <CriticalPath>
      1. Project Setup (Tasks 1-2)
      2. Base Architecture & Types (Tasks 3-5)
      3. API Client & Repositories (Tasks 6-11)
      4. Redux Stores (Tasks 12-17)
      5. Components (Tasks 18-22)
      6. Pages Migration (Tasks 23-33)
      7. Testing (Tasks 36-40)
      8. Optimization & Deployment (Tasks 41-44, 48, 50)
      9. Documentation (Tasks 45-47)
    </CriticalPath>
    <Priorities>
      <Critical>Tasks 1, 2, 6, 7, 12, 13, 23, 44, 50</Critical>
      <High>Tasks 3-5, 8-11, 14-16, 18-20, 24-30, 34, 36-37, 43, 48</High>
      <Medium>Tasks 17, 21-22, 31-33, 38-42, 45-47</Medium>
      <Low>Tasks 35, 49</Low>
    </Priorities>
  </Summary>
</TASKS>
