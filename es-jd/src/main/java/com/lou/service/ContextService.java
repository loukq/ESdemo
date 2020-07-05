package com.lou.service;

import com.alibaba.fastjson.JSON;
import com.lou.pojo.Context;
import com.lou.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ContextService {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Autowired
    private HtmlParseUtil htmlParseUtil;


//    爬出数据，导入es
    public Boolean parseUrl(String keyWord) throws IOException {
        List<Context> contexts = htmlParseUtil.parseUrl(keyWord);
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");

//        查询到的数据封装到request中
        for (int i = 0; i < contexts.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("jd_goods")
                    .source(JSON.toJSONString(contexts.get(i)), XContentType.JSON)
            );
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();


    }


//    根据关键字查询数据
    public List<Map<String, Object>> search(String keyWord, int pageNo, int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo=1;
        }
//        条件搜索
        SearchRequest searchRequest=new SearchRequest("jd_goods");
        
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
//        使用termQueryBuilder进行精确的查询多个中文时由于ik分词的关系，无法查询到结果，使用MatchQueryBuilder可以成功
//        TermQueryBuilder termQueryBuilder=new TermQueryBuilder("name",keyWord);
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name", keyWord);
        searchSourceBuilder.query(matchQueryBuilder);
//        高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("name");//指定哪个的属性高亮
        highlightBuilder.requireFieldMatch(false);//是否允许多个值高亮
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
//        分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

//        执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        数据解析，返回
        List<Map<String, Object>> list=new ArrayList();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
//            封装高亮的结果
//            获取高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            System.out.println("name:"+name);
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的结果

            if (name!=null){
                Text[] texts = name.fragments();
                System.out.println(texts);
                String new_name="";
                for (Text text : texts) {
                    new_name=new_name+text;
                }
                System.out.println("new _naem"+new_name);
                sourceAsMap.put("name",new_name);//替换为新的name
            }


            list.add(sourceAsMap);
        }
        return list;
    }
}
