package ru.malltshik.trobot;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.tinkoff.invest.openapi.OpenApi;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public abstract class BaseIntegrationTest {

    @Bean
    @Primary
    public OpenApi openApi() {
        return Mockito.mock(OpenApi.class);
    }

}
