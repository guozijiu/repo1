package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsCourseService {

    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.media.type}")
    private String media_type;
    @Value("${xuecheng.course.source_field}")
    private String source_field;
    @Value("${xuecheng.media.index}")
    private String media_index;
    @Value("${xuecheng.media.source_field}")
    private String media_source_fileld;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        if (courseSearchParam==null){
           courseSearchParam= new CourseSearchParam();
        }
        //创建搜索的请求对象
        SearchRequest searchRequest = new SearchRequest("index");
        //设置搜索类型
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //指定过滤源字段
        String[] source_field_array = source_field.split(",");
        searchSourceBuilder.fetchSource(source_field_array,new String[]{});
        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //搜索条件
        //根据关键字搜索
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan").minimumShouldMatch("70%").field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //一级分类
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())){
            //根据分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt","courseSearchParam.getMt()"));
        }
        //二级分类
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())){
            //根据分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st","courseSearchParam.getSt()"));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            //根据难度等级
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }

        //设置boolQueryBuilder到searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页参数
        if (page<=0){
            page=1;
        }
        if (size<=0){
            size=12;
        }
        int from=(page-1)*size;
        searchSourceBuilder.size(size);

        //设置高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.from();//起始记录下标
       searchRequest.source(searchSourceBuilder);

        QueryResult<CoursePub>queryResult=new QueryResult();
        List<CoursePub> list=new ArrayList<>();
        try {
            //执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //获取响应结果
            SearchHits hits = searchResponse.getHits();
            //匹配总记录数
            long totalHits = hits.totalHits;
            queryResult.setTotal(totalHits);
            SearchHit[] hitsHits = hits.getHits();
            for (SearchHit hit : hitsHits) {
                CoursePub coursePub=new CoursePub();
                //源文档
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出id
                String id = (String) sourceAsMap.get("id");
                coursePub.setId(id);
                //取出name
                String name = (String) sourceAsMap.get("name");
                //取出高亮字段name
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields!=null){
                    HighlightField highlightFieldName = highlightFields.get("name");
                    if (highlightFieldName!=null){
                        Text[] fragments = highlightFieldName.fragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for (Text text : fragments) {
                            stringBuffer.append(text);
                        }
                    }
                }

                coursePub.setName(name);
                list.add(coursePub);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryResult.setList(list);
        QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
    //使用Es客户端查询
    public Map<String, CoursePub> getall(String id) {
        //定义搜索请求对象
        SearchRequest searchRequest=new SearchRequest(index);
        //指定type
        searchRequest.types(index);

        //定义SearchSourceBulider
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termQuery
        searchSourceBuilder.query(QueryBuilders.termQuery("id",id));
        searchRequest.source(searchSourceBuilder);
        //最终要返回的课程信息
        CoursePub coursePub = new CoursePub();
        Map<String,CoursePub>map=new HashMap<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] hits1 = hits.getHits();
            for (SearchHit hit : hits1) {
                //获取文档内容
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //课程id
               String courseId = (String)sourceAsMap.get("id");
               coursePub.setId(courseId);
               //课程名称
               String name = (String)sourceAsMap.get("name");
               coursePub.setName(name);
                map.put(courseId,coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
    //根据多个课程计划查询课程媒资信息
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {

        //定义搜索请求对象
        SearchRequest searchRequest=new SearchRequest(media_index);
        //指定type
        searchRequest.types(media_type);
        //定义SearchSourceBulider
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termsQuery根据多个id查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        //过滤源字段
        String[] includes = media_source_fileld.split(",");
        searchSourceBuilder.fetchSource(includes,new String[]{});
        searchRequest.source(searchSourceBuilder);
        //使用es客户端进行搜索请求Es
        List<TeachplanMediaPub>teachplanMediaPubList=new ArrayList<>();
        long total=0;

        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                TeachplanMediaPub teachplanMediaPub=new TeachplanMediaPub();
                Map<String,Object>sourceAsMap=hit.getSourceAsMap();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");

                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                teachplanMediaPubList.add(teachplanMediaPub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //数据集合
        QueryResult<TeachplanMediaPub>queryResult=new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(total);
        QueryResponseResult<TeachplanMediaPub>queryResponseResult=new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;

    }
}
