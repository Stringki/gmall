package com.atguigu.gmall.list.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.dao.GoodsRepository;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private ProductFeign productFeign;

    @Resource
    private GoodsRepository goodsRepository;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 上架商品列表
     * @param skuId
     */
    @Override
    public Goods upperGoods(Long skuId) {
        //初始化对象
        Goods goods = new Goods();
        //通过skuId查询商品对应的平台属性
        List<BaseAttrInfo> attrList = productFeign.getAttrList(skuId);
        //遍历商品的平台属性
        List<SearchAttr> searchAttrs = attrList.stream().map(att -> {
            SearchAttr searchAttr = new SearchAttr();
            //获取属性ID
            searchAttr.setAttrId(att.getId());
            searchAttr.setAttrName(att.getAttrName());
            searchAttr.setAttrValue(att.getAttrValueList().get(0).getValueName());
            //返回结果
            return searchAttr;
        }).collect(Collectors.toList());
        //设置属性
        goods.setAttrs(searchAttrs);
        //获取skuInfo信息
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        //获取分类信息
        BaseCategoryView category = productFeign.getCategory(skuInfo.getCategory3Id());
        //设置分类信息
        goods.setCategory1Id(category.getCategory1Id());
        goods.setCategory1Name(category.getCategory1Name());
        goods.setCategory2Id(category.getCategory2Id());
        goods.setCategory2Name(category.getCategory2Name());
        goods.setCategory3Id(category.getCategory3Id());
        goods.setCategory3Name(category.getCategory3Name());
        //获取sku价格
        BigDecimal price = productFeign.getPrice(skuId);
        //设置价格
        goods.setPrice(price.doubleValue());
        //获取对应品牌信息
        BaseTrademark trademark = productFeign.getTrademark(skuInfo.getTmId());
        //设置品牌信息
        goods.setTmId(trademark.getId());
        goods.setTmName(trademark.getTmName());
        //设置品牌图片
        goods.setTmLogoUrl(trademark.getLogoUrl());
        //补全信息
        goods.setId(skuId);
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setCreateTime(new Date());
        //保存设置信息
        goodsRepository.save(goods);
        return goods;
    }

    /**
     * 下架商品列表
     * @param skuId
     */
    @Override
    public void lowerGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    @Resource
    private RedisTemplate redisTemplate;
    /**
     * 更新热点值
     * @param skuId
     */
    @Override
    public void incrHotScore(Long skuId) {
        String hotkey = "hotScore";
        //用户每访问一次就对该商品热点值加一
        Double aDouble = redisTemplate.boundZSetOps(hotkey).incrementScore("sku:" + skuId, 1);
        //每十次同步一次ES数据
        if (aDouble % 10 == 0){
            Goods goods = goodsRepository.findById(skuId).get();
            //设置进去热度值
            goods.setHotScore(aDouble.longValue());
            goodsRepository.save(goods);
        }
    }
    /**
     * 搜索商品
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //参数校验
        if(searchMap == null){
            return null;
        }
        try {
            //构建查询条件
            SearchRequest searchRequest = buildQueryParams(searchMap);
            //执行搜索
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果
            return getSearchData(searchResponse);
        }catch (Exception e){
            e.printStackTrace();
        }
        //返回结果
        return null;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private SearchRequest buildQueryParams(Map<String, String> searchMap) {
        //查询请求的request初始化
        SearchRequest searchRequest = new SearchRequest();
        //条件构造器初始化
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //构建bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //构建查询条件
        String keywords = searchMap.get("keywords");
        if(!StringUtils.isEmpty(keywords)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", keywords));
//            builder.query(QueryBuilders.matchQuery("title", keywords));
        }
        //品牌的查询条件:参数就是 1:华为
        String tradeMark = searchMap.get("tradeMark");
        if(!StringUtils.isEmpty(tradeMark)){
            String[] split = tradeMark.split(":");
            if (split.length > 0){
                //使用品牌id查询
                boolQueryBuilder.must(QueryBuilders.termQuery("tmId", split[0]));
            }
        }
        //平台属性的查询attr_网络制式=电信3G&attr_显示屏幕尺寸=4.0-4.9英寸&attr_摄像头像素=摄像头
        for (String attrName : searchMap.keySet()) {
            //判断是否为平台属性
            if(attrName.startsWith("attr_")){
                //获取平台属性选择的值: 电信3G
                String attrValue = searchMap.get(attrName);
                //平台属性的名称进行处理
                attrName = attrName.replace("attr_", "");
                //拼接条件
                BoolQueryBuilder nestedBool = new BoolQueryBuilder();
                nestedBool.must(QueryBuilders.termQuery("attrs.attrName",attrName));
                nestedBool.must(QueryBuilders.termQuery("attrs.attrValue",attrValue));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBool, ScoreMode.None);
                //设置到总条件中去
                boolQueryBuilder.must(nestedQuery);
            }
        }

        //价格查询----price=1000-2000元或3000元以上
        String price = searchMap.get("price");
        if(!StringUtils.isEmpty(price)){
            //价格切分
            price = price.replace("元", "").replace("以上", "");
            String[] split = price.split("-");
            //价格大于起始值
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            if(split.length > 1){
                //价格小于最大值
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        //分页查询
        Integer size = 100;//后台固定每页显示100条数据
        //获取页码
        Integer pageNum = getPageNum(searchMap.get("pageNum"));
        builder.from((pageNum -1) * size);
        builder.size(size);
        //排序---softField=price  softRule=ASC/DESC
        String softField = searchMap.get("softField");
        String softRule = searchMap.get("softRule");
        if(!StringUtils.isEmpty(softField) && !StringUtils.isEmpty(softRule)){
            builder.sort(softField, softRule.equals("ASC")?SortOrder.ASC:SortOrder.DESC);
        }else{
            builder.sort("createTime", SortOrder.DESC);
        }
        //设置全部的查询条件
        builder.query(boolQueryBuilder);

        //设置高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color:green'>");
        highlightBuilder.postTags("</font>");
        builder.highlighter(highlightBuilder);
        //构建聚合查询条件---品牌
        builder.aggregation(
                AggregationBuilders.terms("aggTmId").field("tmId")
                        .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                        .size(1000000)
        );
        //平台属性的聚合条件设置
        builder.aggregation(
                AggregationBuilders.nested("aggAttrs", "attrs").subAggregation(
                        AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                                .size(1000000)
                )
        );

        searchRequest.source(builder);
        //返回条件
        return searchRequest;
    }

    /**
     * 获取用户输入的页面,计算起始行号
     * @param pageNum
     * @return
     */
    private Integer getPageNum(String pageNum) {
        try {
            return Integer.parseInt(pageNum) > 0?Integer.parseInt(pageNum):1;
        }catch (Exception e){
            return 1;
        }
    }

    /**
     * 解析搜索到的结果
     * @param searchResponse
     */
    private Map<String, Object> getSearchData(SearchResponse searchResponse) {
        //初始化返回结果
        Map<String, Object> result = new HashMap<>();
        //商品列表初始化
        List<Goods> goodsList = new ArrayList<>();
        //获取命中的对象
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit next = iterator.next();
            //获取字符串对象
            String sourceAsString = next.getSourceAsString();
            //反序列化
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);

            //获取高亮的数据
            HighlightField highlightField = next.getHighlightFields().get("title");
            if(highlightField != null){
                Text[] fragments = highlightField.getFragments();
                if(fragments != null && fragments.length > 0){
                    String title = "";
                    for (Text fragment : fragments) {
                        title += fragment;
                    }
                    goods.setTitle(title);
                }
            }
            goodsList.add(goods);
        }
        result.put("goodsList", goodsList);
        //获取全部的聚合数据
        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();
        //获取品牌的聚合结果
        List<SearchResponseTmVo> tradeMarkAggResult = getTradeMarkAggResult(aggregationMap);
        result.put("tradeMarkResult", tradeMarkAggResult);
        //获取平台属性的聚合结果
        List<SearchResponseAttrVo> baseAttrResult = getBaseAttrResult(aggregationMap);
        result.put("baseAttrResult", baseAttrResult);
        //一共有多少条数据
        long totalHits = hits.getTotalHits();
        result.put("totalHits", totalHits);
        result.put("pageSize", 100);
        //返回
        return result;
    }

    /**
     * 获取平台属性的聚合结果
     * @param aggregationMap
     */
    private List<SearchResponseAttrVo> getBaseAttrResult(Map<String, Aggregation> aggregationMap) {
        List<SearchResponseAttrVo> searchResponseAttrVoList = new ArrayList<>();
        //获取平台属性nested类型的聚合结果
        ParsedNested aggAttrs = (ParsedNested)aggregationMap.get("aggAttrs");
        //获取子聚合的结果
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        //遍历获取平台属性的名称和值
        for (Terms.Bucket bucket : aggAttrId.getBuckets()) {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取平台属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取子聚合的结果
            ParsedStringTerms aggAttrName = bucket.getAggregations().get("aggAttrName");
            if(aggAttrName.getBuckets().size() > 0){
                //获取平台属性的名字
                String attrName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            ParsedStringTerms aggAttrValue = bucket.getAggregations().get("aggAttrValue");
            List<String> attrValues = new ArrayList<>();
            if(aggAttrValue.getBuckets().size() > 0){
                for (Terms.Bucket aggAttrValueBucket : aggAttrValue.getBuckets()) {
                    //遍历获取值
                    String attrValue = aggAttrValueBucket.getKeyAsString();
                    attrValues.add(attrValue);
                }
            }
            searchResponseAttrVo.setAttrValueList(attrValues);
            searchResponseAttrVoList.add(searchResponseAttrVo);
        }
        return searchResponseAttrVoList;
    }

    /**
     * 获取聚合的结果
     * @param aggregationMap
     */
    private List<SearchResponseTmVo> getTradeMarkAggResult(Map<String, Aggregation> aggregationMap) {
        List<SearchResponseTmVo> searchResponseTmVoList = new ArrayList<>();
        //获取品牌的聚合结果--品牌的id是long类型的
        ParsedLongTerms aggTmId = (ParsedLongTerms)aggregationMap.get("aggTmId");
        for (Terms.Bucket bucket : aggTmId.getBuckets()) {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌的id
            long tmId = bucket.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //获取自聚合的结果
            Aggregations aggregations = bucket.getAggregations();
            ParsedStringTerms aggTmName = aggregations.get("aggTmName");
            //获取品牌的名字
            if(aggTmName.getBuckets().size() > 0){
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            //获取logo的地址
            ParsedStringTerms aggTmLogoUrl = aggregations.get("aggTmLogoUrl");
            if(aggTmLogoUrl.getBuckets().size() > 0){
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            searchResponseTmVoList.add(searchResponseTmVo);
        }
        return searchResponseTmVoList;
    }
}
