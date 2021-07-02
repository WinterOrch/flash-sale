package com.winter.flashsale.mq;

import org.springframework.amqp.core.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String FLASHSALE_QUEUE = "flashsale.queue";

    public static final String EXCHANGE_TOPIC = "exchange_topic";

    public static final String FLASHSALE_MESSAGE = "flashsale_mess";

    public static final String FLASHSALE_TEST = "flashsale_test";

    public static final String TOPIC_ = "topic.";
    public static final String TOPIC_KEY1 = "key1";
    public static final String TOPIC_KEY2 = "#";

    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String HEADER_QUEUE = "header.queue";

    public static final String TOPIC_EXCHANGE = "topic_exchange";
    public static final String FANOUT_EXCHANGE = "fanout_exchange";
    public static final String HEADERS_EXCHANGE = "headers_exchange";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    /**
     * Topic
     */
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);
    }
    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);
    }
    @Bean
    public TopicExchange topicExc() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExc()).with(TOPIC_ + TOPIC_KEY1);
    }
    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExc()).with(TOPIC_ + TOPIC_KEY2);
    }

    /**
     * Fanout
     */
    @Bean
    public FanoutExchange fanoutExc() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExc());
    }
    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExc());
    }

    /**
     * Header
     */
    @Bean
    public static HeadersExchange headersExc() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headerQueue() {
        return new Queue(HEADER_QUEUE, true);
    }
    @Bean
    public Binding headerBinding() {
        Map<String, Object> map = new HashMap<>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        return BindingBuilder.bind(headerQueue()).to(headersExc()).whereAll(map).match();
    }

}
