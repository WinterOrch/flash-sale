package com.winter.flashsale.mq;

import com.winter.common.message.FlashSaleOrderMessage;
import com.winter.common.message.QueueMessage;
import com.winter.common.utils.StringUtils;
import com.winter.flashsale.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class Sender {

    private static final Logger log = LoggerFactory.getLogger(Sender.class);

    RabbitTemplate rabbitTemplate;

    public Sender(RabbitTemplate rabbitTemplate, MyConfirmCallback myConfirmCallback) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setConfirmCallback(myConfirmCallback);
        this.rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
                log.error("Return Callback! exchange: {}, replyCode: {}, routingKey: {} for message: {}",
                exchange, replyCode, routingKey, message));
    }

    public void sendDirectFlashSaleOrder(FlashSaleOrderMessage msg) {
        String strMsg = StringUtils.bean2String(new QueueMessage<>(msg));
        log.info("send message: " + strMsg);

        CorrelationData correlationData = getCorrelationData();
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EVENT_EXCHANGE, RabbitConfig.FLASHSALE_ROUTING, strMsg, correlationData);
    }

    private CorrelationData getCorrelationData() {
        return new CorrelationData(UUID.randomUUID().toString());
    }

}
