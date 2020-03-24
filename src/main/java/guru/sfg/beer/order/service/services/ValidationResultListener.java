package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.config.JmsConfiguration;
import guru.sfg.brewery.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfiguration.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult result) {
        final UUID beerOrderId = result.getId();
        log.debug("Validation order result for Order id:" + beerOrderId);
        beerOrderManager.processValidationResult(beerOrderId, result.getIsValid());
    }
}
