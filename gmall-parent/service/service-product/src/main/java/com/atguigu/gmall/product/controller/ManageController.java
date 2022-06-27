package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.api.product.ManageControllerApi;
import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product")
public class ManageController implements ManageControllerApi {

    @Autowired
    private ManageService manageService;

    /**
     * 查询所有的一级分类
     * @return
     */
    @GetMapping(value = "/getCategory1")
    public Result<List<BaseCategory1>> getCategory1(){
        List<BaseCategory1> category1List = manageService.findCategory1List();
        return Result.ok(category1List);
    }

    /**
     * 根据一级分类查询所有的二级分类
     * @return
     */
    @GetMapping(value = "/getCategory2/{cid}")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable(value = "cid") Long cid){
        List<BaseCategory2> category2List = manageService.findCategory2List(cid);
        return Result.ok(category2List);
    }

    /**
     * 根据二级分类查询所有的三级分类
     * @return
     */
    @GetMapping(value = "/getCategory3/{cid}")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable(value = "cid") Long cid){
        List<BaseCategory3> category3List = manageService.findCategory3List(cid);
        return Result.ok(category3List);
    }

    /**
     * 根据分类查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> findBaseAttrInfo(@PathVariable(value = "category1Id") Long category1Id,
                                                       @PathVariable(value = "category2Id") Long category2Id,
                                                       @PathVariable(value = "category3Id") Long category3Id){
        List<BaseAttrInfo> baseAttrInfoList =
                manageService.findBaseAttrInfoListByCategory(category1Id, category2Id, category3Id);
        return Result.ok(baseAttrInfoList);
    }

    /**
     * 新增平台属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping(value = "/saveAttrInfo")
    public Result<BaseAttrInfo> saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfo = manageService.addOrUpdateBaseAttrInfo(baseAttrInfo);
        return Result.ok(baseAttrInfo);
    }

    /**
     * 根据平台属性名称的id查询平台属性的值列表
     *
     * @param id
     * @return
     */
    @Override
    @GetMapping(value = "/getAttrValueList/{id}")
    public Result<List<BaseAttrValue>> getAttrValueList(@PathVariable(value = "id") Long id) {
        List<BaseAttrValue> attrValueList = manageService.getAttrValueList(id);
        return Result.ok(attrValueList);
    }

    /**
     * 分页查询spuinfo的列表信息
     * @param category3Id
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/{page}/{size}")
    public Result getSpuInfoList(Long category3Id,
                                 @PathVariable(value = "page") Integer page,
                                 @PathVariable(value = "size")Integer size){
        IPage<SpuInfo> spuInfoList = manageService.getSpuInfoList(category3Id, page, size);
        return Result.ok(spuInfoList);
    }

    /**
     * 查询所有的品牌的列表
     * @return
     */
    @GetMapping(value = "/baseTrademark/getTrademarkList")
    public Result<List<BaseTrademark>> getTrademarkList(){
        List<BaseTrademark> baseTrademarkList = manageService.getBaseTrademarkList();
        return Result.ok(baseTrademarkList);
    }

    /**
     * 查询所有的销售属性的列表
     * @return
     */
    @GetMapping(value = "/baseSaleAttrList")
    public Result<List<BaseSaleAttr>> baseSaleAttrList(){
        List<BaseSaleAttr> baseTrademarkList = manageService.getBaseSaleAttrList();
        return Result.ok(baseTrademarkList);
    }

    /**
     * 保存spu的信息
     * @param spuInfo
     * @return
     */
    @PostMapping(value = "/saveSpuInfo")
    public Result<SpuInfo> saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuInfo = manageService.saveSpuInfo(spuInfo);
        return Result.ok(spuInfo);
    }

    /**
     * 分页查询sku列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/list/{page}/{size}")
    public Result list(@PathVariable(value = "page") Integer page,
                       @PathVariable(value = "size") Integer size){
        IPage<SkuInfo> skuInfoIPage = manageService.pageSkuInfo(page, size);
        return Result.ok(skuInfoIPage);
    }

    /**
     * 查询指定spu的图片信息
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable(value = "spuId") Long spuId){
        List<SpuImage> spuImageList = manageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }

    /**
     * 根据spu的id查询该spu的所有的销售属性和销售属性的值
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttr>> spuSaleAttrList(@PathVariable(value = "spuId") Long spuId){
        List<SpuSaleAttr> spuSaleAttrs = manageService.selectSpuSaleAttrBySpuId(spuId);
        return Result.ok(spuSaleAttrs);
    }

    /**
     * 保存skuinfo
     * @param skuInfo
     * @return
     */
    @PostMapping(value = "/saveSkuInfo")
    public Result<SkuInfo> saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuInfo = manageService.saveSkuInfo(skuInfo);
        return Result.ok(skuInfo);
    }

    /**
     * 上架
     * @param spuId
     * @return
     */
    @GetMapping(value = "/onSale/{spuId}")
    public Result<SkuInfo> onSale(@PathVariable(value = "spuId") Long spuId){
        SkuInfo skuInfo = manageService.upOrDown(spuId, ProductConst.SKU_IS_SALE_TRUE);
        return Result.ok(skuInfo);
    }

    /**
     * 下架
     * @param spuId
     * @return
     */
    @GetMapping(value = "/cancelSale/{spuId}")
    public Result<SkuInfo> cancelSale(@PathVariable(value = "spuId") Long spuId){
        SkuInfo skuInfo = manageService.upOrDown(spuId, ProductConst.SKU_IS_SALE_FALSE);
        return Result.ok(skuInfo);
    }

    /**
     * 分页查询品牌列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/baseTrademark/{page}/{size}")
    public Result<IPage<BaseTrademark>> baseTrademark(@PathVariable(value = "page") Integer page,
                                                     @PathVariable(value = "size") Integer size){
        IPage<BaseTrademark> baseTrademarkListPage = manageService.getBaseTrademarkListPage(page, size);
        return Result.ok(baseTrademarkListPage);
    }
}
