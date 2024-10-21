package org.example;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminComands {

    public static void main(String[] args){
        MyElasticSearch esClient = new MyElasticSearch();
//        changeDate(esClient);
//        try {
//            addMapping(esClient);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        moveToIndex(esClient);

    }

    public static void changeDate(MyElasticSearch es){
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            Iterable<Article> articles = es.findAll("articles2");
            for(var art: articles){

                LocalDate date = LocalDate.parse(art.getPublishDate(), inputFormatter);
                String formattedDate = date.format(outputFormatter);

                art.setPublishDate(formattedDate);
                es.addDocument("articles2",art);

                System.out.println(formattedDate);
            }


        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void moveToIndex(MyElasticSearch es) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            Iterable<Article> articles = es.findAll("articles2");
            for(var art: articles){

                LocalDate date = LocalDate.parse(art.getPublishDate(), inputFormatter);
                String formattedDate = date.format(outputFormatter);

                art.setPublishDate(formattedDate);
                es.addDocument("articles3",art);

                System.out.println(art.getTitle());
            }


        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void addMapping(MyElasticSearch es) throws IOException {
        Map<String, Property> mappings = new HashMap<>();
        mappings.put("title", Property.of(p -> p.text(t -> t)));
        mappings.put("linkToArticle", Property.of(p -> p.keyword(k -> k)));
        mappings.put("publishDate", Property.of(p -> p.date(d -> d.format("dd-MM-yyyy"))));
        mappings.put("publishTime", Property.of(p -> p.text(t -> t)));
        mappings.put("writer", Property.of(p -> p.keyword(k -> k)));
        mappings.put("linkToImage", Property.of(p -> p.keyword(k -> k)));
        mappings.put("source", Property.of(p -> p.keyword(k -> k)));
        mappings.put("text", Property.of(p -> p.text(t -> t)));
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index("articles3")
                .mappings(m -> m.properties(mappings))
                .build();

        CreateIndexResponse createIndexResponse = es.getEsClient().indices().create(createIndexRequest);

        System.out.println("Index Created: " + createIndexResponse.acknowledged());
    }
}
