package com.atguigu.gmall.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.item.feign.ItemFeign;
import com.atguigu.gmall.list.feign.ListFeign;
import com.atguigu.gmall.product.feign.ProductFeign;
import com.atguigu.gmall.web.service.CreateSkuHtmlService;
import com.atguigu.gmall.web.util.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/web")
public class ItemController {

    @Resource
    private ItemFeign itemFeign;


    @Resource
    private ProductFeign productFeign;


    @RequestMapping(value = "/item")
    public String getItem(Long skuId, Model model){
        //通过SkuId查询SKuInfo
        Map<String, Object> goodsItem  = itemFeign.getGoodsItem(skuId);
        model.addAllAttributes(goodsItem);
        return "item/item";
    }

    /**
     * 打开首页
     * @return
     */
    @RequestMapping(value = "/index")
    public String index(Model model){
        List<JSONObject> baseCategoryList = productFeign.getIndexCategoryList();
        model.addAttribute("list", baseCategoryList);
        return "index/index";
    }

    @Resource
    private CreateSkuHtmlService createSkuHtmlService;
    /**
     * 生成静态页面
     * @param skuId
     * @return
     */
    @RequestMapping(value = "/create")
    @ResponseBody
    public String create(Long skuId){
        //生成静态页面
        createSkuHtmlService.createHtml(skuId);
        //返回
        return "success";
    }

    @Resource
    private ListFeign listFeign;

    @RequestMapping (value = "/list")
    public String list(@RequestParam Map<String,String> searchMap,Model model){
        //调用搜索微服务查询数据
        Map<String, Object> search = listFeign.search(searchMap);
        //将参数再返回给页面,用于条件的回显
        model.addAttribute("searchMap", searchMap);
        //绑定数据
        model.addAllAttributes(search);
        //获取url
        String url = getUrl(searchMap);
        model.addAttribute("url", url);
        //获取总条数
        Object totalHits = search.get("totalHits");
        Object pageSize = search.get("pageSize");
        Integer pageNum = getPageNum(searchMap.get("pageNum"));
        Page page = new Page(Long.parseLong(totalHits.toString()), pageNum, Integer.parseInt(pageSize.toString()));
        model.addAttribute("page",page);
        //返回页面
        return "list/list";
    }
    /**
     * 获取url
     * @param searchMap
     * @return
     */
    private String getUrl(Map<String, String> searchMap){
        String url = "http://localhost:8300/web/list?";
        //遍历map----keywords=华为
        for (Map.Entry<String, String> entry : searchMap.entrySet()) {

            //参数的名字keywords
            String key = entry.getKey();
            if(!key.equals("softField") && !key.equals("softRule") && !key.equals("pageNum")){
                //参数的值: 华为
                String value = entry.getValue();
                url += key + "=" + value + "&";
            }
        }
        return url.substring(0, url.length() - 1 );
    }

    /**
     * 获取用户输入的页面,计算起始行号
     * @param pageNum
     * @return
     */
    private Integer getPageNum(Object pageNum) {
        try {
            return Integer.parseInt(pageNum.toString()) > 0?Integer.parseInt(pageNum.toString()):1;
        }catch (Exception e){
            return 1;
        }
    }
}
