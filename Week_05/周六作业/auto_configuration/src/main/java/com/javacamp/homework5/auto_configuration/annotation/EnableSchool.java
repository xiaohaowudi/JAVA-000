package com.javacamp.homework5.auto_configuration.annotation;


import com.javacamp.homework5.auto_configuration.configuration.SchoolConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SchoolConfiguration.class)
public @interface EnableSchool {

}
