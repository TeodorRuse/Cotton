package org.example;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import co.elastic.clients.elasticsearch._types.SortOrder;

import javax.net.ssl.SSLContext;

@Component
public class MyElasticSearch {

    private ElasticsearchTransport transport;
    private ElasticsearchClient esClient;

    private final int pageSize = 20;

//    public MyElasticSearch(){
//        final CredentialsProvider credentialsProvider =
//                new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials("elastic", "password"));
//
//        RestClientBuilder builder = RestClient.builder(
//                        new HttpHost("localhost", 9200, "https"))
//                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                    @Override
//                    public HttpAsyncClientBuilder customizeHttpClient(
//                            HttpAsyncClientBuilder httpClientBuilder) {
//                        return httpClientBuilder
//                                .setDefaultCredentialsProvider(credentialsProvider);
//                    }
//                });
//        RestClient restClient = builder.build();
//
//        this.transport = new RestClientTransport(
//                restClient, new JacksonJsonpMapper());
//
//        this.esClient = new ElasticsearchClient(transport);
//    }

    public MyElasticSearch() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "password"));

        // Create SSL context that accepts self-signed certificates
        RestClient restClient = null;
        try {
            final SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial((chain, authType) -> true) // Trust all certificates
                    .build();
            RestClientBuilder builder = RestClient.builder(
                            new HttpHost("localhost", 9200, "https")) // Use HTTPS
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(
                                HttpAsyncClientBuilder httpClientBuilder) {
                            return httpClientBuilder
                                    .setSSLContext(sslContext) // Set the SSL context
                                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE) // Ignore hostname verification
                                    .setDefaultCredentialsProvider(credentialsProvider);
                        }
                    });
            restClient = builder.build();

        } catch (Exception e) {
            System.out.println(e);
        }

        this.transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        this.esClient = new ElasticsearchClient(transport);
    }

    public ElasticsearchClient getEsClient(){
        return this.esClient;
    }

    public void addDocument(String index, Article article) throws IOException{
        IndexResponse response = esClient.index(i -> i
                .index(index)
                .id(article.getLinkToArticle())
                .document(article)
        );
    }

    public void removeIndex(String index) throws IOException{
                    esClient.indices().delete(d -> d
                    .index(index)
            );
    }

    public ArrayList<Article> findAll(String index) throws IOException{
        ArrayList<Article> all = new ArrayList<>();

        SearchResponse<Article> response = esClient.search(s -> s
                        .index(index)
                        .query(q -> q.matchAll(m -> m))
                        .size(1000)
                        .sort(so -> so // Add sorting
                                .field(f -> f
                                        .field("publishDate") // Sort by publishDate field
                                        .order(SortOrder.Desc) // Order by descending
                                )
                        ).sort(so -> so
                                .field(f -> f
                                        .field("publishTime")
                                        .order(SortOrder.Desc)
                                )
                        ),
                Article.class
        );

        List<Hit<Article>> hits = response.hits().hits();
        for (Hit<Article> hit : hits) {
            Article article = hit.source();
            all.add(article);
        }
        return all;
    }

    public ArrayList<Article> findAllPaginated(String index, String pageString) throws IOException{
        ArrayList<Article> all = new ArrayList<>();

        int page = Integer.parseInt(pageString);

        SearchResponse<Article> response = esClient.search(s -> s
                        .index(index)
                        .query(q -> q.matchAll(m -> m)) // Fetch all documents
                        .sort(so -> so // Add sorting
                                .field(f -> f
                                        .field("publishDate") // Sort by publishDate field
                                        .order(SortOrder.Desc) // Order by descending
                                )
                        )
                        .sort(so -> so
                                .field(f -> f
                                        .field("publishTime")
                                        .order(SortOrder.Desc)
                                )
                        )
                        .from((page - 1) * pageSize)
                        .size(pageSize),
                Article.class
        );

        List<Hit<Article>> hits = response.hits().hits();
        for (Hit<Article> hit : hits) {
            Article article = hit.source();
            all.add(article);
        }

        // Since sorting is done by Elasticsearch, no need to sort again locally
        return all;
    }

    public ArrayList<Article> searchEverything(
            String index, String text, String source,
            String startDate, String endDate, String pageString) throws IOException{
        ArrayList<Article> all = new ArrayList<>();

        int page = Integer.parseInt(pageString);

        Query textQuery,
                sourceQuery,
                rangeQuery;

        if(text!=null){
            textQuery = MultiMatchQuery.of(t -> t
                            .fields("title", "text")
                            .query(text)
                            .fuzziness("AUTO"))._toQuery();
        } else {
            textQuery = null;
        }

        if(source!=null){
            sourceQuery = MultiMatchQuery.of(t -> t
                    .fields("source", "writer")
                    .query(source)
                    .fuzziness("AUTO"))._toQuery();
        } else {
            sourceQuery = null;
        }

        if(startDate!=null && endDate==null)
            rangeQuery = RangeQuery.of(r -> r
                    .field("publishDate")
                    .gte(JsonData.of(startDate))
            )._toQuery();
        else if(startDate==null && endDate!=null)
            rangeQuery = RangeQuery.of(r -> r
                    .field("publishDate")
                    .lt(JsonData.of(endDate))
            )._toQuery();
        else if(startDate!=null && endDate!=null)
            rangeQuery = RangeQuery.of(r -> r
                    .field("publishDate")
                    .gte(JsonData.of(startDate))
                    .lt(JsonData.of(endDate))
            )._toQuery();
        else {
            rangeQuery = null;
        }

        boolean hasQuery = textQuery != null || sourceQuery != null || rangeQuery != null;

        List<SortOptions> sortOptions = new ArrayList<>();

        if (!hasQuery) {
            // Add sorting criteria
            sortOptions.add(SortOptions.of(s -> s
                    .field(f -> f
                            .field("publishDate")
                            .order(SortOrder.Desc)
                    )
            ));
            sortOptions.add(SortOptions.of(s -> s
                    .field(f -> f
                            .field("publishTime")
                            .order(SortOrder.Desc)
                    )
            ));
        }


        SearchResponse<Article> response = esClient.search(s -> s
                        .index(index)
                        .from((page - 1) * pageSize)
                        .size(pageSize)
                        .query(q -> {
                            if (hasQuery) {
                                // Build bool query with the valid conditions
                                return q.bool(b -> {
                                    if (textQuery != null) {
                                        b.must(textQuery);
                                    }
                                    if (sourceQuery != null) {
                                        b.must(sourceQuery);
                                    }
                                    if (rangeQuery != null) {
                                        b.must(rangeQuery);
                                    }
                                    return b;
                                });
                            } else {
                                // Fallback to a match-all query
                                return q.matchAll(m -> m);
                            }
                        })
                        .sort(sortOptions),
                Article.class
        );

        List<Hit<Article>> hits = response.hits().hits();
        for (Hit<Article> hit : hits) {
            Article article = hit.source();
            all.add(article);
        }

       return all;
    }

    public ArrayList<Article> findLatestArticlesBySources(String index, ArrayList<String> sources) throws IOException {
        ArrayList<Article> latestArticles = new ArrayList<>();

        // Loop through each source and fetch the latest article for each one
        for (String sourceValue : sources) {
            SearchResponse<Article> response = esClient.search(s -> s
                            .index(index)
                            .size(1)
                            .query(q -> q.match(r -> r.field("source").query(sourceValue))
                            )
                            .sort(so -> so
                                    .field(f -> f
                                            .field("publishDate")
                                            .order(SortOrder.Desc)
                                    )
                            ).sort(so -> so
                                    .field(f -> f
                                            .field("publishTime")
                                            .order(SortOrder.Desc)
                                    )
                            ),
                    Article.class
            );

            if (!response.hits().hits().isEmpty()) {
                Article latestArticle = response.hits().hits().get(0).source();
                latestArticles.add(latestArticle);
            }
        }

        return latestArticles;
    }



    public void deleteArticle(String index, String deleted) throws IOException {
        DeleteByQueryRequest deleteByQueryRequest = DeleteByQueryRequest.of(req -> req
                .index(index)
                .query(q -> q
                        .match(m -> m
                                .field("title")
                                .query(deleted)
                        )
                )
        );

        DeleteByQueryResponse deleteByQueryResponse = esClient.deleteByQuery(deleteByQueryRequest);

        // Handle the response
        System.out.println("Deleted documents: " + deleteByQueryResponse.deleted());
    }

//    public ArrayList<Article> searchGeneric(String index, String text) throws IOException {
//        ArrayList<Article> all = new ArrayList<>();
//
//        SearchResponse<Article> response = esClient.search(s -> s
//                        .index(index)
//                        .size(1000)
//                        .query(q -> q
//                                .multiMatch(t -> t
//                                        .fields("text", "title")
//                                        .query(text)
//                                        .fuzziness(String.valueOf(text.length()))))
////                                        .fuzziness("AUTO")))
//                ,Article.class
//        );
//
//        List<Hit<Article>> hits = response.hits().hits();
//        for (Hit<Article> hit : hits) {
//            Article article = hit.source();
//            all.add(article);
//        }
//
//        return sortArticles(all);
//    }
//
//    public ArrayList<Article> searchSource(String index, String source) throws IOException {
//        ArrayList<Article> all = new ArrayList<>();
//
//        SearchResponse<Article> response = esClient.search(s -> s
//                        .index(index)
//                        .size(1000)
//                        .query(q -> q.
//                                multiMatch(t -> t.
//                                        fields("source","writer").
//                                        query(source)
//                                        .fuzziness(String.valueOf(source.length()/2))))
//                ,Article.class
//        );
//
//        List<Hit<Article>> hits = response.hits().hits();
//        for (Hit<Article> hit : hits) {
//            Article article = hit.source();
//            all.add(article);
//        }
//
//        return sortArticles(all);
//    }
//
//    public ArrayList<Article> searchPeriod(String index, String startDate, String endDate) throws IOException {
//        ArrayList<Article> all = new ArrayList<>();
//
//        Query rangeQuery;
//
//        if(!startDate.isEmpty() && endDate.isEmpty())
//            rangeQuery = RangeQuery.of(r -> r
//                .field("publishDate")
//                .gte(JsonData.of(startDate))
//            )._toQuery();
//        else if(startDate.isEmpty() && !endDate.isEmpty())
//            rangeQuery = RangeQuery.of(r -> r
//                .field("publishDate")
//                .lt(JsonData.of(endDate))
//            )._toQuery();
//        else
//            rangeQuery = RangeQuery.of(r -> r
//                    .field("publishDate")
//                    .gte(JsonData.of(startDate))
//                    .lt(JsonData.of(endDate))
//            )._toQuery();
//
//        SearchResponse<Article> response = esClient.search(s -> s
//                        .index(index)
//                        .size(1000)
//                        .query(q -> q.
//                                bool(b -> b
//                                        .must(rangeQuery)))
//                ,Article.class
//        );
//
//        List<Hit<Article>> hits = response.hits().hits();
//        for (Hit<Article> hit : hits) {
//            Article article = hit.source();
//            all.add(article);
//        }
//
//        return sortArticles(all);
//    }

}
