package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 管理控制台使用的service接口类
 */
public interface ManageService {

    /**
     * 查询所有的一级分类
     * @return
     */
    public List<BaseCategory1> findCategory1List();

    /**
     * 根据一级分类查询所有的二级分类
     * @param cid:一级分类的id
     * @return
     */
    public List<BaseCategory2> findCategory2List(Long cid);

    /**
     * 根据二级分类查询所有的三级分类
     * @param cid:二级分类的id
     * @return
     */
    public List<BaseCategory3> findCategory3List(Long cid);

    /**
     * 根据分类查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    public List<BaseAttrInfo> findBaseAttrInfoListByCategory(Long category1Id,
                                                               Long category2Id,
                                                               Long category3Id);

    /**
     * 新增或修改平台属性
     * @param baseAttrInfo
     * @return
     */
    public BaseAttrInfo addOrUpdateBaseAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台属性名称的id查询平台属性的值列表
     * @param id
     * @return
     */
    public List<BaseAttrValue> getAttrValueList(Long id);

    /**
     * 分页条件查询spu的信息
     * @param cid
     * @param page
     * @param size
     * @return
     */
    public IPage<SpuInfo> getSpuInfoList(Long cid, Integer page, Integer size);

    /**
     * 获取所有的品牌的列表
     * @return
     */
    public List<BaseTrademark> getBaseTrademarkList();

    /**
     * 分页查询品牌列表
     * @param page
     * @param size
     * @return
     */
    IPage<BaseTrademark> getBaseTrademarkListPage(Integer page,Integer size);

    /**
     * 获取所有的销售属性的列表
     * @return
     */
    public List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 保存spu的信息
     * @param spuInfo
     * @return
     */
    public SpuInfo saveSpuInfo(SpuInfo spuInfo);

    /**
     * 分页查询sku的列表
     * @param page
     * @param size
     * @return
     */
    public IPage<SkuInfo> pageSkuInfo(Integer page, Integer size);


    /**
     * 查询指定spu的图片信息
     * @param spuId
     * @return
     */
    public List<SpuImage> getSpuImageList(Long spuId);

    /**
     * 根据spu的id查询该spu的所有的销售属性和销售属性的值
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuId(Long spuId);

    /**
     * 保存skuinfo
     * @param skuInfo
     * @return
     */
    public SkuInfo saveSkuInfo(SkuInfo skuInfo);

    /**
     * 上架或下架
     * @param skuId
     * @param sale
     * @return
     */
    public SkuInfo upOrDown(Long skuId, Short sale);

}
