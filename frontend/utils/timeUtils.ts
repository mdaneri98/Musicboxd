import i18n from '../i18n';

/**
 * Formats a date as a relative time string (e.g., "5 minutes ago", "2 days ago")
 * Uses i18n for localized strings
 * @param dateTime - The date to format (Date object or ISO string)
 * @returns Localized relative time string
 */
export const formatTimeAgo = (dateTime: Date | string): string => {
  const date = typeof dateTime === 'string' ? new Date(dateTime) : dateTime;
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  
  const minutes = Math.floor(diffMs / (1000 * 60));
  const hours = Math.floor(diffMs / (1000 * 60 * 60));
  const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  const months = Math.floor(days / 30);
  
  if (days > 365) {
    // For dates older than a year, show formatted date
    const day = date.getDate();
    const monthKey = `time.months.${date.getMonth() + 1}`;
    const year = date.getFullYear();
    const monthName = i18n.t(monthKey);
    return `${monthName} ${day}, ${year}`;
  } else if (months > 0) {
    return months === 1 
      ? i18n.t('time.monthAgo', { count: months })
      : i18n.t('time.monthsAgo', { count: months });
  } else if (days > 0) {
    return days === 1 
      ? i18n.t('time.dayAgo', { count: days })
      : i18n.t('time.daysAgo', { count: days });
  } else if (hours > 0) {
    return hours === 1 
      ? i18n.t('time.hourAgo', { count: hours })
      : i18n.t('time.hoursAgo', { count: hours });
  } else if (minutes > 0) {
    return minutes === 1 
      ? i18n.t('time.minuteAgo', { count: minutes })
      : i18n.t('time.minutesAgo', { count: minutes });
  } else {
    return i18n.t('time.justNow');
  }
};

/**
 * Formats a date in a localized format (e.g., "15 January, 2024")
 * Uses i18n for localized strings
 * @param date - The date to format (Date object, ISO string, or null)
 * @returns Localized date string
 */
export const formatDate = (date: Date | string | null): string => {
  if (!date) {
    return i18n.t('time.unknown');
  }
  
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  const day = dateObj.getDate();
  const monthKey = `time.months.${dateObj.getMonth() + 1}`;
  const year = dateObj.getFullYear();
  const monthName = i18n.t(monthKey);
  
  return i18n.t('time.dateFormat', { day, month: monthName, year });
};
