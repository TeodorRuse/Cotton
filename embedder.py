def create_article_from_message(msg):
    article_data = json.loads(msg.value.decode('utf-8'))

    return Article(
        title=article_data.get("title"),
        link_to_article=article_data.get("link_to_article"),
        publish_date=article_data.get("publish_date"),
        publish_time=article_data.get("publish_time"),
        text=article_data.get("text"),
        writer=article_data.get("writer"),
        link_to_image=article_data.get("link_to_image"),
        source=article_data.get("source")
    )

def consume_articles():
    for msg in consumer:
        article = create_article_from_message(msg)
        print(article)
        print('Received article, creating embedding...')
        embeding = model.encode(article.text)
        print(embeding)

consumer = KafkaConsumer('articles', bootstrap_servers='kafka:9092')
print("Created consumer...")
model = SentenceTransformer('sentence-transformers/distiluse-base-multilingual-cased-v2')
print("Loaded model, listening...")

consume_articles()
