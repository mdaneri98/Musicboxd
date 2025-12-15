import { useTranslation } from 'react-i18next';

const Footer = () => {
  const { t } = useTranslation();
  const currentYear = new Date().getFullYear();

  return (
    <footer className="footer">
      <div className="footer-content">
        <p className="footer-text">
          {t('footer.copyright', { year: currentYear })}
        </p>
      </div>
    </footer>
  );
};

export default Footer;

