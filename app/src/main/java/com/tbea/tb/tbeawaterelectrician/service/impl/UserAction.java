package com.tbea.tb.tbeawaterelectrician.service.impl;

import com.google.gson.reflect.TypeToken;
import com.tbea.tb.tbeawaterelectrician.entity.Commodith;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.entity.NearbyCompany;
import com.tbea.tb.tbeawaterelectrician.entity.Register;
import com.tbea.tb.tbeawaterelectrician.entity.SuYuan;
import com.tbea.tb.tbeawaterelectrician.entity.Take;
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo2;
import com.tbea.tb.tbeawaterelectrician.http.MD5Util;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abc on 17/1/14.
 */

public class UserAction extends BaseAction {
    /**
     * 登录
     * @param phone 用户名
     * @param pwd 密码
     * @return
     * @throws Exception
     */
    public RspInfo login(String phone,String pwd) throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mobilenumber", phone));
        pairs.add(new BasicNameValuePair("userpas", MD5Util.getMD5String(pwd)));
        String result = sendRequest("TBEAENG001001004000",pairs);
        rspInfo = gson.fromJson(result,new TypeToken<RspInfo<UserInfo2>>(){}.getType());
        return  rspInfo;

    }

    /**
     * 发送验证码
     * servicecode 判断是哪个接口获取的验证码
     */
    public RspInfo1 sendCode(String mobile, String servicecode) throws Exception{
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mobile", mobile));
        pairs.add(new BasicNameValuePair("servicecode", servicecode));
        String result = sendRequest("TBEAENG001001001000",pairs);
        rspInfo = gson.fromJson(result,RspInfo1.class);
        return  rspInfo;
    }

    /**
     * 注册
     */
    public RspInfo1 register(Register register) throws  Exception{
        RspInfo1 rspInfo;
        Map<String,String> paramsIn=new HashMap<>();
        Map<String,String> fileIn=new HashMap<>();
//        paramsIn.put("mobile",register.getMobile());
//        paramsIn.put("password",MD5Util.getMD5String(register.getPassword()));
//        paramsIn.put("verifycode",register.getVerifycode());
//        paramsIn.put("realname",register.getRealname());
//        paramsIn.put("personid",register.getPersonid());
         paramsIn.put("mobile","123456");
         paramsIn.put("password",MD5Util.getMD5String("123456"));
         paramsIn.put("verifycode","123456");
         paramsIn.put("realname","测试");
        paramsIn.put("personid","53019");
        fileIn.put("personidcard1",register.getPersonidcard1());
        fileIn.put("personidcard2",register.getPersonidcard2());
        fileIn.put("personidcardwithperson",register.getPersonidcardwithperson());
        String result = regist("TBEAENG001001002000",paramsIn,fileIn);
        rspInfo = gson.fromJson(result,RspInfo1.class);
        return  rspInfo;
    }

    /**
     * 首页
     */
    public RspInfo getMianDate(String cityname,String cityid,int page, int pagesize )throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("cityname", cityname));
        pairs.add(new BasicNameValuePair("cityid", cityid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pagesize)));
        String result = sendRequest("TBEAENG002001001000",pairs);
        rspInfo = gson.fromJson(result,new TypeToken<RspInfo<Object>>(){}.getType());
        return  rspInfo;
    }

    /**
     * 获取查询条件的列表（如经销商类型，区域类型等）
     */
    public RspInfo getFranchiserType(String  methodName) throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest(methodName,pairs);
        rspInfo = gson.fromJson(result,new TypeToken<RspInfo<List<Condition>>>(){}.getType());
        return  rspInfo;
    }

    /**
     * 获取查询条件的列表（如经销商类型，区域类型等）
     */
    public RspInfo getLocationList(String  cityName) throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("cityname", cityName));
        String result = sendRequest("TBEAENG003001002000",pairs);
        rspInfo = gson.fromJson(result,new TypeToken<RspInfo<List<Condition>>>(){}.getType());
        return  rspInfo;
    }

    /**
     * 获取附近经销商
     * @param companyTypeId 商家类型ID
     * @param locationid 区域类型ID
     * @param certifiedstatusid 认证类型ID
     */
    public RspInfo getCompanyList(String companyTypeId,String locationid,String certifiedstatusid,String cityname,String cityid,int page,int pageSize) throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("companytypeid", companyTypeId));
        pairs.add(new BasicNameValuePair("locationid", locationid));
        pairs.add(new BasicNameValuePair("certifiedstatusid", certifiedstatusid));
        pairs.add(new BasicNameValuePair("cityname", cityname));
        pairs.add(new BasicNameValuePair("cityid", cityid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001004000",pairs);
        rspInfo = gson.fromJson(result,new TypeToken<RspInfo<Object>>(){}.getType());
        return  rspInfo;
    }

    /**
     * 获取附近商家列表
     * @param companyTypeId 商家类型ID
     * @param locationid 区域类型ID
     * @param certifiedstatusid 认证类型ID
     */
    public RspInfo getShopList(String companyTypeId,String locationid,String certifiedstatusid,String cityname,String cityid,int page,int pageSize) throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("companytypeid", companyTypeId));
        pairs.add(new BasicNameValuePair("locationid", locationid));
        pairs.add(new BasicNameValuePair("certifiedstatusid", certifiedstatusid));
        pairs.add(new BasicNameValuePair("cityname", cityname));
        pairs.add(new BasicNameValuePair("cityid", cityid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001008000",pairs);
        rspInfo = gson.fromJson(result,new TypeToken<RspInfo<Object>>(){}.getType());
        return  rspInfo;
    }

    /**
     * 获取附近采购列表
     * @param categoryid 商品类型ID
     * @param brandid 品牌类型ID
     * @param locationid 区域类型ID
     */
    public RspInfo getCommodithList(String categoryid,String brandid,String locationid,int page,int pageSize) throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("categoryid", categoryid));
        pairs.add(new BasicNameValuePair("brandid", brandid));
        pairs.add(new BasicNameValuePair("locationid", locationid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001009000",pairs);
        rspInfo = gson.fromJson(result,new TypeToken<RspInfo<List<Commodith>>>(){}.getType());
        return  rspInfo;
    }


    /**
     * 获取任务列表（接活页面）
     * @param tasktypeid 任务类型
     * @param locationid 区域类型
     * @param timescopeid 时间类型
     */
    public RspInfo getTakeList(String tasktypeid,String locationid,String timescopeid,String cityname,String cityid,int page,int pageSize) throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("tasktypeid", tasktypeid));
        pairs.add(new BasicNameValuePair("locationid", locationid));
        pairs.add(new BasicNameValuePair("timescopeid", timescopeid));
        pairs.add(new BasicNameValuePair("cityname", cityname));
        pairs.add(new BasicNameValuePair("cityid", cityid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG004001003000",pairs);
        rspInfo = gson.fromJson(result,new TypeToken<RspInfo<List<Take>>>(){}.getType());
        return  rspInfo;
    }

    /**
     * 获取用户信息
     */
    public String getUserInfo() throws Exception{
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001001000",pairs);
        return result;
    }

    /**
     * 获取城市列表
     */
    public RspInfo getCityList(String cityName) throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("cityname", cityName));
        String result = sendRequest("TBEAENG002001002000",pairs);
        rspInfo = gson.fromJson(result,new TypeToken<RspInfo<List<Condition>>>(){}.getType());
        return  rspInfo;
    }

    /**
     *
     二维码有效性检验：
     当扫描完成后需要先将此二维码发过去验证此二维码是否有效，
     如果 有效即跳转到对应的页面上
     * @param scanCode 扫码后获得的码
     * @param scanCodeType 当前类型  fanli: 返利; suyuan: 溯源
     */
    public  RspInfo1 provingScanCode(String scanCode,String scanCodeType) throws Exception{
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scancode", scanCode));
        pairs.add(new BasicNameValuePair("scancodetypeid", scanCodeType));
        String result = sendRequest("TBEAENG006000001000",pairs);
        rspInfo = gson.fromJson(result,RspInfo1.class);
        return  rspInfo;
    }

    /**
     *
     溯源详情接口
     * @param scanCode 扫码获取到的码
     * @param address 扫码地点详情
     */
    public  RspInfo1 getSuYuan(String scanCode,String address) throws Exception{
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scancode", scanCode));
        pairs.add(new BasicNameValuePair("address", address));
        String result = sendRequest("TBEAENG006001001000",pairs);
        rspInfo = gson.fromJson(result,RspInfo1.class);
        return  rspInfo;
    }

    /**
     *
     返利详情接口
     * @param scanCode 扫码获取到的码
     * @param address 扫码地点详情
     */
    public  RspInfo1 getFanLi(String scanCode,String address) throws Exception{
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scancode", scanCode));
        pairs.add(new BasicNameValuePair("address", address));
        String result = sendRequest("TBEAENG006001002000",pairs);
        rspInfo = gson.fromJson(result,RspInfo1.class);
        return  rspInfo;
    }

}
