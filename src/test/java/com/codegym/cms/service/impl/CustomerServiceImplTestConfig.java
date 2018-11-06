package com.codegym.cms.service.impl;

import com.codegym.cms.repository.PostRepository;
import com.codegym.cms.service.PostService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerServiceImplTestConfig {

    @Bean
    public PostService customerService(){
        return new PostServiecImpl();
    }

    @Bean
    public PostRepository customerRepository(){
        return Mockito.mock(PostRepository.class);
    }
}
