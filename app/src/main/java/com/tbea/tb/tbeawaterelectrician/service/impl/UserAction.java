package com.tbea.tb.tbeawaterelectrician.service.impl;

import com.google.gson.reflect.TypeToken;
import com.tbea.tb.tbeawaterelectrician.activity.my.model.MessageListResponseModel;
import com.tbea.tb.tbeawaterelectrician.activity.my.model.MessageTypeListResponseModel;
import com.tbea.tb.tbeawaterelectrician.entity.Address;
import com.tbea.tb.tbeawaterelectrician.entity.Appeal;
import com.tbea.tb.tbeawaterelectrician.entity.Collect;
import com.tbea.tb.tbeawaterelectrician.entity.Commodith;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.entity.Distributor;
import com.tbea.tb.tbeawaterelectrician.entity.MessageCategory;
import com.tbea.tb.tbeawaterelectrician.entity.Receive;
import com.tbea.tb.tbeawaterelectrician.entity.Register;
import com.tbea.tb.tbeawaterelectrician.entity.Take;
import com.tbea.tb.tbeawaterelectrician.entity.TakeMoney;
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo;
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
     *
     * @param phone 用户名
     * @param pwd   密码
     * @return
     * @throws Exception
     */
    public RspInfo1 login(String phone, String pwd) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mobilenumber", phone));
        pairs.add(new BasicNameValuePair("userpas", MD5Util.getMD5String(pwd)));
        String result = sendRequest("TBEAENG001001004000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
//        if(re.isSuccess()){
//
//        }
//        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<UserInfo2>>() {
//        }.getType());
        return rspInfo;

    }

    /**
     * 获取是否有app更新
     *
     * @return
     * @throws Exception
     */
    public RspInfo1 getUpdate() throws Exception {
        RspInfo1 model;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("terminaltype", "android"));
        String result = sendRequest("TBEAENG015001001000", pairs);
        model = gson.fromJson(result, RspInfo1.class);
        return model;
    }

    /**
     * 发送验证码
     * servicecode 判断是哪个接口获取的验证码
     */
    public RspInfo1 sendCode(String mobile, String servicecode) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mobile", mobile));
        pairs.add(new BasicNameValuePair("servicecode", servicecode));
        String result = sendRequest("TBEAENG001001001000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 注册
     */
    public RspInfo1 register(Register register) throws Exception {
        RspInfo1 rspInfo;
        Map<String, String> paramsIn = new HashMap<>();
        Map<String, String> fileIn = new HashMap<>();
        paramsIn.put("mobile", register.getMobile());
        paramsIn.put("password", MD5Util.getMD5String(register.getPassword()));
        paramsIn.put("verifycode", register.getVerifycode());
        paramsIn.put("realname", register.getRealname());
        paramsIn.put("personid", register.getPersonid());
        fileIn.put("personidcard1", register.getPersonidcard1());
        fileIn.put("personidcard2", register.getPersonidcard2());
        fileIn.put("personidcardwithperson", register.getPersonidcardwithperson());
        String result = regist("TBEAENG001001002000", paramsIn, fileIn);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 首页
     */
    public RspInfo1 getMianDate(String cityname, String cityid, int page, int pagesize) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("cityname", cityname));
        pairs.add(new BasicNameValuePair("cityid", cityid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pagesize)));
        String result = sendRequest("TBEAENG002001001000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取查询条件的列表（如经销商类型，区域类型等）
     */
    public RspInfo getFranchiserType(String methodName) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest(methodName, pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Condition>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取查询条件的列表（如经销商类型，区域类型等）
     */
    public RspInfo getLocationList(String cityName) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("cityname", cityName));
        String result = sendRequest("TBEAENG003001002000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Condition>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取附近经销商
     *
     * @param companyTypeId     商家类型ID
     * @param locationid        区域类型ID
     * @param certifiedstatusid 认证类型ID
     */
    public RspInfo getCompanyList(String companyTypeId, String locationid, String certifiedstatusid, String cityname, String cityid, int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("companytypeid", companyTypeId));
        pairs.add(new BasicNameValuePair("locationid", locationid));
        pairs.add(new BasicNameValuePair("certifiedstatusid", certifiedstatusid));
        pairs.add(new BasicNameValuePair("cityname", cityname));
        pairs.add(new BasicNameValuePair("cityid", cityid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001004000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<Object>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取附近商家列表
     *
     * @param companyTypeId     商家类型ID
     * @param locationid        区域类型ID
     * @param certifiedstatusid 认证类型ID
     */
    public RspInfo getShopList(String companyTypeId, String locationid, String certifiedstatusid, String cityname, String cityid, int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("companytypeid", companyTypeId));
        pairs.add(new BasicNameValuePair("locationid", locationid));
        pairs.add(new BasicNameValuePair("certifiedstatusid", certifiedstatusid));
        pairs.add(new BasicNameValuePair("cityname", cityname));
        pairs.add(new BasicNameValuePair("cityid", cityid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001008000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<Object>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取附近采购列表
     *
     * @param categoryid 商品类型ID
     * @param brandid    品牌类型ID
     * @param locationid 区域类型ID
     */
    public RspInfo getCommodithList(String categoryid, String brandid, String locationid, int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("categoryid", categoryid));
        pairs.add(new BasicNameValuePair("brandid", brandid));
        pairs.add(new BasicNameValuePair("locationid", locationid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001009000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Commodith>>>() {
        }.getType());
        return rspInfo;
    }


    /**
     * 获取任务列表（接活页面）
     *
     * @param tasktypeid  任务类型
     * @param locationid  区域类型
     * @param timescopeid 时间类型
     */
    public RspInfo getTakeList(String tasktypeid, String locationid, String timescopeid, String cityname, String cityid, int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("tasktypeid", tasktypeid));
        pairs.add(new BasicNameValuePair("locationid", locationid));
        pairs.add(new BasicNameValuePair("timescopeid", timescopeid));
        pairs.add(new BasicNameValuePair("cityname", cityname));
        pairs.add(new BasicNameValuePair("cityid", cityid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG004001003000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Take>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取用户信息
     */
    public RspInfo1 getUserInfo() throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001001000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取城市列表
     */
    public RspInfo getCityList(String cityName) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("cityname", cityName));
        String result = sendRequest("TBEAENG002001002000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Condition>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 二维码有效性检验：
     * 当扫描完成后需要先将此二维码发过去验证此二维码是否有效，
     * 如果 有效即跳转到对应的页面上
     *
     * @param scanCode     扫码后获得的码
     * @param scanCodeType 当前类型  fanli: 返利; suyuan: 溯源
     */
    public RspInfo1 provingScanCode(String scanCode, String scanCodeType) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scancode", scanCode));
        pairs.add(new BasicNameValuePair("scancodetypeid", scanCodeType));
        String result = sendRequest("TBEAENG006000001000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 溯源详情接口
     *
     * @param scanCode 扫码获取到的码
     * @param address  扫码地点详情
     */
    public RspInfo1 getSuYuan(String scanCode, String address) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scancode", scanCode));
        pairs.add(new BasicNameValuePair("address", address));
        String result = sendRequest("TBEAENG006001001000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 返利详情接口
     *
     * @param scanCode 扫码获取到的码
     * @param address  扫码地点详情
     */
    public RspInfo1 getFanLi(String scanCode, String address) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scancode", scanCode));
        pairs.add(new BasicNameValuePair("address", address));
        String result = sendRequest("TBEAENG006001002000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 返利确认接口
     *
     * @param scanCode 扫码获取到的码
     */
    public RspInfo1 fanLiComfirm(String scanCode) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scancode", scanCode));
        String result = sendRequest("TBEAENG006001002001", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 扫码记录
     */
    public RspInfo1 getScanCodeList(int page, int pageSize) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG006001003000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 扫码记录删除
     */
    public RspInfo1 delectScanCode(String scancodeid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scancodeid", scancodeid));
        String result = sendRequest("TBEAENG006001004000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 我的钱包删除提取二维码
     */
    public RspInfo1 delectTakeMoneyCodeId(String takemoneycodeid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("takemoneycodeid", takemoneycodeid));
        String result = sendRequest("TBEAENG005001210000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 我的钱包收入列表接口
     */
    public RspInfo1 getWalletRevenueList(int page, int pageSize) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG005001006000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取收支明细列表
     */
    public RspInfo getPayRevenue(int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG005001006001", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Receive>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取提现成功历史列表接口
     */
    public RspInfo getWalletIncomeAndExpensesList(int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG005001120000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<TakeMoney>>>() {
        }.getType());
//        rspInfo = gson.fromJson(result,RspInfo1.class);
        return rspInfo;
    }

    /**
     * 提现成功历史记录删除
     */
    public RspInfo1 delectTakeMoney(String takemoneyid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("takemoneyid", takemoneyid));
        String result = sendRequest("TBEAENG005001130000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 我的钱包支出列表接口
     */
    public RspInfo1 getWalletPayList(int page, int pageSize) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG005001008000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 我的举报
     */
    public RspInfo1 getMyAccusationList(int page, int pageSize) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG005001024000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取举报内容
     */
    public RspInfo1 getMyAccusationInfo() throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001023002", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取举报详细
     */
    public RspInfo1 getAccusationInfo(String appealid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("appealid", appealid));
        String result = sendRequest("TBEAENG005001025000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取举报类型
     *
     * @return
     * @throws Exception
     */
    public RspInfo getAccusationType() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001023001", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Condition>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 提交举报
     */
    public RspInfo1 submitAppeal(Appeal obj, List<String> images, String scancode) throws Exception {
        RspInfo1 rspInfo;
        Map<String, String> paramsIn = new HashMap<>();
        Map<String, String> fileIn = new HashMap<>();
        paramsIn.put("scancode", scancode);
        paramsIn.put("appealcategoryid", obj.getAppealcategoryid());
        paramsIn.put("appealtime", obj.getAppealtime());
        paramsIn.put("scanaddress", obj.getScanaddress());
        paramsIn.put("provinceid", obj.getProvinceid());
        paramsIn.put("cityid", obj.getCityid());
        paramsIn.put("distributorid", obj.getDistributorid());
        paramsIn.put("commodityid", obj.getCommodityid());
        paramsIn.put("appealcontent", obj.getAppealcontent());
        if (images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                fileIn.put("picture" + i, images.get(i));
            }
        }
        String result = regist("TBEAENG005001023000", paramsIn, fileIn);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 提现金额
     */
    public RspInfo1 getCanexChangeMoney() throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001009000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 生成二维码
     */
    public RspInfo1 createCode(String money, String distributorid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("money", money));
        pairs.add(new BasicNameValuePair("distributorid", distributorid));
        String result = sendRequest("TBEAENG005001010000", pairs);
        //http://www.u-shang.net//enginterface/Uploads/qrcode_fl_anykvv1441693528cadrqh_1489118792.png
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 根据id生成二维码
     */
    public RspInfo1 createCodeById(String takemoneycodeid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("takemoneycodeid", takemoneycodeid));
        String result = sendRequest("TBEAENG005001006002", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }


    /**
     * 提现成功界面获取方法
     */
    public RspInfo1 getCanexChangeMoneySuccess(String takemoneycode) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("takemoneycode", takemoneycode));
        String result = sendRequest("TBEAENG005001110000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取搜索热词接口
     */
    public RspInfo1 getHeatSpeech(String searchtype) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("searchtype", searchtype));
        String result = sendRequest("TBEAENG002001003000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取输入搜索关键词接口
     */
    public RspInfo getSearchList(String searchtype, String keyword) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("searchtype", searchtype));
        pairs.add(new BasicNameValuePair("keyword", keyword));
        String result = sendRequest("TBEAENG002001004000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<Object>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取收藏列表
     */
    public RspInfo getCollectList(int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG005001012000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Collect>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 取消收藏的商品
     */
    public RspInfo1 cancelCollect(String saveids) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("saveids", saveids));
        String result = sendRequest("TBEAENG005001012001", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取客服中心
     */
    public RspInfo getServiceCenterInfo() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001014000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<MessageCategory>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 修改基本信息
     *
     * @param sex        修改性别 先生传male  女士传Female
     * @param email      邮件
     * @param birthday   生日 日
     * @param birthmonth 生日 月
     * @return
     * @throws Exception
     */
    public RspInfo1 updateInfo(String nickname, String sex, String email, String birthday, String birthmonth) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("nickname", nickname));
        pairs.add(new BasicNameValuePair("sex", sex));
        pairs.add(new BasicNameValuePair("email", email));
        pairs.add(new BasicNameValuePair("birthday", birthday));
        pairs.add(new BasicNameValuePair("birthmonth", birthmonth));
        String result = sendRequest("TBEAENG005001002001", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取个人信息
     */
    public RspInfo getUser() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001002000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<UserInfo>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取安全级别及原绑定手机号
     */
    public RspInfo1 getPhoneInfo() throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001015000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * TBEAENG005001005001
     * 更改手机号验证老手机号
     *
     * @param mobile     老的手机号码
     * @param verifycode 验证码
     */
    public RspInfo1 updateOldPhone(String mobile, String verifycode) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mobile", mobile));
        pairs.add(new BasicNameValuePair("verifycode", verifycode));
        String result = sendRequest("TBEAENG005001005000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 找回密码：验证验证码的有效性：
     *
     * @param mobile     老的手机号码
     * @param verifycode 验证码
     */
    public RspInfo1 validateCode(String mobile, String verifycode) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mobile", mobile));
        pairs.add(new BasicNameValuePair("verifycode", verifycode));
        String result = sendRequest("TBEAENG001001005000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 更改手机号验证新手机号
     *
     * @param mobile     新的手机号码
     * @param verifycode 验证码
     */
    public RspInfo1 updateNewPhone(String mobile, String verifycode) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("newmobile", mobile));
        pairs.add(new BasicNameValuePair("verifycode", verifycode));
        String result = sendRequest("TBEAENG005001005001", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 修改密码
     *
     * @param olduserpas 老密码
     * @param newuserpas 新密码
     */
    public RspInfo1 updateNewPwd(String olduserpas, String newuserpas) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("olduserpas", MD5Util.getMD5String(olduserpas)));
        pairs.add(new BasicNameValuePair("newuserpas", MD5Util.getMD5String(newuserpas)));
        String result = sendRequest("TBEAENG001001007000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 忘记密码（修改密码）
     */
    public RspInfo1 resetPwd(String mobile, String password) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mobile", mobile));
        pairs.add(new BasicNameValuePair("password", MD5Util.getMD5String(password)));
        String result = sendRequest("TBEAENG001001006000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取省列表
     */
    public RspInfo getProvinceList() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG002001002001", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Condition>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取市列表
     */
    public RspInfo getCityList2(String provinceid) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("provinceid", provinceid));
        String result = sendRequest("TBEAENG002001002000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Condition>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取收货地址列表
     */
    public RspInfo getAddrList() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001003000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Address>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 添加收货地址
     * contactperson，（收货人）
     * contactmobile，  （电话）
     * provinceid，（选择的省份ID）
     * cityid，  （选择的城市ID）
     * zoneid，  （选择的区域id）
     * adddress，  (
     * 详细地址)
     * isdefault  （是否设置成默认   0 表示非默认  1表示默认）
     */
    public RspInfo1 addAddrss(Address obj) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("contactperson", obj.getContactperson()));
        pairs.add(new BasicNameValuePair("contactmobile", obj.getContactmobile()));
        pairs.add(new BasicNameValuePair("provinceid", obj.getProvinceid()));
        pairs.add(new BasicNameValuePair("cityid", obj.getCityid()));
        pairs.add(new BasicNameValuePair("zoneid", obj.getZoneid()));
        pairs.add(new BasicNameValuePair("address", obj.getAddress()));
        pairs.add(new BasicNameValuePair("isdefault", obj.getIsdefault()));
        String result = sendRequest("TBEAENG005001004000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 修改收货地址
     * contactperson，（收货人）
     * contactmobile，  （电话）
     * provinceid，（选择的省份ID）
     * cityid，  （选择的城市ID）
     * zoneid，  （选择的区域id）
     * adddress，  (
     * 详细地址)
     * isdefault  （是否设置成默认   0 表示非默认  1表示默认）
     */
    public RspInfo1 editAddrss(Address obj) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("receiveaddrid", obj.getId()));
        pairs.add(new BasicNameValuePair("contactperson", obj.getContactperson()));
        pairs.add(new BasicNameValuePair("contactmobile", obj.getContactmobile()));
        pairs.add(new BasicNameValuePair("provinceid", obj.getProvinceid()));
        pairs.add(new BasicNameValuePair("cityid", obj.getCityid()));
        pairs.add(new BasicNameValuePair("zoneid", obj.getZoneid()));
        pairs.add(new BasicNameValuePair("address", obj.getAddress()));
        pairs.add(new BasicNameValuePair("isdefault", obj.getIsdefault()));
        String result = sendRequest("TBEAENG005001004002", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 删除收货地址
     */
    public RspInfo1 delectAddr(String id) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("receiveaddrid", id));
        String result = sendRequest("TBEAENG005001004001", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 总经销商中得商品列表
     * companyid(经销商ID)
     * orderitemid(排序类型id，推荐(auto)(默认),价格(price),销量,(salecount))
     * order(排序类型id，desc（默认）,asc)
     * justforpromotion(只显示促销商品 0不显示(默认)，1显示)
     * page
     * pagesize
     */
    public RspInfo getCommodithList(String companyid, String orderitemid, String order, String justforpromotionint, int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("companyid", companyid));
        pairs.add(new BasicNameValuePair("orderitemid", orderitemid));
        pairs.add(new BasicNameValuePair("order", order));
        pairs.add(new BasicNameValuePair("justforpromotion", justforpromotionint));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001006000", pairs);
        rspInfo = gson.fromJson(result, RspInfo.class);
        return rspInfo;
    }

    /**
     * 总经销商中的获取公司动态
     * companyid(经销商ID)
     * page
     * pagesize
     */
    public RspInfo getNewList(String companyid, int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("companyid", companyid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001007000", pairs);
        rspInfo = gson.fromJson(result, RspInfo.class);
        return rspInfo;
    }

    /**
     * 获取加入购物车的弹出框里面的信息（加入购物车的时候弹出的选择规格，数目，颜色 等项的接口）
     */
    public RspInfo getAddSCInfo(String commodityid) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("commodityid", commodityid));
        String result = sendRequest("TBEAENG003001009003", pairs);
        rspInfo = gson.fromJson(result, RspInfo.class);
        return rspInfo;
    }

    /**
     * 添加购物车
     * distributorid 当总经销商进入的时候需要传经销商的ID,即附近经销商的商品进入经销商详细的时候，其它时候传空
     * commodityid  商品ID
     * specificationid  所选规格ID
     * colorid 所选颜色ID
     * number   数目
     */
    public RspInfo1 addShopCar(String distributorid, String commodityid, String specificationid, String colorid, String number) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("distributorid", distributorid));
        pairs.add(new BasicNameValuePair("commodityid", commodityid));
        pairs.add(new BasicNameValuePair("specificationid", specificationid));
        pairs.add(new BasicNameValuePair("colorid", colorid));
        pairs.add(new BasicNameValuePair("number", number));
        String result = sendRequest("TBEAENG003001010000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * /**
     * <p>
     * 购买单个商品接口：
     * 购买单个商品的时候，先请求下面的接口获取到orderdetailid
     * distributorid 当总经销商进入的时候需要传经销商的ID,即附近经销商的商品进入经销商详细的时候，其它时候传空
     * commodityid  商品ID
     * specificationid  所选规格ID
     * colorid 所选颜色ID
     * number   数目
     */
    public RspInfo1 getOrderDetailId(String distributorid, String commodityid, String specificationid, String colorid, String number) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("distributorid", distributorid));
        pairs.add(new BasicNameValuePair("commodityid", commodityid));
        pairs.add(new BasicNameValuePair("specificationid", specificationid));
        pairs.add(new BasicNameValuePair("colorid", colorid));
        pairs.add(new BasicNameValuePair("number", number));
        String result = sendRequest("TBEAENG003001011000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取购物车列表
     */
    public RspInfo getShopCarList(int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001012000", pairs);
        rspInfo = gson.fromJson(result, RspInfo.class);
        return rspInfo;
    }

    /**
     * 删除购物车里面的商品
     *
     * @param ids 删除的商品ID集合，用逗号隔开如:1111,2222,3333
     */
    public RspInfo1 delectShopCar(String ids) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("orderdetailids", ids));
        String result = sendRequest("TBEAENG003001013000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 下单
     * <p>
     * orderdetailidlist  （选择的产品的son，包括了产品的id和数目(orderdetailid,ordernumber)）
     * receiveaddrid   (收货的地址id)
     * paytypeid   (支付类型id)
     * deliverytypeid
     * (发货类型id)
     * ordernote    （留言）
     * actualneedpaymoney  （实际支付金额）
     */
    public RspInfo1 placeOnOrder(String orderdetailidlist, String receiveaddrid, String paytypeid, String deliverytypeid, String ordernote, String actualneedpaymoney) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("orderdetailidlist", orderdetailidlist));
        pairs.add(new BasicNameValuePair("receiveaddrid", receiveaddrid));
        pairs.add(new BasicNameValuePair("paytypeid", paytypeid));
        pairs.add(new BasicNameValuePair("deliverytypeid", deliverytypeid));
        pairs.add(new BasicNameValuePair("ordernote", ordernote));
        pairs.add(new BasicNameValuePair("actualneedpaymoney", actualneedpaymoney));
        String result = sendRequest("TBEAENG003001016000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取下单信息
     */
    public RspInfo getOrderInfo() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG003001015000", pairs);
        rspInfo = gson.fromJson(result, RspInfo.class);
        return rspInfo;
    }

    /**
     * 获取收货地址详细信息
     */
    public RspInfo getAddrInfo(String receiveaddrid) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("receiveaddrid", receiveaddrid));
        String result = sendRequest("TBEAENG005001003002", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<Address>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取订单状态
     */
    public RspInfo getOrderState() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001018000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Condition>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取我的订单
     */
    public RspInfo getOrderList(String orderstatusid, int page, int pageSize) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("orderstatusid", orderstatusid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG005001017000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<Object>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 获取我的订单
     */
    public RspInfo getOrder(String userorderid) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("userorderid", userorderid));
        String result = sendRequest("TBEAENG005001019000", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<Object>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 收藏商品
     */
    public RspInfo1 collectCommodity(String commodityid, String distributorid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("commodityid", commodityid));
        pairs.add(new BasicNameValuePair("distributorid", distributorid));
        String result = sendRequest("TBEAENG005001011000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 评价列表
     */
    public RspInfo1 getEvaluateList(String commodityid, int page, int pageSize) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("commodityid", commodityid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG003001009004", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 更改头像
     */
    public RspInfo1 updateHead(String filePath) throws Exception {
        RspInfo1 rspInfo;
        Map<String, String> paramsIn = new HashMap<>();
        Map<String, String> fileIn = new HashMap<>();
        fileIn.put("picture1", filePath);
        String result = regist("TBEAENG005001002002", paramsIn, fileIn);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 注册时获取上级经销商列表
     */
    public RspInfo getDistributorList(String provinceId, String cityId, String locationId) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("provinceid", provinceId));
        pairs.add(new BasicNameValuePair("cityid", cityId));
        pairs.add(new BasicNameValuePair("zoneid", locationId));
        String result = sendRequest("TBEAENG001001002001", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Condition>>>() {
        }.getType());
        return rspInfo;
    }

    public RspInfo1 register(String mobile, String password, String verifycode, String provinceid, String cityid, String zoneid, String distributorid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mobile", mobile));
        pairs.add(new BasicNameValuePair("password", MD5Util.getMD5String(password)));
        pairs.add(new BasicNameValuePair("verifycode", verifycode));
        pairs.add(new BasicNameValuePair("provinceid", provinceid));
        pairs.add(new BasicNameValuePair("cityid", cityid));
        pairs.add(new BasicNameValuePair("zoneid", zoneid));
        pairs.add(new BasicNameValuePair("distributorid", distributorid));
        String result = sendRequest("TBEAENG001001002000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 注册获取电话号码
     */
    public RspInfo1 getPhone() throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG001001002002", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 保存评价
     *
     * @param commodityid 商品ID
     * @param starlevel   等级
     * @param appraise    评语
     * @return
     * @throws Exception
     */
    public RspInfo1 saveEvaluate(String commodityid, String starlevel, String appraise) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("commodityid", commodityid));
        pairs.add(new BasicNameValuePair("starlevel", starlevel));
        pairs.add(new BasicNameValuePair("appraise", appraise));
        String result = sendRequest("TBEAENG005001099000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 认证
     */
    public RspInfo1 accountAuthentication(Register register) throws Exception {
        RspInfo1 rspInfo;
        Map<String, String> paramsIn = new HashMap<>();
        Map<String, String> fileIn = new HashMap<>();
        paramsIn.put("realname", register.getRealname());
        paramsIn.put("personcardid", register.getPersonid());
        fileIn.put("personidcard1", register.getPersonidcard1());
        fileIn.put("personidcard2", register.getPersonidcard2());
        fileIn.put("personidcardwithperson", register.getPersonidcardwithperson());
        String result = regist("TBEAENG005001002003", paramsIn, fileIn);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 购物车数量
     */
    public RspInfo getShopCarNumber(String commodityid) throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("commodityid", commodityid));
        String result = sendRequest("TBEAENG003001012001", pairs);
        rspInfo = gson.fromJson(result, RspInfo.class);
        return rspInfo;
    }

    /**
     * 消息数量
     */
    public RspInfo getMessageNumber() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG002001001001", pairs);
        rspInfo = gson.fromJson(result, RspInfo.class);
        return rspInfo;
    }

    /**
     * 提醒发货
     * userorderid 订单Id
     */
    public RspInfo1 remindSendOutCommdith(String userorderid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("userorderid ", userorderid));
        String result = sendRequest("TBEAENG005001098000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取下单信息
     */
    public RspInfo getAccountAuthentication() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001002004", pairs);
        rspInfo = gson.fromJson(result, RspInfo.class);
        return rspInfo;
    }

    /**
     * 提现获取经销商列表
     */
    public RspInfo getDistributoList() throws Exception {
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG005001009001", pairs);
        rspInfo = gson.fromJson(result, new TypeToken<RspInfo<List<Distributor>>>() {
        }.getType());
        return rspInfo;
    }

    /**
     * 删除订单信息
     */
    public RspInfo1 delectOrder(String id) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("userorderid", id));
        String result = sendRequest("TBEAENG005001020000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取分享信息接口
     */
    public RspInfo1 getShareInfo(String objecttype, String objectid) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("objecttype", objecttype));
        pairs.add(new BasicNameValuePair("objectid", objectid));
        String result = sendRequest("TBEAENG002002001000", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取消息类型列表
     */
    public MessageTypeListResponseModel getMessageTypeList(int page, int pageSize) throws Exception {
        MessageTypeListResponseModel rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG005001013000", pairs);
        rspInfo = gson.fromJson(result, MessageTypeListResponseModel.class);
        return rspInfo;
    }

    /**
     * 获取消息列表
     */
    public MessageListResponseModel getMessageList(String categoryid, int page, int pageSize) throws Exception {
        MessageListResponseModel rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("categoryid", categoryid));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        String result = sendRequest("TBEAENG005001013001", pairs);
        rspInfo = gson.fromJson(result, MessageListResponseModel.class);
        return rspInfo;
    }

}
