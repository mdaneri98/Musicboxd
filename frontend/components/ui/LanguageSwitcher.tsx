/**
 * Language Switcher Component
 * Allows users to change the application language
 */

import { useTranslation } from 'react-i18next';
import { Language } from '@/types/enums';

interface LanguageSwitcherProps {
  onLanguageChange?: (language: Language) => void;
}

const LanguageSwitcher = ({ onLanguageChange }: LanguageSwitcherProps) => {
  const { i18n, t } = useTranslation();

  const changeLanguage = (lng: string) => {
    onLanguageChange?.(lng as Language);
  };

  return (
    <>
      <div className="option-info">
        <h3>{t('settings.language')}</h3>
        <p>{t('settings.languageDescription')}</p>
      </div>
      <select
        id="language-select"
        value={i18n.language}
        onChange={(e) => changeLanguage(e.target.value)}
        className="theme-select"
      >
        <option value={Language.EN}>{t('settings.languages.en')}</option>
        <option value={Language.ES}>{t('settings.languages.es')}</option>
        <option value={Language.DE}>{t('settings.languages.de')}</option>
        <option value={Language.FR}>{t('settings.languages.fr')}</option>
        <option value={Language.IT}>{t('settings.languages.it')}</option>
        <option value={Language.JA}>{t('settings.languages.ja')}</option>
        <option value={Language.PT}>{t('settings.languages.pt')}</option>
      </select>
    </>
  );
};

export default LanguageSwitcher;
