
package com.zhangyu.coderman.controller;

import com.zhangyu.coderman.dto.AccessTokenDTO;
import com.zhangyu.coderman.dto.GithubUser;
import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.provider.GithubProvider;
import com.zhangyu.coderman.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private UserService userService;

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.client.redirecturi}")
    private String RedirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code, @RequestParam(name = "state") String state,
                           HttpServletRequest request, HttpServletResponse response) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_uri(RedirectUri);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if(githubUser!=null){
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
            //c.setMaxAge(3600*24*3);//设置cookie的时长为3天
            response.addCookie(c);
            return "redirect:/";
        }else {
            return "redirect:/";
        }
    }

    /**
     * 退出登入
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response){
        HttpSession session = request.getSession();
        session.invalidate();
        //清除cookie
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if("token".equals(cookie.getName())){
                    cookie.setMaxAge(0);
                }
            }
        }
        response.addCookie(new Cookie("token",null));

        return "redirect:/";
    }


}

