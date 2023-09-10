package com.huo.controller;

import com.huo.common.GeneralResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {

    @Value("${xd.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public GeneralResult<String> upload(MultipartFile file) {
//        file只是一个临时文件，需要转存到指定位置，否则本次请求完成后就会消失
//        log.info(file.toString());

//        拿到文件的原始名
        String originalFileName = file.getOriginalFilename();
//        拿到文件的后缀名，例如 .png/.jpg
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
//        使用UUID作为文件名，防止重复命名造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

//        查看目录结构是否存在，不存在的话创建
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
//            把前端传过来的临时文件进行转存
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return GeneralResult.success(fileName);
    }


    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
         try {
            //输入流，通过输入流读取文件内容  这里的name是前台用户需要下载的文件的文件名
            //new File(basePath + name) 是为了从存储图片的地方获取用户需要的图片对象
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            //设置写回去的文件类型
            response.setContentType("image/jpeg");

            //定义缓存区，准备读写文件
            int len  = 0 ;
            byte[] buff = new byte[1024];
            while ((len = fileInputStream.read(buff)) != -1){
                outputStream.write(buff,0,len);
                outputStream.flush();
            }
            //关流
            outputStream.close();
            fileInputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}

