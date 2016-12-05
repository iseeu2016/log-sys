package cn.yunyichina.log.service.searcherGateway.service;

import cn.yunyichina.log.common.entity.dto.Response;
import cn.yunyichina.log.common.entity.dto.SearchCondition;
import cn.yunyichina.log.common.entity.po.StoreRecord;
import cn.yunyichina.log.service.searcherGateway.mapper.StoreRecordMapper;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * @Author: Leo
 * @Blog: http://blog.csdn.net/lc0817
 * @CreateTime: 2016/11/28 9:56
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Throwable.class, Exception.class, RuntimeException.class})
public class DispatchService {

    @Autowired
    StoreRecordMapper storeRecordMapper;

    @Transactional(readOnly = true)
    public Response dispatch(SearchCondition condition) throws IOException {
        StoreRecord storeRecordParam = new StoreRecord();
        storeRecordParam.setCollector_id(condition.getCollector().getId());

        StoreRecord storeRecord = storeRecordMapper.selectOne(storeRecordParam);

        String outerIp = storeRecord.getOuter_ip();
        Integer outerPort = storeRecord.getOuter_port();

        String url = "http://" + outerIp + ":" + outerPort + "/search/history";

        Content content = Request.Post(url)
                .bodyString(JSON.toJSONString(condition), ContentType.APPLICATION_JSON)
                .execute()
                .returnContent();

        String respJson = content.asString(Charsets.UTF_8);
        return JSON.parseObject(respJson, Response.class);
    }

}