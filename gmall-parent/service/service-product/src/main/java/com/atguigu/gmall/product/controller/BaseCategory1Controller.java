package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 一级分类的controller控制层
 */
@RestController
@RequestMapping(value = "/api/product/category1")
public class BaseCategory1Controller {

    @Autowired
    private BaseCategory1Service baseCategory1Service;

    /**
     * 根据一级分类的id查询一级分类的详情
     * @param id
     * @return
     */
    @GetMapping(value = "/findById/{id}")
    public Result<BaseCategory1> findById(@PathVariable(value = "id") Long id){
        BaseCategory1 category1 = baseCategory1Service.getById(id);
        return Result.ok(category1);
    }

    /**
     * 查询全部的一级分类
     * @return
     */
    @GetMapping(value = "/findAll")
    public Result<List<BaseCategory1>> findAll(){
        List<BaseCategory1> baseCategory1List = baseCategory1Service.list(null);
        return Result.ok(baseCategory1List);
    }


    /**
     * 新增一级分类
     * @param baseCategory1
     * @return
     */
    @PostMapping
    public Result insert(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.save(baseCategory1);
        return Result.ok();
    }

    /**
     * 修改一级分类
     * @param baseCategory1
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.updateById(baseCategory1);
        return Result.ok();
    }

    /**
     * 删除一级分类
     * @param id
     * @return
     */
    @DeleteMapping(value = "/del/{id}")
    public Result delete(@PathVariable(value = "id")Long id){
        baseCategory1Service.removeById(id);
        return Result.ok();
    }

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<BaseCategory1>> search(@RequestBody BaseCategory1 baseCategory1){
        List<BaseCategory1> search = baseCategory1Service.search(baseCategory1);
        return Result.ok(search);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/page/{page}/{size}")
    public Result<IPage<BaseCategory1>> page(@PathVariable(value = "page") Integer page,
                                             @PathVariable(value = "size") Integer size){
        IPage<BaseCategory1> pageList = baseCategory1Service.page(page, size);
        return Result.ok(pageList);
    }

    /**
     * 分页条件查询
     * @param baseCategory1
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result<IPage<BaseCategory1>> search(@RequestBody BaseCategory1 baseCategory1,
                                              @PathVariable(value = "page") Integer page,
                                              @PathVariable(value = "size") Integer size){
        IPage<BaseCategory1> search = baseCategory1Service.search(baseCategory1, page, size);
        return Result.ok(search);
    }
}
