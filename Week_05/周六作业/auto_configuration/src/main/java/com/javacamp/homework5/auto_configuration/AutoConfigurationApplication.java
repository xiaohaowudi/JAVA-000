package com.javacamp.homework5.auto_configuration;

import com.javacamp.homework5.auto_configuration.model.Klass;
import com.javacamp.homework5.auto_configuration.model.School;
import com.javacamp.homework5.auto_configuration.model.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AutoConfigurationApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(AutoConfigurationApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);

		try {
			Student stu1 = context.getBean("student1", Student.class);
			Student stu2 = context.getBean("student2", Student.class);
			Klass klass = context.getBean("klass", Klass.class);
			School school = context.getBean("school", School.class);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.close();
		}
	}

}
