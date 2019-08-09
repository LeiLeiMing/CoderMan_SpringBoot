package com.zhangyu.coderman.controller;

import com.zhangyu.coderman.dto.FileDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {

    @ResponseBody
    @RequestMapping(value = "/question/fileupload",method = RequestMethod.POST)
    public FileDTO uploadImg(MultipartFile file){
        System.out.println(file.getOriginalFilename());
        FileDTO fileDTO = new FileDTO();
        fileDTO.setMessage("上传成功");
        fileDTO.setSuccess(1);
        fileDTO.setUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=508387608,2848974022&fm=26&gp=0.jpg");
        return fileDTO;
    }
}
