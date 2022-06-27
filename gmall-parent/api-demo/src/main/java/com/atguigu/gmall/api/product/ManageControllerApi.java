package com.atguigu.gmall.api.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * controller的接口类
 */
@Api(tags = "后台管理接口")
public interface ManageControllerApi {

    /**
     * 查询所有的一级分类
     * @return
     */
    @ApiOperation(value = "查询所有的一级分类")
    public Result<List<BaseCategory1>> getCategory1();

    /**
     * 根据一级分类查询所有的二级分类
     * @return
     */
    @ApiOperation(value = "根据一级分类查询所有的二级分类")
    public Result<List<BaseCategory2>> getCategory2(Long cid);

    /**
     * 根据二级分类查询所有的三级分类
     * @return
     */
    @ApiOperation(value = "根据二级分类查询所有的三级分类")
    public Result<List<BaseCategory3>> getCategory3(Long cid);

    /**
     * 根据分类查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @ApiOperation(value = "根据分类查询平台属性列表")
    public Result<List<BaseAttrInfo>> findBaseAttrInfo(Long category1Id,
                                                       Long category2Id,
                                                       Long category3Id);


    /**
     * 新增平台属性
     * @param baseAttrInfo
     * @return
     */
    @ApiOperation(value = "新增平台属性")
    public Result<BaseAttrInfo> saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台属性名称的id查询平台属性的值列表
     * @param id
     * @return
     */
    @ApiOperation(value = "根据平台属性名称的id查询平台属性的值列表")
    public Result<List<BaseAttrValue>> getAttrValueList(Long id);
}
