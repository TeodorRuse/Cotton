package org.example;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class ArticleSaver {
    public static void main(String[] args) {
        KafkaConsumer consumer = createConsumer("articles_processed",false);
        String esIndex = "articles_embeddings";
        String preProcessTopic = "articles_raw";
        ArrayList<String> sources = new ArrayList<>();
        sources.add("digi24");

        MyElasticSearch esClient = new MyElasticSearch();
        ArticleChecker articleChecker = new ArticleChecker(esClient,esIndex,sources, preProcessTopic);
        articleChecker.start();

        System.out.println("Waiting for articles...");
        while(true){
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(50));

            for (ConsumerRecord<String, String> record : records){
                ObjectMapper mapper = new ObjectMapper();

                try {
                    String rawJson = record.value();
                    System.out.println("Raw JSON Response: " + rawJson);

                    JsonNode node = mapper.readTree(record.value());

                    Article article = new Article(node.get("title").asText(),node.get("link_to_article").asText(),
                            node.get("publish_date").asText(), node.get("publish_time").asText(),
                            node.get("writer").asText(), node.get("link_to_image").asText(),
                            node.get("source").asText(), node.get("text").asText());

                    JsonNode embeddingsNode = node.get("embeddings");
                    double[] embeddings = new double[embeddingsNode.size()];  // Create array for embeddings
                    for (int i = 0; i < embeddingsNode.size(); i++) {
                        embeddings[i] = embeddingsNode.get(i).asDouble();  // Convert each value to double
                    }

                    article.setEmbeddings(embeddings);

                    System.out.println(article);


                    esClient.addDocument(esIndex,article);

                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }

    public static KafkaConsumer createConsumer(String topic, boolean fromBeginning){
        String bootstrapServers = "127.0.0.1:29092";
        String groupId = "group1";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
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

    public static class ArticleChecker extends Thread {
        MyElasticSearch esClient;
        String esIndex;
        ArrayList<String> sources;
        String preProcessTopic;

        public ArticleChecker(MyElasticSearch esClient, String esIndex, ArrayList<String> sources, String preProcessTopic) {
            this.esClient = esClient;
            this.esIndex = esIndex;
            this.sources = sources;
            this.preProcessTopic = preProcessTopic;
        }

        public void run() {
            try {
                while(true) {
                    System.out.println("Searching articles...");
                    ArrayList<Article> latestArticles = esClient.findLatestArticlesBySources(esIndex, sources);
                    if (latestArticles.isEmpty()){
                        System.out.println("No Articles found from: " + "digi24" + "getting first 100");
                        String exePath = "D:\\Extra\\NewsMonitor\\MainServerJava\\Worker\\WebScraper\\scraper.exe";
                        String[] command = {exePath, "digi24", "--send " + preProcessTopic, "--start", "5", "--end", "100"};
                        String commandStr = String.join(" ", command);
                        //System.out.println(commandStr);

                        Process process = Runtime.getRuntime().exec(commandStr);
                        int exitCode = process.waitFor();
                    }
                    for (var article : latestArticles) {
                        System.out.println("Getting all articles from: " + article.getSource() + " since \"" + article.getTitle() + "\"");
                        String exePath = "D:\\Extra\\NewsMonitor\\MainServerJava\\Worker\\WebScraper\\scraper.exe";
                        String[] command = {exePath, article.getSource(), "-a", "--send " + preProcessTopic, "--last_article", "\"" + article.getLinkToArticle() + "\""};
                        String commandStr = String.join(" ", command);
                        //System.out.println(commandStr);

                        Process process = Runtime.getRuntime().exec(commandStr);
                        int exitCode = process.waitFor();
                    }
                     Thread.sleep(600000);
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
