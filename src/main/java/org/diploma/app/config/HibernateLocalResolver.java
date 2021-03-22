package org.diploma.app.config;

import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolverContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class HibernateLocalResolver implements LocaleResolver {

    @Override
    public Locale resolve(LocaleResolverContext localeResolverContext) {
        return LocaleContextHolder.getLocale();
    }
}
