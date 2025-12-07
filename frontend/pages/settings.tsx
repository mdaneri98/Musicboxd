import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, getCurrentUserAsync, deleteUserAsync, updateUserConfigAsync } from '@/store/slices';
import { ConfirmationModal } from '@/components/ui';
import { ThemeEnum, LanguageEnum } from '@/types';

interface NotificationSettings {
  has_follow_notifications_enabled: boolean;
  has_like_notifications_enabled: boolean;
  has_comments_notifications_enabled: boolean;
  has_reviews_notifications_enabled: boolean;
}

export default function SettingsPage() {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  const [theme, setTheme] = useState<ThemeEnum>(ThemeEnum.DARK);
  const [language, setLanguage] = useState<LanguageEnum>(LanguageEnum.EN);
  const [notificationSettings, setNotificationSettings] = useState<NotificationSettings>();
  
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    } else dispatch(getCurrentUserAsync());
  }, [isAuthenticated, router, dispatch]);

  useEffect(() => {
    if (currentUser) {
      setNotificationSettings({
        has_follow_notifications_enabled: currentUser.has_follow_notifications_enabled,
        has_like_notifications_enabled: currentUser.has_like_notifications_enabled,
        has_comments_notifications_enabled: currentUser.has_comments_notifications_enabled,
        has_reviews_notifications_enabled: currentUser.has_reviews_notifications_enabled
      });
      setTheme(currentUser.preferred_theme);
      setLanguage(currentUser.preferred_language);
    }
  }, [currentUser]);

  const handleThemeChange = async (newTheme: ThemeEnum) => {
    setTheme(newTheme);
    try {
      setSaving(true);
      if (!currentUser) return;
      await dispatch(updateUserConfigAsync({ userId: currentUser.id, userData: { preferred_theme: newTheme } })).unwrap();
    } catch (error) {
      console.error('Failed to update theme:', error);
    } finally {
      setSaving(false);
    }
  };

  const handleLanguageChange = async (newLanguage: LanguageEnum) => {
    setLanguage(newLanguage);
    try {
      setSaving(true);
      if (!currentUser) return;
      await dispatch(updateUserConfigAsync({ userId: currentUser.id, userData: { preferred_language: newLanguage } })).unwrap();
    } catch (error) {
      console.error('Failed to update language:', error);
    } finally {
      setSaving(false);
    }
  };

  const handleNotificationToggle = async (setting: keyof NotificationSettings) => {
    const newValue = !notificationSettings?.[setting];
    setNotificationSettings((prev) => {
      if (!prev) return prev;
      return { ...prev, [setting]: newValue };
    });

    try {
      if (!currentUser) return;
      await dispatch(updateUserConfigAsync({ userId: currentUser.id, userData: { [setting]: newValue } })).unwrap();
    } catch (error) {
      console.error(`Failed to update ${setting}:`, error);
    }
  };

  // Handle account deletion
  const handleDeleteAccount = async () => {
    if (!currentUser) return;

    try {
      await dispatch(deleteUserAsync(currentUser.id)).unwrap();
      router.push('/landing');
    } catch (error) {
      console.error('Failed to delete account:', error);
    }
  };

  if (!isAuthenticated) {
    return null; // Will redirect in useEffect
  }

  return (
    <Layout title="Settings - Musicboxd">
      <div className="settings-container">
        <div className="settings-header">
          <h1 className="settings-title">Settings</h1>
          <p className="settings-description">Manage your account preferences and settings</p>
        </div>

        <div className="settings-content">
          {/* Appearance Section */}
          <section className="settings-section">
            <h2 className="section-title">Appearance</h2>
            <div className="settings-card">
              <div className="settings-option">
                <div className="option-info">
                  <h3>Theme</h3>
                  <p>Choose your preferred color theme</p>
                </div>
                <select
                  value={theme}
                  onChange={(e) => handleThemeChange(e.target.value as ThemeEnum)}
                  className="theme-select"
                  disabled={saving}
                >
                  <option value={ThemeEnum.DARK}>Dark</option>
                  <option value={ThemeEnum.KAWAII}>Kawaii</option>
                  <option value={ThemeEnum.SEPIA}>Sepia</option>
                  <option value={ThemeEnum.OCEAN}>Ocean</option>
                  <option value={ThemeEnum.FOREST}>Forest</option>
                </select>
              </div>
            </div>
          </section>

          {/* Language Section */}
          <section className="settings-section">
            <h2 className="section-title">Language</h2>
            <div className="settings-card">
              <div className="settings-option">
                <div className="option-info">
                  <h3>Select Language</h3>
                  <p>Choose your preferred language</p>
                </div>
                <select
                  value={language}
                  onChange={(e) => handleLanguageChange(e.target.value as LanguageEnum)}
                  className="theme-select"
                  disabled={saving}
                >
                  <option value={LanguageEnum.EN}>English</option>
                  <option value={LanguageEnum.ES}>Español</option>
                  <option value={LanguageEnum.FR}>Français</option>
                  <option value={LanguageEnum.DE}>Deutsch</option>
                  <option value={LanguageEnum.IT}>Italiano</option>
                  <option value={LanguageEnum.PT}>Português</option>
                  <option value={LanguageEnum.JA}>日本語</option>
                </select>
              </div>
            </div>
          </section>

          {/* Notifications Section */}
          <section className="settings-section">
            <h2 className="section-title">Notifications</h2>
            <div className="settings-card">
              <div className="settings-option">
                <div className="option-info">
                  <h3>Follows</h3>
                  <p>Get notified when someone follows you</p>
                </div>
                <div className="toggle-switch">
                  <input
                    type="checkbox"
                    id="followNotif"
                    className="toggle-input"
                    checked={notificationSettings?.has_follow_notifications_enabled}
                    onChange={() => handleNotificationToggle('has_follow_notifications_enabled')}
                    disabled={saving}
                  />
                  <label htmlFor="followNotif" className="toggle-label"></label>
                </div>
              </div>

              <div className="settings-option">
                <div className="option-info">
                  <h3>Likes</h3>
                  <p>Get notified when someone likes your review</p>
                </div>
                <div className="toggle-switch">
                  <input
                    type="checkbox"
                    id="likeNotif"
                    className="toggle-input"
                    checked={notificationSettings?.has_like_notifications_enabled}
                    onChange={() => handleNotificationToggle('has_like_notifications_enabled')}
                    disabled={saving}
                  />
                  <label htmlFor="likeNotif" className="toggle-label"></label>
                </div>
              </div>

              <div className="settings-option">
                <div className="option-info">
                  <h3>Comments</h3>
                  <p>Get notified when someone comments on your review</p>
                </div>
                <div className="toggle-switch">
                  <input
                    type="checkbox"
                    id="commentNotif"
                    className="toggle-input"
                    checked={notificationSettings?.has_comments_notifications_enabled}
                    onChange={() => handleNotificationToggle('has_comments_notifications_enabled')}
                    disabled={saving}
                  />
                  <label htmlFor="commentNotif" className="toggle-label"></label>
                </div>
              </div>

              <div className="settings-option">
                <div className="option-info">
                  <h3>Reviews</h3>
                  <p>Get notified when followed users post a review</p>
                </div>
                <div className="toggle-switch">
                  <input
                    type="checkbox"
                    id="reviewNotif"
                    className="toggle-input"
                    checked={notificationSettings?.has_reviews_notifications_enabled}
                    onChange={() => handleNotificationToggle('has_reviews_notifications_enabled')}
                    disabled={saving}
                  />
                  <label htmlFor="reviewNotif" className="toggle-label"></label>
                </div>
              </div>
            </div>
          </section>

          {/* Danger Zone */}
          <section className="settings-section">
            <h2 className="section-title danger-title">Danger Zone</h2>
            <div className="settings-card danger-card">
              <div className="settings-option">
                <div className="option-info">
                  <h3>Delete Account</h3>
                  <p>Permanently delete your account and all associated data</p>
                </div>
                <button
                  onClick={() => setShowDeleteModal(true)}
                  className="btn btn-danger"
                >
                  Delete Account
                </button>
              </div>
            </div>
          </section>
        </div>
      </div>

      {/* Delete Account Confirmation Modal */}
      <ConfirmationModal
        isOpen={showDeleteModal}
        message="Are you sure you want to delete your account? This action cannot be undone and all your data will be permanently deleted."
        confirmText="Delete Account"
        cancelText="Cancel"
        onConfirm={handleDeleteAccount}
        onCancel={() => setShowDeleteModal(false)}
      />
    </Layout>
  );
}

