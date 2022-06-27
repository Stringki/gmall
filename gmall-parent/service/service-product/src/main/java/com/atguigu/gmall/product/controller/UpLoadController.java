package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.fastdfs.FastDfsClient;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/admin/product")
public class UpLoadController {

    @Value("${fileServer.url}")
    private String imageUrl;

    @Resource
    private FastDfsClient fastDfsClient;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping(value = "/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file) throws Exception{
        String upload = fastDfsClient.upload(file);
        return imageUrl + upload;
    }
}
