package ar.edu.itba.paw.api.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

@Configuration
public class ApiI18nConfig {

	@Bean
	public MessageSource messageSource() {
		final ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
		ms.setBasename("classpath:i18n/messages");
		ms.setDefaultEncoding(StandardCharsets.UTF_8.name());
		ms.setCacheSeconds(5);
		ms.setFallbackToSystemLocale(false);
		return ms;
	}
}


