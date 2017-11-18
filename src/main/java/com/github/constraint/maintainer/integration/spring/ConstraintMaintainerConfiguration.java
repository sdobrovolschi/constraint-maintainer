package com.github.constraint.maintainer.integration.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;

/**
 * @author Stanislav Dobrovolschi
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ConstraintMaintainerConfiguration implements ImportAware {

    private AnnotationAttributes enableConstraintMaintainer;

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public SpringConstraintMaintainer constraintMaintainer(DataSource dataSource, @Value("${constraint.maintainer.default-schema:#{null}}") String schemaName) {
        SpringConstraintMaintainer constraintMaintainer = new SpringConstraintMaintainer(dataSource);
        constraintMaintainer.setSchemaName(schemaName);

        constraintMaintainer.setDisablingMode(enableConstraintMaintainer.getEnum("value"));
        return constraintMaintainer;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        enableConstraintMaintainer = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableConstraintMaintainer.class.getName(), false));
        if (enableConstraintMaintainer == null) {
            throw new IllegalArgumentException(
                    "@EnableConstraintMaintainer is not present on importing class " + importMetadata.getClassName());
        }
    }
}
