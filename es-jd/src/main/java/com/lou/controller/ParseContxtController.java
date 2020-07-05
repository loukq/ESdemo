package com.lou.controller;


import com.lou.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ParseContxtController {

    @Autowired
    private ContextService contextService;

//    1、获取到数据，写入es中
    @GetMapping("/parse/{keyWord}")
    public Boolean parse(@PathVariable("keyWord") String keyWord) throws IOException {
        return contextService.parseUrl(keyWord);
    }


//    2、指定关键字查询
    @GetMapping("/search/{keyWord}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(
            @PathVariable("keyWord") String keyWord,
            @PathVariable("pageNo") int pageNo,
            @PathVariable("pageSize") int pageSize) throws IOException {
        return contextService.search(keyWord,pageNo,pageSize);
    }


}
