/**
 * Sidebar Component
 * Navigation sidebar with icon-based menu
 * Migrated from: components/sidebar.jsp
 */

import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { useAppSelector } from '@/store/hooks';
import {
  selectIsAuthenticated,
  selectIsModerator,
  selectCurrentUser,
} from '@/store/slices';
import { selectUnreadCount } from '@/store/slices';
import { imageRepository } from '@/repositories';

const Sidebar = () => {
  const { t } = useTranslation();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const isModerator = useAppSelector(selectIsModerator);
  const currentUser = useAppSelector(selectCurrentUser);
  const unreadCount = useAppSelector(selectUnreadCount);

  const profileImageUrl = currentUser && currentUser.image_id
    ? imageRepository.getImageUrl(currentUser.image_id)
    : '/assets/default-avatar.png';

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
            <Link href="/logout" className="sidebar-icon" title={t('navbar.logout')}>
              <i className="fas fa-sign-out-alt"></i>
            </Link>
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

