package guru.sfg.beer.order.service.services.testcomponents;

import guru.sfg.beer.order.service.config.JmsConfiguration;
import guru.sfg.brewery.model.events.ValidateOrderRequest;
import guru.sfg.brewery.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfiguration.VALIDATE_ORDER_QUEUE)
    public void list(Message message) {

        ValidateOrderRequest request = (ValidateOrderRequest) message.getPayload();

        boolean isValid = true;
        boolean sendResponse = true;
        if ("validation-fail".equals(request.getBeerOrder().getCustomerRef())) {
            isValid = false;
        }
        else if ("dont-validate".equals(request.getBeerOrder().getCustomerRef())){
            sendResponse = false;
        }

        if(sendResponse) {
            jmsTemplate.convertAndSend(JmsConfiguration.VALIDATE_ORDER_RESPONSE_QUEUE,
                    ValidateOrderResult.builder()
                            .isValid(isValid)
                            .orderId(request.getBeerOrder().getId())
                            .build());
        }
    }
}
