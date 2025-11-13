package ar.edu.itba.paw.api.filter;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Provider
@Component
public class AcceptLanguageFilter implements ContainerRequestFilter {

	private static final Set<String> SUPPORTED_LANGS = Set.of("en", "es");

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		final List<Locale> acceptable = requestContext.getAcceptableLanguages();

		final Locale selectedLocale = acceptable.stream()
				.map(locale -> Locale.forLanguageTag(locale.getLanguage().toLowerCase()))
				.filter(locale -> SUPPORTED_LANGS.contains(locale.getLanguage()))
				.findFirst()
				.orElse(Locale.ENGLISH);

		LocaleContextHolder.setLocale(selectedLocale);
	}
}


