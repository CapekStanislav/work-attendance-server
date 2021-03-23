package cz.stanislavcapek.workattendancerest.v1.appconfig;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * An instance of class {@code CustomRepositoryRestConfigurer}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Component
public class CustomRepositoryRestConfigurer implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        String[] allowedMethods = {"GET", "POST","HEAD", "PATCH", "OPTIONS"};
        cors.addMapping("/**")
                .allowedMethods(allowedMethods)
                .allowCredentials(false).maxAge(3600);
    }
}
