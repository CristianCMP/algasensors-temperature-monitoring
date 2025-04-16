package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private static final String PROCESS_TEMPERATURE = "temperature-monitoring.process-temperature.v1";
    public static final String QUEUE_PROCESS_TEMPERATURE = PROCESS_TEMPERATURE + ".q";
    public static final String DEAD_LETTER_PROCESS_TEMPERATURE = PROCESS_TEMPERATURE + ".dlq";
    public static final String QUEUE_ALERTING = "temperature-monitoring.alerting.v1.q";

    //CONFIGURAÇÃO EM GERAL
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    public FanoutExchange exchange() {
        return ExchangeBuilder.fanoutExchange("temperature-processing.temperature-received.v1.e").build();
    }

    //PROCESS TEMPERATURE
    @Bean
    public Queue queueProcessTemperature() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "");
        args.put("x-dead-letter-routing-key", DEAD_LETTER_PROCESS_TEMPERATURE);
        return QueueBuilder.durable(QUEUE_PROCESS_TEMPERATURE).withArguments(args).build();
    }
    @Bean
    public Queue deadLetterQueueProcessTemperature() {
        return QueueBuilder.durable(DEAD_LETTER_PROCESS_TEMPERATURE).build();
    }

    @Bean
    public Binding bindingProcessTemperature() {
        return BindingBuilder.bind(queueProcessTemperature()).to(exchange());
    }

    //ALERTING
    @Bean
    public Queue queueAlerting() {
        return QueueBuilder.durable(QUEUE_ALERTING).build();
    }

    @Bean
    public Binding bindingAlerting() {
        return BindingBuilder.bind(queueAlerting()).to(exchange());
    }
}
