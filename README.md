# Cotton - AI Powered OSINT Web Application
* This is the backend, for the frontend check [the frontend repo](https://github.com/TeodorRuse/Cotton).

![Cotton Logo](https://github.com/user-attachments/assets/ed072ecf-50f9-456a-97db-f4ed8c7d159d)


**Cotton** is an AI-powered OSINT (Open Source Intelligence) web application specializing in high-speed searching, indexing, and filtering of news articles. It leverages Elasticsearch, Apache Kafka, and SBERT embeddings to offer robust semantic search capabilities. Cotton provides users with the ability to monitor and track news from multiple sources, ensuring fast retrieval and accurate semantic understanding of global events.

## Key Features
- **Real-time monitoring** of news articles from multiple sources.
- **Fast semantic search** with SBERT embeddings.
- **Efficient indexing** using Elasticsearch for high-speed data retrieval.
- **Seamless news filtering** to allow users to get relevant news articles only.
- Built with **React** for an interactive, user-friendly interface.
- Uses **MUI (Material-UI)** for modern and responsive front-end design.
- **Dockerized deployment** for easy setup, scaling, and management of all components.


## Tech Stack
- **Frontend**: React.js, MUI (Material UI)
- **Backend**: Java (Main server), Python (Web Scrapers & SBERT embeddings)
- **Search Engine**: Elasticsearch
- **Messaging Queue**: Apache Kafka
- **Embeddings**: SBERT (Sentence-BERT)
- **Containerization**: Docker
---


### Detailed Workflow

1. **Java Server**: Periodically initializes and manages Python-based **web scrapers**, each assigned to a specific news source.
2. **Web Scrapers**: These scrapers search their respective sources for new articles, comparing against the last known article stored in the database.
3. **Embedding**: When a new article is detected, it is sent to a Python server where an **SBERT embedder** generates a semantic embedding of the article. This embedding is then sent back to the main server.
4. **Article Storage**: The article and its corresponding embedding are stored in an **Elasticsearch** database, which is running in a **Docker** container.
5. **REST API**: The Java server provides a RESTful service for retrieving and searching articles within the Elasticsearch database.
6. **React Frontend**: A React.js application, styled with **MUI (Material UI)**, interacts with the REST API, enabling users to search, browse, and filter news articles.



## Future Features and Roadmap

### 1. **Article Compare**:
   - Ability to compare two articles to determine if they report the same event. This will highlight similarities and discrepancies across multiple sources.
   
### 2. **Advanced Tagging Preprocessor**:
   - A new preprocessing feature to automatically add specific tags and annotations to highlight important aspects of an article (e.g., location, names, events).

### 3. **Dashboard**:
   - A comprehensive dashboard to track:
     - Article metrics (frequency, sources, etc.)
     - Trending topics
     - Important tags
   - This will give users a more contextual understanding of the news.

### 4. **Interactive Map**:
   - Visualize unfolding events around the world with an interactive map that updates in real time as new articles are published.

## Screenshots & Demos

Here are some screenshots and demo videos of **Cotton** in action:

##Web Application preview: 
 https://github.com/user-attachments/assets/71083c6f-dcf7-4c21-984e-4fd6602d9d4a

## Articles being send from the embedder to the main server:
  https://github.com/user-attachments/assets/e85bbc16-56e2-4b63-a24a-a6e63e507bd2

## Docker
 ![docker](https://github.com/user-attachments/assets/98cbb243-1a7e-4f74-b9ca-7eddba89ddc8)

## Kibanna
  ![kibanna](https://github.com/user-attachments/assets/68e815e1-2fab-49fd-a401-0c16c9de8263)

## Kafka Offset Explorer
  ![OffsetExplorer](https://github.com/user-attachments/assets/9f35b70e-e7c1-4709-a88f-1654bf7e3629)


