package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadCallback;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.dao.FaCourierCompanyDao;
import com.cmhq.core.dao.FaUploadLogDao;
import com.cmhq.core.model.FaCourierCompanyEntity;
import com.cmhq.core.model.UploadLogEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.spring.SpringContextUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Jiyang.Zheng on 2024/4/7 10:36.
 */
@Slf4j
@Component
public abstract class AbstractUpload<Req,T extends UploadData> implements Upload<Req>  {
    @Autowired
    private FaUploadLogDao faUploadLogDao;
    @Autowired
    FaCourierCompanyDao faCourierCompanyDao;

    @Override
    public UploadResult execute(Req o) {
        T dto;
        try {
            dto = getData(o);
        }catch (Exception e){
            log.error("上传失败，param【"+ JSONObject.toJSONString(o) +"】，msg【"+e.getMessage()+"】",e);
            if (e.getCause() != null){
                return UploadResult.builder().failCount(0).errorMsg(e.getCause().getMessage()).build();
            }
            return UploadResult.builder().failCount(0).errorMsg(e.getMessage()).build();
        }
        return upload( dto);
    }



    @SneakyThrows
    public UploadResult upload(T dto) {
        UploadResult result =  send(dto,  new UploadCallback() {
            @Override
            public void success(Object obj) {
            }
            @Override
            public void fail(Object obj, Exception e) {
            }
        });
        uploadResultHandle(dto, result);
        return result;
    }


    public  void uploadResultHandle(T uploadData, UploadResult uploadResult){
        UploadLogEntity log = new UploadLogEntity();
        log.setUploadType(supports().getCode());
        log.setUploadCode(uploadData.getUnKey());
        log.setMsg(uploadResult.getJsonMsg()+"");
        log.setErrorMsg(uploadResult.getErrorMsg()+":"+uploadResult.getErrorJsonMsg());
        log.setCreateTime(new Date());
        faUploadLogDao.insert(log);

    }

    protected abstract T getData(Req param) throws RuntimeException;


    /**
     * 执行上传
     *
     * @param uploadData
     * @return
     */
    protected abstract  UploadResult  send(T uploadData,  UploadCallback callback);
    /**
     * 获取上传Url
     * @return
     */
    protected abstract String getUploadUrl();

    /**
     * 上传状态同步
     * @param code
     * @param status
     */
    protected  void syncTransferStatus(List<String> code, String status){};
    /**
     * 计算签名值
     *
     * @param content  请求报文体
     * @param secretKey    配置的私钥
     * @return
     */
    public static String calculateDigest(String content, String secretKey) {
        String text = content + secretKey;
        byte[] md5 = DigestUtils.md5(text);
        return Base64.encodeBase64String(md5);
    }

    protected String getToken(){
        return faCourierCompanyDao.selectOne(new LambdaQueryWrapper<FaCourierCompanyEntity>().eq(FaCourierCompanyEntity::getCourierCode, supports().getCourierCompanyCode())).getTokenInfo();
    }
    protected String getApiHost(){
        return faCourierCompanyDao.selectOne(new LambdaQueryWrapper<FaCourierCompanyEntity>().eq(FaCourierCompanyEntity::getCourierCode, supports().getCourierCompanyCode())).getApiUrl();
    }

    /**
     * 取出上传失败的数据
     *
     * @param result
     * @return
     */
    private Set<String> getUploadFail(UploadResult result, Set<String> raw) {
        Set<String> uploadFail;
        //上传全部失败
        if (!result.getFlag() && CollectionUtils.isEmpty(result.getFail())) {
            uploadFail = raw;
        } else {
            uploadFail = Sets.newHashSet(result.getFail());
        }
        return uploadFail;
    }


}
