package masterthesis.conferences.server.controller.storage;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;

public abstract class ElasticOperation {
    protected static ElasticsearchAsyncClient esClient = getElastic();

    private static ElasticsearchAsyncClient getElastic(){
        if (esClient == null) esClient = StorageController.getElasticInstance();
        return esClient;
    }
}
