package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    RestClient restClient;


/**
 * 查询全部文档
 * */
public void testSearchAll() throws IOException {
    //搜索请求对象
    SearchRequest searchRequest = new SearchRequest("rc_course");
    //指定类型
    searchRequest.types("doc");
    //搜索源构建
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    //搜索方式
    //matchALLQuery搜索全部
    searchSourceBuilder.query(QueryBuilders.matchAllQuery());
    //设置源字段过滤，第一个参数结果集包含那些字段，第二个参数表示结果集不包含那些字段
    searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
    //向搜索请求对象中设置搜素源
    searchRequest.source(searchSourceBuilder);
    //执行搜索，向ES发起http请求
    SearchResponse searchResponse = client.search(searchRequest);
    //搜索结果
    SearchHits hits = searchResponse.getHits();
    //匹配到的总记录数
    long totalHits = hits.getTotalHits();
    //得到匹配度高的文档
    SearchHit[] searchHits = hits.getHits();
    for (SearchHit hit : searchHits) {
        //文档主键
        String id = hit.getId();
        //源文档内容
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        String name = (String) sourceAsMap.get("name");

        }
    }

    /**
     * 分页查询
     * */
    public void testSearchPage() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("rc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //页码
        int page=1;
        //每页记录数
        int size=1;
        //计算出记录起始下标
        int from = (page - 1) * size;
        //起始下标位置
        searchSourceBuilder.from(from);//起始记录下标，从0开始
        searchSourceBuilder.size(size);//每页显示的记录数
        //搜索方式
        //matchALLQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤，第一个参数结果集包含那些字段，第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜素源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");

        }
    }
    /**
     * 精确查询
     * */
    public void testTermQuery() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("rc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //页码
        int page=1;
        //每页记录数
        int size=1;
        //计算出记录起始下标
        int from = (page - 1) * size;
        //起始下标位置
        searchSourceBuilder.from(from);//起始记录下标，从0开始
        searchSourceBuilder.size(size);//每页显示的记录数
        //搜索方式
        //matchALLQuery搜索精确
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤，第一个参数结果集包含那些字段，第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜素源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");

        }
    }
    /**
     * 根据id查询
     * */
    public void testTerm() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("rc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //页码
        int page=1;
        //每页记录数
        int size=1;
        //计算出记录起始下标
        int from = (page - 1) * size;
        //起始下标位置
        searchSourceBuilder.from(from);//起始记录下标，从0开始
        searchSourceBuilder.size(size);//每页显示的记录数
        //搜索方式
        //根据id查询
        //定义id
        String[]ids=new String[]{"1","2"};
        //matchALLQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //设置源字段过滤，第一个参数结果集包含那些字段，第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜素源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");

        }
    }
    /**
     * MatchQuery
     * */
    public void testMatchQuery() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("rc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //页码
        int page=1;
        //每页记录数
        int size=1;
        //计算出记录起始下标
        int from = (page - 1) * size;
        //起始下标位置
        searchSourceBuilder.from(from);//起始记录下标，从0开始
        searchSourceBuilder.size(size);//每页显示的记录数
        //搜索方式
        //matchALLQuery搜索精确
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("80%"));
        //设置源字段过滤，第一个参数结果集包含那些字段，第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索请求对象中设置搜素源
        searchRequest.source(searchSourceBuilder);
        //执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //文档主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");

        }
    }

}
