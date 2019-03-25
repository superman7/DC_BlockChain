package com.digitalchina.xa.it.kafkaConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digitalchina.xa.it.model.KafkaConsumerBean;

@Component
public class KafkaUtil {
	   @Autowired
	   private KafkaProducer kafkaProducer;
	   
	   public String sendMessage(String topic, String key, KafkaConsumerBean kafkaEntity) {
	    	kafkaProducer.sendMessage(topic, key, kafkaEntity);
	        return "sucess";
	   } 
}
