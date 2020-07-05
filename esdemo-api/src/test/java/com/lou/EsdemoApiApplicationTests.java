package com.lou;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.lou.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//测试api

/**
 * 固定的套路，
 * 1、得到一个XXXXrequest，作为命令的对象
 * 2、request中传入相应的参数，可以设置timeout
 * 3、调用client来执行命令
 */
@SpringBootTest
class EsdemoApiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;


//    创建索引
    @Test
    void contextLoads() throws IOException {
//        创建请求
        CreateIndexRequest request = new CreateIndexRequest("citys");
//        执行请求,请求后获得响应createIndexResponse
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);

    }

    //        测试获取索引,判断库是否存在
    @Test
    void testExist() throws IOException {
        GetIndexRequest request=new GetIndexRequest("citys");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(exists);
    }

//    测试删除索引
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest=new DeleteIndexRequest("citys");
        AcknowledgedResponse delete = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);

        System.out.println(delete);
    }

//    测试创建对象
    @Test
    void testAddPojo() throws IOException {
//        创建对象
        User user = new User("beijing", 230);

//        创建请求
        IndexRequest indexRequest=new IndexRequest("citys");

        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));

//        数据放入请求,转换为json string
        JSON parse = JSONUtil.parse(user);
        String s = parse.toJSONString(0);
        indexRequest.source(s, XContentType.JSON);

//        执行请求
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());//返回执行的状态码
    }


//    获取索引的信息
    @Test
    void getIndext() throws IOException {
        GetRequest request=new GetRequest("citys","1");

        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);

        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);

        String sourceAsString = documentFields.getSourceAsString();

        System.out.println(sourceAsString);

    }

//    测试更新
    @Test
    void testUpdata() throws IOException {

        UpdateRequest request = new UpdateRequest("citys", "1");

        User user = new User("hangzhou", 800);

        request.doc(JSONUtil.parse(user).toJSONString(2),XContentType.JSON);

        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println(update.status());

    }

//      测试删除
    @Test
    void testDelete() throws IOException {
        DeleteRequest deleteRequest=new DeleteRequest("citys","1");
        deleteRequest.timeout(TimeValue.timeValueSeconds(1));

        DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(delete.status());

    }


//    批量的导入
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("1s");

        ArrayList<User> users = new ArrayList<>();

        users.add(new User("西安",200));
        users.add(new User("hainan",300));
        users.add(new User("shanghai",900));

//      遍历后存入request中
        for (int i = 0; i < users.size(); i++) {
                bulkRequest.add(
                        new IndexRequest("citys")
                                .id(""+(i+1))
                                .source(JSONUtil.parse(users.get(i)).toJSONString(0),XContentType.JSON)

                );

        }

        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        System.out.println(bulk.status());

    }


    @Test
//    测试查找
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("citys");
//        构建搜索的条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


//        精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "西安");
//      匹配所有
//        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);

//        fastjson的解析的比hutool解析的深度大
        System.out.println(JSONUtil.parse(search).toJSONString(0));
        System.out.println("--------");
        System.out.println(com.alibaba.fastjson.JSON.toJSONString(search));
//      输出所有的命中
        for (SearchHit hit : search.getHits().getHits()) {
            System.out.println(JSONUtil.toJsonStr(hit.getSourceAsString()));

        }

//        System.out.println(search.status());
//        System.out.println(search.toString());
//        System.out.println(search.getHits());


    }





}
