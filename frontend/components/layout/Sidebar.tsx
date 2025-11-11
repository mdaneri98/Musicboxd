/**
 * Sidebar Component
 * Navigation sidebar with icon-based menu
 * Migrated from: components/sidebar.jsp
 */

import Link from 'next/link';
import { useAppSelector } from '@/store/hooks';
import {
  selectIsAuthenticated,
  selectIsModerator,
  selectCurrentUser,
} from '@/store/slices';
import { selectUnreadCount } from '@/store/slices';

const Sidebar = () => {
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const isModerator = useAppSelector(selectIsModerator);
  const currentUser = useAppSelector(selectCurrentUser);
  const unreadCount = useAppSelector(selectUnreadCount);

  const profileImageUrl = currentUser?.imageId
    ? `/api/images/${currentUser.imageId}`
    : '/assets/default-avatar.png';

  return (
    <aside className="sidebar">
      <nav className="sidebar-nav">
        {/* Home */}
        <Link href="/" className="sidebar-icon" title="Home">
          <i className="fas fa-home"></i>
        </Link>

        {/* Music Discovery */}
        <Link href="/music" className="sidebar-icon" title="Music">
          <i className="fas fa-music"></i>
        </Link>

        {/* Search */}
        <Link href="/search" className="sidebar-icon" title="Search">
          <i className="fas fa-search"></i>
        </Link>

        {/* Logged User Section */}
        {isAuthenticated && currentUser && (
          <>
            {/* Notifications */}
            <Link href="/notifications" className="sidebar-icon" title="Notifications">
              <i className="fas fa-bell"></i>
              {unreadCount > 0 && (
                <span className="notification-badge">{unreadCount}</span>
              )}
            </Link>

            {/* Moderator Section */}
            {isModerator && (
              <Link href="/moderator" className="sidebar-icon" title="Moderator">
                <i className="fas fa-plus-square"></i>
              </Link>
            )}

            {/* Profile */}
            <Link
              href={`/users/${currentUser.id}`}
              className="sidebar-icon profile-icon"
              title="Profile"
            >
              <img
                src={profileImageUrl}
                alt="Profile"
                className="profile-image"
              />
            </Link>

            {/* Settings */}
            <Link href="/settings" className="sidebar-icon" title="Settings">
              <i className="fas fa-cog"></i>
            </Link>

            {/* Logout */}
            <Link href="/logout" className="sidebar-icon" title="Logout">
              <i className="fas fa-sign-out-alt"></i>
            </Link>
          </>
        )}

        {/* Anonymous User Section */}
        {!isAuthenticated && (
          <Link href="/login" className="sidebar-icon profile-icon" title="Login">
            <i className="fa-solid fa-right-to-bracket"></i>
          </Link>
        )}
      </nav>
    </aside>
  );
};

export default Sidebar;

