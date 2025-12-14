package ar.edu.itba.paw.webapp.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LocaleContextFilter extends OncePerRequestFilter {

	private static final Set<String> SUPPORTED_LANGS = Set.of("en", "es");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			Locale locale = resolveLocaleFromAcceptLanguage(request);
			LocaleContextHolder.setLocale(locale);
			filterChain.doFilter(request, response);
		} finally {
			LocaleContextHolder.resetLocaleContext();
		}
	}

	private Locale resolveLocaleFromAcceptLanguage(HttpServletRequest request) {
		final String header = request.getHeader("Accept-Language");
		if (header == null || header.isBlank()) {
			return Locale.ENGLISH;
		}
		return Arrays.stream(header.split(","))
				.map(String::trim)
				.map(langRange -> langRange.split(";")[0])
				.map(tag -> tag.equals("*") ? "en" : tag)
				.map(tag -> tag.split("-")[0].toLowerCase())
				.filter(lang -> SUPPORTED_LANGS.contains(lang))
				.findFirst()
				.map(Locale::forLanguageTag)
				.orElse(Locale.ENGLISH);
	}
}


