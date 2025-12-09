import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import englishTranslations from "./locale/en.json";
import spanishTranslations from "./locale/es.json";
import germanTranslations from "./locale/de.json";
import frenchTranslations from "./locale/fr.json";
import italianTranslations from "./locale/it.json";
import japaneseTranslations from "./locale/ja.json";
import portugueseTranslations from "./locale/pt.json";


const resources = {
  en: {
    translation: englishTranslations
  },
  es: {
    translation: spanishTranslations
  },
  de: {
    translation: germanTranslations
  },
  fr: {
    translation: frenchTranslations
  },
  it: {
    translation: italianTranslations
  },
  ja: {
    translation: japaneseTranslations
  },
  pt: {
    translation: portugueseTranslations
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
