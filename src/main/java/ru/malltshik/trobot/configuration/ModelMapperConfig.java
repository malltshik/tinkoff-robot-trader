package ru.malltshik.trobot.configuration;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Configuration
public class ModelMapperConfig {

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ModelMapper modelMapper(List<Converter<?, ?>> converters) {
        ModelMapper mapper = new ModelMapper();
        if (!CollectionUtils.isEmpty(converters)) {
            converters.forEach(mapper::addConverter);
        }
        return mapper;
    }

}
