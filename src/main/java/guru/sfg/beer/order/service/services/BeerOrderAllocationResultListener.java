package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.config.JmsConfiguration;
import guru.sfg.brewery.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderAllocationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfiguration.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result) {
        log.debug("Allocation order result for Order id:" + result.getBeerOrder().getId());

        if(result.getAllocationError()) {
            beerOrderManager.beerOrderAllocationFailed(result.getBeerOrder());
        } else if( result.getPendingInventory()) {
            this.beerOrderManager.beerOrderAllocationPendingInventory(result.getBeerOrder());
        } else {
            this.beerOrderManager.beerOrderAllocationPassed(result.getBeerOrder());
        }
    }
}
