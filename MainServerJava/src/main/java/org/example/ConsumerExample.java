package org.example;


import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;


public class ConsumerExample {
    public static void main(String[] args) {
        KafkaConsumer<String,String> consumer = createConsumer("articles3",true);

        while(true){
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(50));

            for (ConsumerRecord<String, String> record : records){
                ObjectMapper mapper = new ObjectMapper();

                try {
                    JsonNode node = mapper.readTree(record.value());

                    Article article = new Article(node.get("title").asText(),node.get("link_to_article").asText(),
                            node.get("publish_date").asText(), node.get("publish_time").asText(),
                            node.get("writer").asText(), node.get("link_to_image").asText(),
                            node.get("source").asText(), node.get("text").asText());
                    System.out.println(article);

                } catch (JsonMappingException e) {
                    throw new RuntimeException(e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static KafkaConsumer createConsumer(String topic, boolean fromBeginning){
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "group1";
        //String topic = "articles3";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer .class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(Arrays.asList(topic));

        if(fromBeginning) {
            consumer.poll(0);
            consumer.seekToBeginning(consumer.assignment());
        }
        return consumer;
    }

}
