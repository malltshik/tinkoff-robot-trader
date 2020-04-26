package ru.malltshik.trobot.trading;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.malltshik.trobot.BaseIT;
import ru.malltshik.trobot.persistance.entities.TraderConfig;
import ru.malltshik.trobot.persistance.repositories.TraderConfigRepository;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@Transactional
@Sql("classpath:/sql/create_trader_config.sql")
public class TraderManagerIT extends BaseIT {

    @Autowired
    private TraderManager manager;
    @Autowired
    private TraderConfigRepository repository;

    @Test(expected = NullPointerException.class)
    public void registerWithoutConfigIdTest() {
        TraderConfig config = TraderConfig.builder().figi("200").build();
        manager.register(config);
    }

    @Test
    public void registerTest() {
        TraderConfig config = TraderConfig.builder().figi("200").build();
        config = repository.save(config);
        Trader trader = manager.register(config);
        asserts(config, trader);
    }

    @Test
    public void findOneTest() {
        TraderConfig config = repository.getOne(1L);
        Trader trader = manager.findOne(config.getId()).orElse(null);
        asserts(config, trader);
    }

    @Test
    public void getAll() {
        TraderConfig config = repository.findAll().get(0);
        List<Trader> traders = manager.getAll();
        assertThat(traders, hasSize(1));
        asserts(config, traders.get(0));
    }

    @Test
    public void unregister() {
        manager.unregister(1L);
        assertFalse(manager.findOne(1L).isPresent());
    }

    private void asserts(TraderConfig config, Trader trader) {
        assertNotNull(trader);
        assertNotNull(trader.getState());
        assertNotNull(trader.getState().getInstrument());
        assertEquals(config.getFigi(), trader.getState().getFigi());
        assertEquals(config.getId(), trader.getState().getId());
        assertEquals(config.getFigi(), trader.getState().getFigi());
        assertFalse(trader.isRunning());
    }

}