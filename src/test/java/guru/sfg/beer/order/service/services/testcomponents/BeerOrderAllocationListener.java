package guru.sfg.beer.order.service.services.testcomponents;

import guru.sfg.beer.order.service.config.JmsConfiguration;
import guru.sfg.brewery.model.events.AllocateOrderRequest;
import guru.sfg.brewery.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfiguration.ALLOCATE_ORDER_QUEUE )
    public void listen(Message msg){
        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();

        boolean allocationError = false;
        if("allocation-error".equals(request.getBeerOrderDto().getCustomerRef())) {
            allocationError = true;
        }

        final boolean pendingInventory = "allocation-pending".equals(request.getBeerOrderDto().getCustomerRef());
        request.getBeerOrderDto().getBeerOrderLines().forEach( beerOrderLineDto -> {
            if(pendingInventory) {
                beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity()-1);
            } else{
                beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity());
            }
        });

        jmsTemplate.convertAndSend(JmsConfiguration.ALLOCATE_ORDER_RESPONSE_QUEUE,
                AllocateOrderResult.builder()
                    .beerOrderDto(request.getBeerOrderDto())
                        .allocationError(allocationError)
                        .pendingInventory(pendingInventory)
                    .build());
    }
}
