import { useEffect } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import {
  selectIsAuthenticated,
  selectIsModerator,
  selectCurrentUser,
  logoutAsync,
  selectUnreadCount,
  fetchUnreadCountAsync,
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import { ASSETS } from '@/utils';

const Sidebar = () => {
  const { t, i18n } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const isModerator = useAppSelector(selectIsModerator);
  const currentUser = useAppSelector(selectCurrentUser);
  const unreadCount = useAppSelector(selectUnreadCount);

  // Fetch unread notification count when authenticated
  useEffect(() => {
    if (isAuthenticated) {
      dispatch(fetchUnreadCountAsync());
    }
  }, [isAuthenticated, dispatch]);

  const handleLogout = async () => {
    await dispatch(logoutAsync());
    // Reset language to English after logout to avoid keeping the logged user's language cached
    i18n.changeLanguage('en');
    router.push('/');
  };

  const profileImageUrl = currentUser && currentUser.image_id
    ? imageRepository.getImageUrl(currentUser.image_id)
    : ASSETS.DEFAULT_AVATAR;

  return (
    <aside className="sidebar">
      <nav className="sidebar-nav">
        {/* Home */}
        <Link href="/" className="sidebar-icon" title={t('navbar.home')}>
          <i className="fas fa-home"></i>
        </Link>

        {/* Music Discovery */}
        <Link href="/music" className="sidebar-icon" title={t('navbar.discovery')}>
          <i className="fas fa-music"></i>
        </Link>

        {/* Search */}
        <Link href="/search" className="sidebar-icon" title={t('navbar.search')}>
          <i className="fas fa-search"></i>
        </Link>

        {/* Logged User Section */}
        {isAuthenticated && currentUser && (
          <>
            {/* Notifications */}
            <Link href="/notifications" className="sidebar-icon" title={t('navbar.notifications')}>
              <i className="fas fa-bell"></i>
              {unreadCount > 0 && (
                <span className="notification-badge">{unreadCount}</span>
              )}
            </Link>

            {/* Moderator Section */}
            {isModerator && (
              <Link href="/moderator" className="sidebar-icon" title={t('navbar.moderator', 'Moderator')}>
                <i className="fas fa-plus-square"></i>
              </Link>
            )}

            {/* Profile */}
            <Link
              href={`/users/${currentUser.id}`}
              className="sidebar-icon profile-icon"
              title={t('navbar.profile')}
            >
              <img
                src={profileImageUrl}
                alt={t('navbar.profile')}
                className="profile-image"
              />
            </Link>

            {/* Settings */}
            <Link href="/settings" className="sidebar-icon" title={t('navbar.settings')}>
              <i className="fas fa-cog"></i>
            </Link>

            {/* Logout */}
            <button onClick={handleLogout} className="sidebar-icon" title={t('navbar.logout')}>
              <i className="fas fa-sign-out-alt"></i>
            </button>
          </>
        )}

        {/* Anonymous User Section */}
        {!isAuthenticated && (
          <Link href="/login" className="sidebar-icon profile-icon" title={t('navbar.login')}>
            <i className="fa-solid fa-right-to-bracket"></i>
          </Link>
        )}
      </nav>
    </aside>
  );
};

export default Sidebar;

