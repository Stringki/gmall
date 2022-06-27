package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台管理相关接口的实现类
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    /**
     * 查询所有的一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> findCategory1List() {
        return baseCategory1Mapper.selectList(null);
    }

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    /**
     * 根据一级分类查询所有的二级分类
     *
     * @param cid :一级分类的id
     * @return
     */
    @Override
    public List<BaseCategory2> findCategory2List(Long cid) {
        //参数判断
        if(cid == null){
            throw new RuntimeException("参数错误!!!");
        }
        //条件查询
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        //拼接条件
        wrapper.eq(BaseCategory2::getCategory1Id, cid);
        //执行查询
        return baseCategory2Mapper.selectList(wrapper);
    }

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    /**
     * 根据二级分类查询所有的三级分类
     *
     * @param cid :二级分类的id
     * @return
     */
    @Override
    public List<BaseCategory3> findCategory3List(Long cid) {
        //参数判断
        if(cid == null){
            throw new RuntimeException("参数错误!!!");
        }
        //条件查询
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        //拼接条件
        wrapper.eq(BaseCategory3::getCategory2Id, cid);
        //执行查询
        return baseCategory3Mapper.selectList(wrapper);
    }

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    /**
     * 根据分类查询平台属性列表
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> findBaseAttrInfoListByCategory(Long category1Id,
                                                             Long category2Id,
                                                             Long category3Id) {

        return baseAttrInfoMapper.selectBaseAttrInfoListByCategory(category1Id,category2Id, category3Id);
    }

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    /**
     * 新增或修改平台属性
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public BaseAttrInfo addOrUpdateBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        //校验参数
        if(baseAttrInfo == null){
            throw new RuntimeException("参数错误!!!!");
        }
        //判断是否为新增
        if(baseAttrInfo.getId() == null){
            //新增平台属性名称表:新增完成以后id就有值了
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if(insert <= 0){
                throw new RuntimeException("新增平台属性名称失败!!!!");
            }
        }else{
            //修改平台属性的名称表
            baseAttrInfoMapper.updateById(baseAttrInfo);
            //修改的操作,删除旧的平台属性值的信息
            baseAttrValueMapper.delete(
                    new LambdaQueryWrapper<BaseAttrValue>()
                            .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
        }
        //新增平台属性值表---方案一
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        List<BaseAttrValue> newAttrValueList = attrValueList.stream().map(value -> {
            //补全平台属性名称的id
            value.setAttrId(baseAttrInfo.getId());
            //新增
            baseAttrValueMapper.insert(value);
            //返回
            return value;
        }).collect(Collectors.toList());
//        //方案二
//        for (BaseAttrValue baseAttrValue : attrValueList) {
//            baseAttrValue.setAttrId(baseAttrInfo.getId());
//            baseAttrValueMapper.insert(baseAttrValue);
//        }
        //放回原对象
        baseAttrInfo.setAttrValueList(newAttrValueList);
        //返回
        return baseAttrInfo;
    }

    /**
     * 根据平台属性名称的id查询平台属性的值列表
     *
     * @param id
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long id) {
        //参数校验
        if(id == null){
            throw new RuntimeException("参数错误!!!!");
        }
        //条件查询
        List<BaseAttrValue> baseAttrValues =
                baseAttrValueMapper.selectList(
                        new LambdaQueryWrapper<BaseAttrValue>().eq(BaseAttrValue::getAttrId, id));
        //返回结果
        return baseAttrValues;
    }

    @Autowired
    private SpuInfoMapper spuInfoMapper;
    /**
     * 分页条件查询spu的信息
     *
     * @param cid
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfoList(Long cid, Integer page, Integer size) {
        //参数校验
        if(cid == null){
            throw new RuntimeException("参数错误!!!!");
        }
        //分页参数校验
        if(page == null || page <= 0){
            page = 1;
        }
        if(size == null || size <= 0 ){
            size = 10;
        }
        //条件构建
        LambdaQueryWrapper<SpuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpuInfo::getCategory3Id, cid);
        //分页条件查询
        IPage<SpuInfo> spuInfoIPage = spuInfoMapper.selectPage(new Page<>(page, size), wrapper);
        //返回结果
        return spuInfoIPage;
    }

    @Autowired
    private BaseTradeMarkMapper baseTradeMarkMapper;
    /**
     * 获取所有的品牌的列表
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getBaseTrademarkList() {
        return baseTradeMarkMapper.selectList(null);
    }

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    /**
     * 获取所有的销售属性的列表
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }


    @Autowired
    private SpuImageMapper spuImageMapper;
    /**
     * 保存spu的信息
     *
     * @param spuInfo
     * @return
     */
    @Override
    public SpuInfo saveSpuInfo(SpuInfo spuInfo) {
        //参数校验
        if(spuInfo == null){
            throw new RuntimeException("参数错误!!!!");
        }
        //获取spu的id的信息
        Long spuId = spuInfo.getId();
        if(spuId != null){
            //修改的场合
            spuInfoMapper.updateById(spuInfo);
            //清除旧数据
            spuImageMapper.delete(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId, spuId));
            spuSaleAttrMapper.delete(new LambdaQueryWrapper<SpuSaleAttr>().eq(SpuSaleAttr::getSpuId, spuId));
            spuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SpuSaleAttrValue>().eq(SpuSaleAttrValue::getSpuId, spuId));
        }else{
            //保存spuinfo的信息,获取spu的id
            int spuResult = spuInfoMapper.insert(spuInfo);
            if(spuResult <= 0){
                throw new RuntimeException("保存spu的信息失败!!!!");
            }
            spuId = spuInfo.getId();
        }
        //保存spu的image信息
        List<SpuImage> spuImages = saveSpuImageList(spuId, spuInfo.getSpuImageList());
        //放回返回对象中
        spuInfo.setSpuImageList(spuImages);
        //保存spu的销售属性的信息
        List<SpuSaleAttr> spuSaleAttrs =
                saveSpuSaleAttrInfo(spuId, spuInfo.getSpuSaleAttrList());
        spuInfo.setSpuSaleAttrList(spuSaleAttrs);
        //返回结果
        return spuInfo;
    }

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    /**
     * 保存spu的销售属性的信息
     * @param spuId
     * @param spuSaleAttrList
     * @return
     */
    private List<SpuSaleAttr> saveSpuSaleAttrInfo(Long spuId,
                                                  List<SpuSaleAttr> spuSaleAttrList) {
        return spuSaleAttrList.stream().map(attr -> {
            //补全信息
            attr.setSpuId(spuId);
            //新增:attr新增完成以后,就拥有主键id的值
            spuSaleAttrMapper.insert(attr);
            //新增该属性的值
            List<SpuSaleAttrValue> spuSaleAttrValueList = attr.getSpuSaleAttrValueList();
            List<SpuSaleAttrValue> newSpuSaleAttrValueList = spuSaleAttrValueList.stream().map(value -> {
                //补全spuid
                value.setSpuId(spuId);
                //补全销售属性的名称
                value.setSaleAttrName(attr.getSaleAttrName());
                //新增
                spuSaleAttrValueMapper.insert(value);
                //返回
                return value;
            }).collect(Collectors.toList());
            //返回销售属性中
            attr.setSpuSaleAttrValueList(newSpuSaleAttrValueList);
            //返回属性
            return attr;
        }).collect(Collectors.toList());
    }

    /**
     * 保存spu的图片信息
     * @param spuId
     * @param spuImageList
     * @return
     */
    private List<SpuImage> saveSpuImageList(Long spuId, List<SpuImage> spuImageList){
        return spuImageList.stream().map(image -> {
            //补全spu的id
            image.setSpuId(spuId);
            //新增
            spuImageMapper.insert(image);
            //返回
            return image;
        }).collect(Collectors.toList());
    }

    @Autowired
    private SkuInfoMapper skuInfoMapper;
    /**
     * 分页查询sku的列表
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SkuInfo> pageSkuInfo(Integer page, Integer size) {
        return skuInfoMapper.selectPage(new Page<>(page,size), null);
    }

    /**
     * 查询指定spu的图片信息
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        return spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId, spuId));
    }

    /**
     * 根据spu的id查询该spu的所有的销售属性和销售属性的值
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuId(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuId(spuId);
    }

    @Resource
    private RabbitService rabbitService;

    /**
     * 保存skuinfo
     *
     * @param skuInfo
     * @return
     */
    @Override
    public SkuInfo saveSkuInfo(SkuInfo skuInfo) {
        //参数校验
        if(skuInfo == null){
            throw new RuntimeException("参数错误!!!!");
        }
        //保存skuinfo的信息
        int insert = skuInfoMapper.insert(skuInfo);
        if(insert <= 0 ){
            throw new RuntimeException("保存失败!!!!");
        }
        //获取sku的id
        Long skuId = skuInfo.getId();
        //保存image
        List<SkuImage> skuImageList =
                saveSkuImageList(skuId, skuInfo.getSkuImageList());
        skuInfo.setSkuImageList(skuImageList);
        //保存平台属性
        List<SkuAttrValue> skuAttrValueList =
                saveSkuAttrValue(skuId, skuInfo.getSkuAttrValueList());
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        //保存销售属性
        Long spuId = skuInfo.getSpuId();
        List<SkuSaleAttrValue> skuSaleAttrValueList =
                saveSkuSaleAttrValueList(skuId, spuId, skuInfo.getSkuSaleAttrValueList());
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValueList);
        //消息队列发送消息
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS,MqConst.ROUTING_GOODS_UPPER,skuId);
        //返回
        return skuInfo;
    }

    /**
     * 上架或下架
     *
     * @param skuId
     * @param sale
     * @return
     */
    @Override
    public SkuInfo upOrDown(Long skuId, Short sale) {
        //参数校验
        if(skuId == null || sale == null){
            throw new RuntimeException("参数错误!!!!!!");
        }
        //查询数据
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo == null || skuInfo.getId() == null){
            throw new RuntimeException("数据不存在!!!!!!");
        }
        //修改商品的上下架状态
        skuInfo.setIsSale(sale.intValue());
        skuInfoMapper.updateById(skuInfo);
        //数据同步---跟ES数据库同步---Mq发送消息
        if (sale.intValue()==1){
            //商品上架
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS, MqConst.ROUTING_GOODS_UPPER, skuId);
        }else {
            //商品下架
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS, MqConst.ROUTING_GOODS_LOWER, skuId);
        }
        //返回商品
        return skuInfo;
    }

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    /**
     * 保存sku的销售属性
     * @param skuId
     * @param spuId
     * @param skuSaleAttrValueList
     * @return
     */
    private List<SkuSaleAttrValue> saveSkuSaleAttrValueList(Long skuId,
                                                            Long spuId,
                                                            List<SkuSaleAttrValue> skuSaleAttrValueList) {
        return skuSaleAttrValueList.stream().map(sale ->{
            //补全数据skuid spuid
            sale.setSkuId(skuId);
            sale.setSpuId(spuId);
            //保存数据
            skuSaleAttrValueMapper.insert(sale);
            //返回数据
            return sale;
        }).collect(Collectors.toList());
    }


    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    /**
     * 保存sku的平台属性信息
     * @param skuId
     * @param skuAttrValueList
     * @return
     */
    private List<SkuAttrValue> saveSkuAttrValue(Long skuId, List<SkuAttrValue> skuAttrValueList) {
        return skuAttrValueList.stream().map(attr ->{
            //补全数据
            attr.setSkuId(skuId);
            //保存数据
            skuAttrValueMapper.insert(attr);
            //返回数据
            return attr;
        }).collect(Collectors.toList());
    }


    @Autowired
    private SkuImageMapper skuImageMapper;
    /**
     * 保存sku的图片信息
     * @param skuId
     * @param skuImageList
     * @return
     */
    private List<SkuImage> saveSkuImageList(Long skuId, List<SkuImage> skuImageList) {
        return skuImageList.stream().map(image ->{
            //补全skuid
            image.setSkuId(skuId);
            //保存数据
            skuImageMapper.insert(image);
            //返回
            return image;
        }).collect(Collectors.toList());
    }

    /**
     * 分页查询品牌列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseTrademark> getBaseTrademarkListPage(Integer page, Integer size) {
        IPage<BaseTrademark> baseTrademarkIPage = new Page<>(page,size);
        IPage<BaseTrademark> trademarkIPage = baseTradeMarkMapper.selectPage(baseTrademarkIPage, null);
        return trademarkIPage;
    }
}
