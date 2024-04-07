package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadCallback;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.dao.FaCourierCompanyDao;
import com.cmhq.core.dao.FaUploadLogDao;
import com.cmhq.core.model.FaCourierCompanyEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.spring.SpringContextUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    public UploadResult execute(Req o) {
        List<T> listMap;
        try {
            listMap = getData(o);
        }catch (Exception e){
            log.error("上传失败，param【"+ JSONObject.toJSONString(o) +"】，msg【"+e.getMessage()+"】",e);
            if (e.getCause() != null){
                return UploadResult.builder().failCount(0).errorMsg(e.getCause().getMessage()).build();
            }
            return UploadResult.builder().failCount(0).errorMsg(e.getMessage()).build();
        }
        return upload( listMap);
    }



    @SneakyThrows
    public UploadResult upload(List<T> list) {
        UploadResult result = UploadResult.builder().build();
        int total = list.size();
        log.info("期望上传【{}】条数据至【{}】", total,supports().getDesc());
        int successNum = 0;
        List<String> failUks = Lists.newArrayList();
        List<String> successUks = Lists.newArrayList();
        //遍历不同账号下的数据
        List<T> uploadData = Lists.newArrayList();
        for (int i = 0, j = list.size(); i < j; i++) {
            uploadData.add(list.get(i));
            log.info("mark:{},上传 total:{},currentIndex:{}", list.get(i).getUnKey(),list.size(),i);
            int finalI = i;
            int[] ints = doUpload(uploadData,  new UploadCallback() {
                @Override
                public void success(Object obj) {
                    successUks.add(list.get(finalI).getUnKey());
                }
                @Override
                public void fail(Object obj, Exception e) {
                    result.setErrorMsg(obj+"");
                    failUks.add(list.get(finalI).getUnKey());
                }
            });
            uploadData.clear();
            successNum += ints[0];
        }

        log.info("【{}】条数据成功上传至【{}】", successNum,supports().getDesc());
        result.setSuccessCount(successNum).setFailCount(total - successNum);
        result.setFailUks(failUks);
        result.setSuccessUks(successUks);
        return result;
    }


    @Transactional
    public  <T extends UploadData> int[] uploadResultHandle(List<T> uploadData, UploadResult uploadResult){
        if (CollectionUtils.isEmpty(uploadData) || uploadResult == null){
            return new int[]{0,0};
        }
        //上传结果处理
        Set<String> uploadKeys = uploadData.stream().map(T::getUnKey).collect(Collectors.toSet());
        //上传全部失败
        Set<String> uploadFail = getUploadFail(uploadResult, uploadKeys);
        Sets.SetView<String> uploadSuccess = Sets.difference(uploadKeys, uploadFail);
        //状态同步
//        syncTransferStatus(Lists.newArrayList(uploadSuccess), TransferStatusEnum.UPLOAD.getCode());TODO
//        syncTransferStatus(Lists.newArrayList(uploadFail), TransferStatusEnum.UPLOAD_ERROR.getCode());
        int successNum = uploadSuccess.size();
        int failNum = uploadFail.size();
        //日志记录
//        log( uploadResult, uploadKeys);
        return new int[]{successNum, failNum};
    }

    protected abstract List<T> getData(Req param) throws RuntimeException;

    /**
     * 执行上传
     *
     * @param uploadData
     * @return
     */
    protected abstract  <T extends UploadData> int[] doUpload(List<T> uploadData,  UploadCallback callback);
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
    protected abstract void syncTransferStatus(List<String> code, String status);


    protected String getToken(){
        FaCourierCompanyDao faCourierCompanyDao = SpringContextUtil.getBean(FaCourierCompanyDao.class);
        return faCourierCompanyDao.selectOne(new LambdaQueryWrapper<FaCourierCompanyEntity>().eq(FaCourierCompanyEntity::getCourierCode, supports().getCourierCompanyCode())).getTokenInfo();
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
