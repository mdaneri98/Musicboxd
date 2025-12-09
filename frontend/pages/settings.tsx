import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, getCurrentUserAsync, deleteUserAsync, updateUserConfigAsync } from '@/store/slices';
import { ConfirmationModal, LanguageSwitcher } from '@/components/ui';
import { ThemeEnum, LanguageEnum } from '@/types';

interface NotificationSettings {
  has_follow_notifications_enabled: boolean;
  has_like_notifications_enabled: boolean;
  has_comments_notifications_enabled: boolean;
  has_reviews_notifications_enabled: boolean;
}

export default function SettingsPage() {
  const { t } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  const [theme, setTheme] = useState<ThemeEnum>(ThemeEnum.DARK);
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
          <h1 className="settings-title">{t('settings.title')}</h1>
          <p className="settings-description">{t('settings.description')}</p>
        </div>

        <div className="settings-content">
          {/* Appearance Section */}
          <section className="settings-section">
            <h2 className="section-title">{t('settings.appearance')}</h2>
            <div className="settings-card">
              <div className="settings-option">
                <div className="option-info">
                  <h3>{t('settings.theme')}</h3>
                  <p>{t('settings.themeDescription')}</p>
                </div>
                <select
                  value={theme}
                  onChange={(e) => handleThemeChange(e.target.value as ThemeEnum)}
                  className="theme-select"
                  disabled={saving}
                >
                  <option value={ThemeEnum.DARK}>{t('settings.themes.dark')}</option>
                  <option value={ThemeEnum.KAWAII}>{t('settings.themes.kawaii')}</option>
                  <option value={ThemeEnum.SEPIA}>{t('settings.themes.sepia')}</option>
                  <option value={ThemeEnum.OCEAN}>{t('settings.themes.ocean')}</option>
                  <option value={ThemeEnum.FOREST}>{t('settings.themes.forest')}</option>
                </select>
              </div>
            </div>
          </section>

          {/* Language Section */}
          <section className="settings-section">
            <h2 className="section-title">{t('settings.language')}</h2>
            <div className="settings-card">
              <LanguageSwitcher onLanguageChange={handleLanguageChange} />
            </div>
          </section>

          {/* Notifications Section */}
          <section className="settings-section">
            <h2 className="section-title">{t('settings.notifications')}</h2>
            <div className="settings-card">
              <div className="settings-option">
                <div className="option-info">
                  <h3>{t('settings.notificationTypes.follows')}</h3>
                  <p>{t('settings.notificationTypes.followsDescription')}</p>
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
                  <h3>{t('settings.notificationTypes.likes')}</h3>
                  <p>{t('settings.notificationTypes.likesDescription')}</p>
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
                  <h3>{t('settings.notificationTypes.comments')}</h3>
                  <p>{t('settings.notificationTypes.commentsDescription')}</p>
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
                  <h3>{t('settings.notificationTypes.reviews')}</h3>
                  <p>{t('settings.notificationTypes.reviewsDescription')}</p>
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
            <h2 className="section-title danger-title">{t('settings.dangerZone')}</h2>
            <div className="settings-card danger-card">
              <div className="settings-option">
                <div className="option-info">
                  <h3>{t('settings.deleteAccount')}</h3>
                  <p>{t('settings.deleteAccountDescription')}</p>
                </div>
                <button
                  onClick={() => setShowDeleteModal(true)}
                  className="btn btn-danger"
                >
                  {t('settings.deleteAccount')}
                </button>
              </div>
            </div>
          </section>
        </div>
      </div>

      {/* Delete Account Confirmation Modal */}
      <ConfirmationModal
        isOpen={showDeleteModal}
        message={t('settings.deleteAccountConfirmation')}
        confirmText={t('settings.deleteAccount')}
        cancelText={t('common.cancel')}
        onConfirm={handleDeleteAccount}
        onCancel={() => setShowDeleteModal(false)}
      />
    </Layout>
  );
}

