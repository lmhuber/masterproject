package masterthesis.conferences.server.rest.storage;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import masterthesis.conferences.server.controller.ServerController;

public abstract class ElasticOperation {
    protected static ElasticsearchAsyncClient esClient = getElastic();

    private static ElasticsearchAsyncClient getElastic(){
        if (esClient == null) esClient = ServerController.getElastic();
        return esClient;
    }
}
