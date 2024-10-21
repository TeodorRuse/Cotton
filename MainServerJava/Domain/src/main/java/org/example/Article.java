package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class Article {
//    private String title;
//    private String linkToArticle;
//    private String publishDate;
//    private String publishTime;
//    private String writer;
//    private String linkToImage;
//    private String source;
//    private String text;
    @JsonProperty("title")
    private String title;

    @JsonProperty("linkToArticle")
    private String linkToArticle;

    @JsonProperty("publishDate")
    private String publishDate;

    @JsonProperty("publishTime")
    private String publishTime;

    @JsonProperty("writer")
    private String writer;

    @JsonProperty("linkToImage")
    private String linkToImage;

    @JsonProperty("source")
    private String source;

    @JsonProperty("text")
    private String text;

    @JsonProperty("embeddings")
    private double[] embeddings;

    public Article(String title, String linkToArticle, String publishDate, String publishTime,
                   String writer, String linkToImage, String source, String text) {
        this.title = title;
        this.linkToArticle = linkToArticle;
        this.publishDate = publishDate;
        this.publishTime = publishTime;
        this.text = text;
        this.writer = writer;
        this.linkToImage = linkToImage;
        this.source = source;
    }

    public Article(String title, String linkToArticle, String publishDate, String publishTime,
                   String text, String writer, String linkToImage, String source, double[] embeddings) {
        this.title = title;
        this.linkToArticle = linkToArticle;
        this.publishDate = publishDate;
        this.publishTime = publishTime;
        this.text = text;
        this.writer = writer;
        this.linkToImage = linkToImage;
        this.source = source;
        this.embeddings = Arrays.copyOf(embeddings, embeddings.length);
    }

    @Override
    public String toString() {
        return "Article{" +
                "\ntitle='" + title + '\'' +
                "\nlinkToArticle='" + linkToArticle + '\'' +
                "\npublishDate='" + publishDate + '\'' +
                "\npublishTime='" + publishTime + '\'' +
                "\nwriter='" + writer + '\'' +
                "\nlinkToImage='" + linkToImage + '\'' +
                "\nsource='" + source + '\'' +
                "\ntext='" + text + '\'' +
                "\nembeddings='" + Arrays.toString(embeddings) + '\'' +
                "}\n\n";
    }

    public Article() {
    }

    public double[] getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(double[] embeddings) {
        this.embeddings = Arrays.copyOf(embeddings, embeddings.length);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkToArticle() {
        return linkToArticle;
    }

    public void setLinkToArticle(String linkToArticle) {
        this.linkToArticle = linkToArticle;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getLinkToImage() {
        return linkToImage;
    }

    public void setLinkToImage(String linkToImage) {
        this.linkToImage = linkToImage;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
