package ar.edu.itba.paw.webapp.config;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

@EnableScheduling
@EnableAsync(proxyTargetClass = true)
@EnableTransactionManagement
@ComponentScan({
        "ar.edu.itba.paw.webapp",
        "ar.edu.itba.paw.services",
        "ar.edu.itba.paw.persistence",
})
@PropertySource("classpath:application.properties")
@Configuration
public class ApiConfig {

    @Autowired
    private Environment environment;

    @Value("classpath:schemas.sql")
    private Resource schemaSql;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(
                "i18n/messages",     // Para mensajes generales de la aplicación
                "i18n/mail_messages" // Para mensajes de email
        );
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriverClass(org.postgresql.Driver.class);

        ds.setUrl(environment.getProperty("database.url"));
        ds.setUsername(environment.getProperty("database.username"));
        ds.setPassword(environment.getProperty("database.password"));
        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ar.edu.itba.paw.models");
        factoryBean.setDataSource(dataSource());

        final JpaVendorAdapter jpaAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(jpaAdapter);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");

        factoryBean.setJpaProperties(properties);

        return factoryBean;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource ds) {
        final DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(ds);
        dsi.setDatabasePopulator(databasePopulator());
        return dsi;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaSql);
        return populator;
    }

}