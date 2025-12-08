import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import englishTranslations from "./locale/en.json";
import spanishTranslations from "./locale/es.json";

const resources = {
  en: {
    translation: englishTranslations
  },
  es: {
    translation: spanishTranslations
  }
};

i18n
  .use(initReactI18next)
  .init({
    resources: resources,
    lng: "en",
    fallbackLng: "en",
    interpolation: {
      escapeValue: false
    }
  });

export default i18n;
