package com.atguigu.gmall.common.fastdfs;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FastDfsClient {


    @Resource
    private FastFileStorageClient fastFileStorageClient;


    //文件上传
    public String upload(MultipartFile file) throws IOException{
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), StringUtils.getFilenameExtension(file.getOriginalFilename()), null);
        return storePath.getFullPath();

    }

    //文件下载
    public byte[] download(String groupName, String path) throws IOException {

        InputStream inputStream = fastFileStorageClient.downloadFile(groupName, path, new DownloadCallback<InputStream>() {
            @Override
            public InputStream recv(InputStream ins) throws IOException {
                return ins;
            }
        });
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1){
            byteArrayOutputStream.write(buffer,0,length);
        }
        return byteArrayOutputStream.toByteArray();
    }

    //文件删除
    public void delete(String groupName,String path){
        fastFileStorageClient.deleteFile(groupName,path);
    }


}
