package com.winter.flashsale.mq;

import com.winter.flashsale.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class MyConfirmCallback implements RabbitTemplate.ConfirmCallback {

    private static final String FLASHSALE_ROUTING = RabbitConfig.TOPIC_ + RabbitConfig.ORDER_KEY;

    private final RabbitTemplate template;

    public MyConfirmCallback(RabbitTemplate template) {
        this.template = template;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        if (correlationData == null) {
            log.error("null correlation data for call back");
            return;
        }

        String correlationId = correlationData.getId();
        String order = MyDataRelation.get(correlationId);

        if (!ack) {
            log.info("confirm NACK for {}, starting retransmission", order);
            CorrelationData retxData = new CorrelationData(UUID.randomUUID().toString());
            MyDataRelation.del(correlationId);
            MyDataRelation.add(retxData.getId(), order);

            template.convertAndSend(RabbitConfig.ORDER_EVENT_EXCHANGE, FLASHSALE_ROUTING, order, retxData);
        } else {
            log.info("confirm ack for " + order);
            MyDataRelation.del(correlationId);
        }
    }
}
