package ru.malltshik.trobot.services.trader;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import ru.malltshik.trobot.BaseIT;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TraderServiceFactoryIT extends BaseIT {

    @Autowired
    private TraderServiceFactory traderServiceFactory;

    @Test
    public void t01_register() {
        TraderService trader = traderServiceFactory.register("BBG005DXJS36");
        assertNotNull(trader);
        assertEquals(trader.getStatus(), TraderService.Status.DOWN);
    }

    @Test
    public void t02_findOne() {
        Optional<TraderService> optional = traderServiceFactory.findOne("BBG005DXJS36");
        assertTrue(optional.isPresent());
        TraderService trader = optional.get();
        assertEquals(trader.getStatus(), TraderService.Status.DOWN);
        trader.start();
        optional = traderServiceFactory.findOne("BBG005DXJS36");
        assertTrue(optional.isPresent());
        trader = optional.get();
        assertEquals(trader.getStatus(), TraderService.Status.UP);
    }

    @Test
    public void t03_unregister() {
        Optional<TraderService> optional = traderServiceFactory.unregister("BBG005DXJS36");
        assertTrue(optional.isPresent());
        TraderService trader = optional.get();
        assertEquals(trader.getStatus(), TraderService.Status.DOWN);
    }


}