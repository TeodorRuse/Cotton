package org.example;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyElasticSearch {

    public static ElasticsearchTransport createTransport(){
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "W23C1U8dywcBZhMDEvOl"));

        RestClientBuilder builder = RestClient.builder(
                        new HttpHost("localhost", 9200))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        RestClient restClient = builder.build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

//        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        return transport;
    }

    public static void addIndex(String index,ElasticsearchClient esClient) throws IOException {
                    esClient.indices().create(c -> c
                    .index(index)
            );
    }

    public static void addDocument(String index, Article article, ElasticsearchClient esClient) throws IOException{
        IndexResponse response = esClient.index(i -> i
                .index(index)
                .id(article.getLinkToArticle())
                .document(article)
        );
    }

    public static void removeIndex(String index, ElasticsearchClient esClient) throws IOException{
                    esClient.indices().delete(d -> d
                    .index(index)
            );
    }

    public static ArrayList<Article> findAll(String index, ElasticsearchClient esClient) throws IOException{
        ArrayList<Article> all = new ArrayList<>();

        SearchResponse<Article> response = esClient.search(s -> s
                        .index(index)
                        .query(q -> q.matchAll(m -> m))
                        .size(1000),
                Article.class
        );

        List<Hit<Article>> hits = response.hits().hits();
        for (Hit<Article> hit : hits) {
            Article article = hit.source();
            all.add(article);
        }
        return all;

//            SearchResponse<JsonData> response = esClient.search(s -> s
//                            .index("articles1")
//                            .query(q -> q.matchAll(m -> m))
//                            .size(1000),
//                    JsonData.class
//            );
//
//            List<Hit<JsonData>> hits = response.hits().hits();
//            for (Hit<JsonData> hit : hits) {
//                System.out.println(hit.source().toJson());
//            }
    }
}
