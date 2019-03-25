package com.feiqu.web.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.feiqu.common.enums.TopicTypeEnum;
import com.feiqu.common.enums.UserStatusEnum;
import com.feiqu.common.enums.YesNoEnum;
import com.feiqu.framwork.constant.CommonConstant;
import com.feiqu.framwork.util.WebUtil;
import com.feiqu.framwork.web.base.BaseController;
import com.feiqu.system.model.*;
import com.feiqu.system.pojo.cache.FqUserCache;
import com.feiqu.system.pojo.response.ArticleUserDetail;
import com.feiqu.system.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cwd
 * @create 2017-09-10:51
 **/
@Controller
public class IndexController extends BaseController {

//    private static Logger logger = LoggerFactory.getLogger(IndexController.class);
    private static Log logger = LogFactory.get();

    @Autowired
    private ThoughtService thoughtService;

    @Autowired
    private ArticleService articleService;

//    @Autowired
//    private SuperBeautyService superBeautyService;
    @Autowired
    private FqLabelService fqLabelService;
    @Autowired
    private FqUserService fqUserService;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private UserActionNewService userActionNewService;

    @GetMapping(value = {"index",""})
    public String index(Model model, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") Integer pageIndex,
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam(required = false) String order){
        try{
           /* FqUserCache fqUser = webUtil.currentUser(request,response);
            if(fqUser != null){
                return "redirect:/u/"+fqUser.getId()+"/home";
            }*/

            /*PageHelper.startPage(1, CommonConstant.DEAULT_PAGE_SIZE,false);
            ThoughtExample example = new ThoughtExample();
            example.setOrderByClause("comment_count desc ");
            example.createCriteria().andDelFlagEqualTo(YesNoEnum.NO.getValue());
            List<ThoughtWithUser> thoughts = thoughtService.getThoughtWithUser(example);*/
//            PageInfo page = new PageInfo(thoughts);
            model.addAttribute("newThoughtList",CommonConstant.NEW_THOUGHT_LIST );
            model.addAttribute("thoughtList",CommonConstant.HOT_THOUGHT_LIST );
//            model.addAttribute("noticeList", CommonConstant.FQ_NOTICE_LIST);
            model.addAttribute("articleList", CommonConstant.HOT_ARTICLE_LIST);
//            model.addAttribute("beautyList", CommonConstant.HOT_BEAUTY_LIST );
            model.addAttribute("newUserList", CommonConstant.NEW_SIMPLE_USERS );
//            model.addAttribute("friendLinkList", CommonConstant.FRIEND_LINK_LIST );
            model.addAttribute("userCount", CommonConstant.FQ_USER_TOTAL_COUNT);
//            model.addAttribute("beautySims", CommonConstant.BEAUTY_BANNERS);
            model.addAttribute("activeUserList",CommonConstant.FQ_ACTIVE_USER_LIST);
        }catch (Exception e){
           logger.error("主页 获取数据出错",e);
           return "/error.html";
        }
        return "/index.html";
    }

    @RequestMapping(value = "/superGeek")
    public String superGeekIndex(Model model, Integer labelId, String order, String keyword ,HttpServletRequest request, HttpServletResponse response){
       return superGeek(model,1,order,labelId,keyword,request,response);
    }

    @RequestMapping(value = "/superGeek/{pageIndex}")
    public String superGeek(Model model, @PathVariable(required = false) Integer pageIndex,
                            String order, Integer labelId, String keyword, HttpServletRequest request, HttpServletResponse response){
        PageHelper.startPage(pageIndex, 20);
        FqUserCache fqUser = webUtil.currentUser(request, response); //当前用户
        ArticleExample example = new ArticleExample();
        if("zan".equals(order)){
            example.setOrderByClause("like_count desc ");
        }else if("comment".equals(order)){
            example.setOrderByClause("comment_count desc ");
        }else {
            example.setOrderByClause("create_time desc");
        }
        if(labelId == null){
            example.createCriteria().andDelFlagEqualTo(YesNoEnum.NO.getValue());
        }else {
            example.createCriteria().andDelFlagEqualTo(YesNoEnum.NO.getValue()).andLabelEqualTo(labelId);
            model.addAttribute("labelId",labelId);
        }
        if(StringUtils.isNotBlank(keyword)){
            example.getOredCriteria().get(0).andArticleTitleLike("%"+keyword+"%");
        }
        List<ArticleUserDetail> articles = articleService.selectUserByExampleWithBLOBs(example);//所有笔记

        List<UserActionNew> list1=userActionNewService.getActionByUserId(fqUser.getId());
        List<Integer> labels1= null;
        List<Article> recommendArticles = new ArrayList<Article>();//推荐笔记,user_action不为空-->前两个标签的五篇笔记，为空-->管理员推荐笔记
        if(list1!=null && list1.size()!=0){
            Integer[] labels= new Integer[list1.size()];
            for (int i = 0; i < list1.size(); i++){
                labels[i] =list1.get(i).getArticleLabel();
            }
            labels1=java.util.Arrays.asList(labels);
            recommendArticles= articleService.getArticleByLabels(labels1);
        }else{
            example.clear();
            example.setOrderByClause("browse_count desc");
            example.createCriteria().andIsRecommendEqualTo(YesNoEnum.YES.getValue());
            recommendArticles = articleService.selectByExample(example);
        }

        PageInfo page = new PageInfo(articles);
        model.addAttribute("recommendArticles",recommendArticles);
        model.addAttribute("articles",articles);
        model.addAttribute("pageIndex",pageIndex);
        model.addAttribute("pageSize",20);//笔记放多点好，感觉，要不然老是需要翻页
        model.addAttribute("count",page.getTotal());
        model.addAttribute("order",order);
        model.addAttribute("articleList", CommonConstant.HOT_ARTICLE_LIST);
        FqLabelExample labelExample = new FqLabelExample();
        labelExample.createCriteria().andDelFlagEqualTo(YesNoEnum.NO.getValue()).andTypeEqualTo(TopicTypeEnum.ARTICLE_TYPE.getValue());
        List<FqLabel> labels = fqLabelService.selectByExample(labelExample);
        Map<Integer,String> map = Maps.newHashMap();
        for(FqLabel label : labels){
            map.put(label.getId(),label.getName());
        }
        model.addAttribute("labels",map);
        return "/superGeek.html";
    }

    @GetMapping("about")
    public String about(){
        return "/about.html";
    }

    @GetMapping(value = "/captcha")
    @ResponseBody
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        try {
            LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200,50);
            lineCaptcha.createCode();
            request.getSession().setAttribute("code",lineCaptcha.getCode());
            ImageIO.write(lineCaptcha.getImage(), "JPEG", response.getOutputStream());
        } catch (IOException e) {
            logger.error("获取验证码出错",e);
        }
    }

    @GetMapping("jump")
    public String about(String username,Model model){
        FqUserExample example = new FqUserExample();
        example.createCriteria().andNicknameEqualTo(username);
        FqUser fqUser = fqUserService.selectFirstByExample(example);
        if(fqUser == null){
            return "redirect:/";
        }
        if(UserStatusEnum.DEL.getValue().equals(fqUser.getStatus())){
            model.addAttribute(CommonConstant.GENERAL_CUSTOM_ERROR_CODE,"用户已经被删除");
            return GENERAL_CUSTOM_ERROR_URL;
        }else if(UserStatusEnum.FROZEN.getValue().equals(fqUser.getStatus())){
            model.addAttribute(CommonConstant.GENERAL_CUSTOM_ERROR_CODE,"用户已经被禁用");
            return GENERAL_CUSTOM_ERROR_URL;
        }else if(UserStatusEnum.NORMAL.getValue().equals(fqUser.getStatus())){
            return "redirect:/u/"+fqUser.getId()+"/peopleIndex";
        }
        return "redirect:/u/"+fqUser.getId()+"/peopleIndex";
    }


}
