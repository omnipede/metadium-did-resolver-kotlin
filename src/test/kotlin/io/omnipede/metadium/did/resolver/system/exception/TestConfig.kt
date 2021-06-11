package io.omnipede.metadium.did.resolver.system.exception

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc // 주의! 반드시 EnableWebMvc 어노테이션이 존재해야 한다
class TestConfig {

    /**
     * ConstraintViolationException 을 테스트하기 위해 mock mvc 에 아래 bean 을 넣어주어야 한다
     */
    @Bean
    fun methodValidationPostProcessor(): MethodValidationPostProcessor {
        return MethodValidationPostProcessor()
    }
}
