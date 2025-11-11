import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, getCurrentUserAsync } from '@/store/slices';
import { ConfirmationModal } from '@/components/ui';
import { userRepository } from '@/repositories';

type Theme = 'dark' | 'kawaii' | 'sepia' | 'ocean' | 'forest';
type Language = 'en' | 'es' | 'fr' | 'de' | 'it' | 'pt' | 'ja';

interface NotificationSettings {
  followNotificationsEnabled: boolean;
  likeNotificationsEnabled: boolean;
  commentNotificationsEnabled: boolean;
  reviewNotificationsEnabled: boolean;
}

export default function SettingsPage() {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  // Local state for settings (would normally come from user object)
  const [theme, setTheme] = useState<Theme>('dark');
  const [language, setLanguage] = useState<Language>('en');
  const [notificationSettings, setNotificationSettings] = useState<NotificationSettings>({
    followNotificationsEnabled: true,
    likeNotificationsEnabled: true,
    commentNotificationsEnabled: true,
    reviewNotificationsEnabled: true,
  });
  
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [saving, setSaving] = useState(false);

  // Redirect to landing if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    }
  }, [isAuthenticated, router]);

  // Fetch current user settings
  useEffect(() => {
    if (isAuthenticated) {
      dispatch(getCurrentUserAsync());
    }
  }, [isAuthenticated, dispatch]);

  // Initialize settings from current user
  useEffect(() => {
    if (currentUser) {
      // These fields would need to be added to the User model
      // For now, using defaults
      // setTheme(currentUser.theme || 'dark');
      // setLanguage(currentUser.preferredLanguage || 'en');
      // setNotificationSettings({
      //   followNotificationsEnabled: currentUser.followNotificationsEnabled ?? true,
      //   likeNotificationsEnabled: currentUser.likeNotificationsEnabled ?? true,
      //   commentNotificationsEnabled: currentUser.commentNotificationsEnabled ?? true,
      //   reviewNotificationsEnabled: currentUser.reviewNotificationsEnabled ?? true,
      // });
    }
  }, [currentUser]);

  // Handle theme change
  const handleThemeChange = async (newTheme: Theme) => {
    setTheme(newTheme);
    try {
      setSaving(true);
      // TODO: API endpoint to update theme
      // await userRepository.updateSettings({ theme: newTheme });
      console.log('Theme updated to:', newTheme);
    } catch (error) {
      console.error('Failed to update theme:', error);
    } finally {
      setSaving(false);
    }
  };

  // Handle language change
  const handleLanguageChange = async (newLanguage: Language) => {
    setLanguage(newLanguage);
    try {
      setSaving(true);
      // TODO: API endpoint to update language
      // await userRepository.updateSettings({ language: newLanguage });
      console.log('Language updated to:', newLanguage);
    } catch (error) {
      console.error('Failed to update language:', error);
    } finally {
      setSaving(false);
    }
  };

  // Handle notification setting toggle
  const handleNotificationToggle = async (setting: keyof NotificationSettings) => {
    const newValue = !notificationSettings[setting];
    setNotificationSettings((prev) => ({
      ...prev,
      [setting]: newValue,
    }));

    try {
      setSaving(true);
      // TODO: API endpoint to update notification settings
      // await userRepository.updateSettings({ [setting]: newValue });
      console.log(`${setting} updated to:`, newValue);
    } catch (error) {
      console.error(`Failed to update ${setting}:`, error);
      // Revert on error
      setNotificationSettings((prev) => ({
        ...prev,
        [setting]: !newValue,
      }));
    } finally {
      setSaving(false);
    }
  };

  // Handle account deletion
  const handleDeleteAccount = async () => {
    if (!currentUser) return;

    try {
      await userRepository.deleteUser(currentUser.id);
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
                  onChange={(e) => handleThemeChange(e.target.value as Theme)}
                  className="theme-select"
                  disabled={saving}
                >
                  <option value="dark">Dark</option>
                  <option value="kawaii">Kawaii</option>
                  <option value="sepia">Sepia</option>
                  <option value="ocean">Ocean</option>
                  <option value="forest">Forest</option>
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
                  onChange={(e) => handleLanguageChange(e.target.value as Language)}
                  className="theme-select"
                  disabled={saving}
                >
                  <option value="en">English</option>
                  <option value="es">Español</option>
                  <option value="fr">Français</option>
                  <option value="de">Deutsch</option>
                  <option value="it">Italiano</option>
                  <option value="pt">Português</option>
                  <option value="ja">日本語</option>
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
                    checked={notificationSettings.followNotificationsEnabled}
                    onChange={() => handleNotificationToggle('followNotificationsEnabled')}
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
                    checked={notificationSettings.likeNotificationsEnabled}
                    onChange={() => handleNotificationToggle('likeNotificationsEnabled')}
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
                    checked={notificationSettings.commentNotificationsEnabled}
                    onChange={() => handleNotificationToggle('commentNotificationsEnabled')}
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
                    checked={notificationSettings.reviewNotificationsEnabled}
                    onChange={() => handleNotificationToggle('reviewNotificationsEnabled')}
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

