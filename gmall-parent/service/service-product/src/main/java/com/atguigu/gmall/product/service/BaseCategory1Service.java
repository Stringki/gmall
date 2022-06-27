package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 一级分类的service的接口类
 */
public interface BaseCategory1Service extends IService<BaseCategory1> {

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    public List<BaseCategory1> search(BaseCategory1 baseCategory1);

    /**
     * 分页查询
     * @param page :页码
     * @param size :每页显示数据的行数
     * @return
     */
    public IPage<BaseCategory1> page(Integer page, Integer size);

    /**
     *
     * @param baseCategory1
     * @param page :页码
     * @param size :每页显示数据的行数
     * @return
     */
    public IPage<BaseCategory1> search(BaseCategory1 baseCategory1,
                        Integer page,
                        Integer size);
}
