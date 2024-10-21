package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@CrossOrigin
@org.springframework.web.bind.annotation.RestController
@RequestMapping("news")
@ComponentScan("org.example")
public class RestController {

    private String curentIndex = "articles_embeddings";

    @Autowired
    private MyElasticSearch esClient;

    @RequestMapping("/greeting")
    public String greeting(){
        return "Hello World!";
    }

    public class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Article[] getAll(@RequestParam(defaultValue = "1") String page){
        System.out.println("Find all : (" + page + ") ");

        try {
            Iterable<Article> articles = esClient.findAllPaginated(curentIndex, page);
            ArrayList<Article> new_articles = new ArrayList<>();
            for (var trial : articles)
                new_articles.add(trial);
            Article[] articlesArray = new Article[new_articles.size()];
            return new_articles.toArray(articlesArray);
        } catch (IOException e) {
            System.out.println(e);
        }
        return new Article[0];
    }


    @RequestMapping(value="/search", method = RequestMethod.GET)
    @ResponseBody
    public Article[] searchEverything(@RequestParam(required = false) String text, @RequestParam(required = false) String source,
                               @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
                               @RequestParam(defaultValue = "1") String page){
        System.out.println("Search everything: (" + page + ") "  + text + " " + source + " " + startDate + " " + endDate);

        try {
            Iterable<Article> articles = esClient.searchEverything(curentIndex, text, source, startDate, endDate, page);
            ArrayList<Article> new_articles = new ArrayList<>();
            for (var trial : articles)
                new_articles.add(trial);
            Article[] articlesArray = new Article[new_articles.size()];
            return new_articles.toArray(articlesArray);
        } catch (IOException e) {
            System.out.println(e);
        }
        return new Article[0];
    }

//    @RequestMapping(value="/search/generic/{text}", method = RequestMethod.GET)
//    @ResponseBody
//    public Article[] searchGeneric(@PathVariable String text){
//        System.out.println("Search generic: " + text);
//
//        try {
//            Iterable<Article> articles = esClient.searchGeneric(curentIndex,text);
//            ArrayList<Article> new_articles = new ArrayList<>();
//            for (var trial : articles)
//                new_articles.add(trial);
//            Article[] articlesArray = new Article[new_articles.size()];
//            return new_articles.toArray(articlesArray);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        return new Article[0];
//    }
//
//    @RequestMapping(value="/search/source/{text}", method = RequestMethod.GET)
//    @ResponseBody
//    public Article[] searchSource(@PathVariable String text){
//        System.out.println("Search source: " + text);
//
//        try {
//            Iterable<Article> articles = esClient.searchSource(curentIndex,text);
//            ArrayList<Article> new_articles = new ArrayList<>();
//            for (var trial : articles)
//                new_articles.add(trial);
//            Article[] articlesArray = new Article[new_articles.size()];
//            return new_articles.toArray(articlesArray);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        return new Article[0];
//    }

//    @RequestMapping(value="/search/range/both/{startDate}/{endDate}", method = RequestMethod.GET)
//    @ResponseBody
//    public Article[] searchRange(@PathVariable String startDate, @PathVariable String endDate){
//        System.out.println("Search range: " + startDate + " | " + endDate);
//
//        try {
//            Iterable<Article> articles = esClient.searchPeriod(curentIndex,startDate, endDate);
//            ArrayList<Article> new_articles = new ArrayList<>();
//            for (var trial : articles)
//                new_articles.add(trial);
//            Article[] articlesArray = new Article[new_articles.size()];
//            return new_articles.toArray(articlesArray);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        return new Article[0];
//    }
//
//    @RequestMapping(value="/search/range/gt/{startDate}", method = RequestMethod.GET)
//    @ResponseBody
//    public Article[] searchRangeGreater(@PathVariable String startDate){
//        System.out.println("Search range greater: " + startDate);
//        try {
//            Iterable<Article> articles = esClient.searchPeriod(curentIndex,startDate, "");
//            ArrayList<Article> new_articles = new ArrayList<>();
//            for (var trial : articles)
//                new_articles.add(trial);
//            Article[] articlesArray = new Article[new_articles.size()];
//            return new_articles.toArray(articlesArray);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        return new Article[0];
//    }
//
//    @RequestMapping(value="/search/range/lt/{endDate}", method = RequestMethod.GET)
//    @ResponseBody
//    public Article[] searchRangeLess(@PathVariable String endDate){
//        System.out.println("Search range less: " + endDate);
//        try {
//            Iterable<Article> articles = esClient.searchPeriod(curentIndex,"", endDate);
//            ArrayList<Article> new_articles = new ArrayList<>();
//            for (var trial : articles)
//                new_articles.add(trial);
//            Article[] articlesArray = new Article[new_articles.size()];
//            return new_articles.toArray(articlesArray);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        return new Article[0];
//    }


//    @RequestMapping(value="/delete/{text}", method = RequestMethod.GET)
//    @ResponseBody
//    public void deleteArticle(@PathVariable String text){
//        System.out.println("Delete article: " + text);
//
//        try {
//           esClient.deleteArticle("articles2", text);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }

//    @RequestMapping(value="/search/generic/{text}", method = RequestMethod.GET)
//    @ResponseBody
//    public Article[] findByText(@PathVariable String text){
//        System.out.println("Search generic: " + text);
//
//        try {
//            Iterable<Article> articles = esClient.findByText("articles2",text);
//            ArrayList<Article> new_articles = new ArrayList<>();
//            for (var trial : articles)
//                new_articles.add(trial);
//            Article[] articlesArray = new Article[new_articles.size()];
//            return new_articles.toArray(articlesArray);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        return new Article[0];
//    }

//    @RequestMapping(value="/search/id/{text}", method = RequestMethod.GET)
//    @ResponseBody
//    public Article[] searchArticle(@PathVariable String text){
//        System.out.println("Search article: " + text);
//
//        try {
//            Iterable<Article> articles = esClient.searchArticle(curentIndex,text);
//            ArrayList<Article> new_articles = new ArrayList<>();
//            for (var trial : articles)
//                new_articles.add(trial);
//            Article[] articlesArray = new Article[new_articles.size()];
//            return new_articles.toArray(articlesArray);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        return new Article[0];
//    }

}
