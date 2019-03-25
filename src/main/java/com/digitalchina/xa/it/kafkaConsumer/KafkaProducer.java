package com.digitalchina.xa.it.kafkaConsumer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.digitalchina.xa.it.model.KafkaConsumerBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate; 
    
    private Gson gson = new GsonBuilder().create();

    
    public void sendMessage(String topic, String key, KafkaConsumerBean kafkaEntity){
        //测试发送对象数据
        kafkaTemplate.send(new ProducerRecord(topic, key, gson.toJson(kafkaEntity))); 
    }
}