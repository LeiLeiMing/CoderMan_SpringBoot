
package com.zhangyu.coderman.controller;

import com.zhangyu.coderman.dto.AccessTokenDTO;
import com.zhangyu.coderman.dto.BaiduAccessTokenDTO;
import com.zhangyu.coderman.dto.BaiduUser;
import com.zhangyu.coderman.dto.GithubUser;
import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.provider.BaiduProvider;
import com.zhangyu.coderman.provider.GithubProvider;
import com.zhangyu.coderman.service.UserService;
import com.zhangyu.coderman.utils.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Controller
public class AuthorizeController {

    protected static final Logger logger = LoggerFactory.getLogger(AuthorizeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private BaiduProvider baiduProvider;

    @Value("${chatId}")
    private String chatId;

    @Value("${chatKey}")
    private String chatKey;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.client.redirecturi}")
    private String RedirectUri;

    /**
     * 百度
     * @param code
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/baiducallback")
    private String BaiduCallBack(@RequestParam(name = "code") String code, HttpServletRequest request, HttpServletResponse response) {
        BaiduAccessTokenDTO baiduAccessTokenDTO = new BaiduAccessTokenDTO();
        baiduAccessTokenDTO.setClient_id("51RRNd7IvCciSw5hmkhVmD2c");
        baiduAccessTokenDTO.setClient_secret("odZgaURLEv1R0YVphxHMZrcBd9zkEni6");
        baiduAccessTokenDTO.setRedirect_uri("http://localhost:8080/callback2");
        baiduAccessTokenDTO.setCode(code);
        baiduAccessTokenDTO.setGrant_type("authorization_code");
        String accessToken = baiduProvider.getAccessToken(baiduAccessTokenDTO);
        BaiduUser user = baiduProvider.getUser(accessToken);
        //百度用户用户
        return null;
    }
    /**
     * github
     * @param code
     * @param state
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code, @RequestParam(name = "state") String state,
                           HttpServletRequest request, HttpServletResponse response, Model model) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_uri(RedirectUri);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if (githubUser != null) {
            //登入成功
            User user = new User();
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setName(githubUser.getName());
            user.setCompany(githubUser.getCompany());
            user.setBio(githubUser.getBio());
            user.setLocation(githubUser.getLocation());
            user.setCompany(githubUser.getCompany());
            user.setAvatarUrl(githubUser.getAvatar_url());
            //token
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            userService.SaveOrUpdate(user);
            Cookie c = new Cookie("token", token);
            c.setMaxAge(3600*2);//设置cookie的时长为2小时(登入的token)
            response.addCookie(c);
            //登入成功
            //logger.info("用户登入成功:{}",githubUser);
            //注册聊天
            long time = System.currentTimeMillis();
            HttpSession session = request.getSession();
            session.setAttribute("xlm_wid",chatId);
            session.setAttribute("xlm_uid",githubUser.getId());
            session.setAttribute("xlm_name",githubUser.getName());
            session.setAttribute("xlm_avatar",githubUser.getAvatar_url());
            session.setAttribute("xlm_time",time);
            String string= 14876+"_"+githubUser.getId()+"_"+time+"_"+chatKey;
            string = AuthorizeController.encryptPasswordWithSHA512(string).toLowerCase();
            session.setAttribute("xlm_hash",string);
            //聊天cookie
            Cookie[] cookies=request.getCookies();
            Cookie chatCookie = CookieUtils.findCookieByName(cookies, "JSESSIONID");
            chatCookie.setMaxAge(3600*2);
            session.setMaxInactiveInterval(3600*2);
            response.addCookie(chatCookie);
            return "redirect:/";
        } else {
            //登入失败
            logger.error("用户登入失败:{}",githubUser);
            return "redirect:/";
        }
    }

    public static String encryptPasswordWithSHA512(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");  //创建SHA512类型的加密对象
            messageDigest.update(password.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuffer strHexString = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xff & bytes[i]);
                if (hex.length() == 1) {
                    strHexString.append('0');
                }
                strHexString.append(hex);
            }
            String result = strHexString.toString();
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 退出登入
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.invalidate();
        //清除cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                }
            }
        }
        response.addCookie(new Cookie("token", null));
        return "redirect:/";
    }


}

