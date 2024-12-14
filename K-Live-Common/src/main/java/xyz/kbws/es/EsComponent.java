package xyz.kbws.es;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.AppConfig;
import xyz.kbws.exception.BusinessException;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author kbws
 * @date 2024/12/14
 * @description: ES 组件
 */
@Slf4j
@Component
public class EsComponent {

    @Resource
    private AppConfig appConfig;

    @Resource
    private RestHighLevelClient restHighLevelClient;


    private Boolean isExistIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(appConfig.getEsIndexName());
        return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    public void createIndex() {
        try {
            if (isExistIndex()) {
                return;
            }
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(appConfig.getEsIndexName());
            createIndexRequest.settings("{\"analysis\": {\n" +
                    "    \"analyzer\": {\n" +
                    "        \"comma\": {\n" +
                    "            \"type\": \"pattern\",\n" +
                    "              \"pattern\": \",\"\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }}", XContentType.JSON);

            createIndexRequest.mapping("{\n" +
                    "    \"properties\": {\n" +
                    "        \"videoId\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"userId\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"videoCover\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"videoName\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"analyzer\": \"ik_max_word\"\n" +
                    "        },\n" +
                    "        \"tags\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"analyzer\": \"comma\"\n" +
                    "        },\n" +
                    "        \"playCount\": {\n" +
                    "            \"type\": \"integer\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"danmuCount\": {\n" +
                    "            \"type\": \"integer\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"collectCount\": {\n" +
                    "            \"type\": \"integer\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"createTime\": {\n" +
                    "            \"type\": \"date\",\n" +
                    "            \"format\": \"yyyy-MM-dd HH:mm:ss\",\n" +
                    "            \"index\": false\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", XContentType.JSON);
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = createIndexResponse.isAcknowledged();
            if (!acknowledged) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 ES 失败");
            }
        } catch (Exception e) {
            log.error("初始化 ES 失败: {}", e.getMessage());
        }
    }
}
