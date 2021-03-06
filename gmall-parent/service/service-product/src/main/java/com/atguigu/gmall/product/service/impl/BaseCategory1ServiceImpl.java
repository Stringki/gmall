package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 一级分类的service的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseCategory1ServiceImpl
        extends ServiceImpl<BaseCategory1Mapper, BaseCategory1>
        implements BaseCategory1Service {

    /**
     * 条件查询
     *
     * @param baseCategory1
     * @return
     */
    @Override
    public List<BaseCategory1> search(BaseCategory1 baseCategory1) {
        //参数校验
        if(baseCategory1 == null){
            return list(null);
        }
        //拼接条件
        LambdaQueryWrapper wrapper = buildQueryWapper(baseCategory1);
        //执行查询获取结果
        List<BaseCategory1> list = list(wrapper);
        //返回
        return list;
    }

    /**
     * 分页查询
     *
     * @param page :页码
     * @param size :每页显示数据的行数
     * @return
     */
    @Override
    public IPage<BaseCategory1> page(Integer page, Integer size) {
        //校验页码
        if(page == null ){
            page = 1;
        }
        //校验每页显示的行数
        if(size == null){
            size = 10;
        }
        //分页查询
        Page<BaseCategory1> pageInfo = new Page<>(page, size);
        return page(pageInfo, null);
    }

    /**
     * @param baseCategory1
     * @param page :页码
     * @param size :每页显示数据的行数
     * @return
     */
    @Override
    public IPage<BaseCategory1> search(BaseCategory1 baseCategory1,
                        Integer page,
                        Integer size) {
        //拼接条件
        LambdaQueryWrapper wrapper = buildQueryWapper(baseCategory1);
        //分页条件构建
        Page<BaseCategory1> pageInfo = new Page<>(page, size);
        //执行查询
        return page(pageInfo, wrapper);
    }

    /**
     * 条件拼接
     * @param baseCategory1
     * @return
     */
    private LambdaQueryWrapper buildQueryWapper(BaseCategory1 baseCategory1){
        //声明条件构造器
        LambdaQueryWrapper<BaseCategory1> wrapper = new LambdaQueryWrapper<>();
        //拼接条件id
        if(baseCategory1.getId() != null){
            wrapper.eq(BaseCategory1::getId, baseCategory1.getId());
        }
        //拼接条件name
        if(!StringUtils.isEmpty(baseCategory1.getName())){
            wrapper.like(BaseCategory1::getName, baseCategory1.getName());
        }
        //返回拼接条件
        return wrapper;
    }
}
