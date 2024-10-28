package ar.edu.itba.paw.services.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class TimeUtils {

    private static MessageSource messageSource;

    private TimeUtils() {}

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        TimeUtils.messageSource = messageSource;
    }

    public static String formatTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        Duration duration = Duration.between(dateTime, now);

        if (duration.toDays() > 365) {
            return dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy", LocaleContextHolder.getLocale()));
        } else if (duration.toDays() > 30) {
            long months = duration.toDays() / 30;
            return messageSource.getMessage(months == 1 ? "label.month.ago" : "label.months.ago", new Object[]{months}, LocaleContextHolder.getLocale());
        } else if (duration.toDays() > 0) {
            long days = duration.toDays();
            return messageSource.getMessage(days == 1 ? "label.day.ago" : "label.days.ago", new Object[]{days}, LocaleContextHolder.getLocale());
        } else if (duration.toHours() > 0) {
            long hours = duration.toHours();
            return messageSource.getMessage(hours == 1 ? "label.hour.ago" : "label.hours.ago", new Object[]{hours}, LocaleContextHolder.getLocale());
        } else if (duration.toMinutes() > 0) {
            long minutes = duration.toMinutes();
            return messageSource.getMessage(minutes == 1 ? "label.minute.ago" : "label.minutes.ago", new Object[]{minutes}, LocaleContextHolder.getLocale());
        } else {
            return messageSource.getMessage("label.just.now", null, LocaleContextHolder.getLocale());
        }
    }
}
