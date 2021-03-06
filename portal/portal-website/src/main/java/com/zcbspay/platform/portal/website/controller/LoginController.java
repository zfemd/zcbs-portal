package com.zcbspay.platform.portal.website.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zcbspay.platform.portal.system.bean.UserBean;
import com.zcbspay.platform.portal.system.service.UserService;
import com.zcbspay.platform.portal.website.constant.Constants;
import com.zcbspay.platform.portal.website.util.CookieUtils;
import com.zcbspay.platform.portal.website.util.MD5Util;

import net.sf.json.util.JSONUtils;

@Controller
@RequestMapping("/login")
@SuppressWarnings("all")
public class LoginController {

    @Autowired
	private UserService userService;
    
    
    
    /**
	 * 验证用户登录信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/test")
	public void test(UserBean userBean,HttpServletRequest request,String randcode) {
		userBean.setCreateTime("19891212");
		userBean.setCreator("1");
		userBean.setEmail("bema@126.com");
		userBean.setMemberid("121212");
		userBean.setPwd("123");
		userBean.setUserName("bema");
		userBean.setLoginName("bema");
		userBean.setUserId("2");
		
		//System.out.println(JSONUtils.valueToString(userService.saveUser(userBean)));
		
		//System.out.println(JSONUtils.valueToString(userService.updateUser(userBean)));
		//System.out.println(JSONUtils.valueToString(userService.queryUsers(userBean, "1", "10")));
		
		System.out.println(JSONUtils.valueToString(userService.login(userBean)));
	}

	/**
	 * 验证用户登录信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/login")
	public Map<String, Object> validateUser(UserBean user,HttpServletRequest request,String randcode,HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		boolean loginFlag = false;
		Map<String, Object> returnmap = userService.login(user);
		if (returnmap.get("code").equals("00")) {
			Cookie cookie=new Cookie(Constants.LoginCanstant.LOGIN_USER_NAME, user.getLoginName());
			cookie.setMaxAge(30 * 60);// 设置为30min  
	        cookie.setPath("/");  
	        response.addCookie(cookie);  
			request.getSession().setAttribute(Constants.LoginCanstant.LOGIN_USER, user);
		}
		return returnmap;
	}

	/**
	 * 用户登出
	 * @return
	 */
    @ResponseBody
	@RequestMapping("/logout")
	public ModelAndView logout(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView result=new ModelAndView("/login");
		HttpSession session = request.getSession(true);

        if (isNull(session.getAttribute(Constants.LoginCanstant.LOGIN_USER))) {
        	session.invalidate();
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.LoginCanstant.LOGIN_USER_NAME)) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        return result;
    }
    
    
    /**
	 * 用户登出
	 * @return
	 */
	@RequestMapping("/showForgetPwd")
	public String showBill() {
		return "user/reset_password";
	}

	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("http_client_ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 如果是多级代理，那么取第一个ip为客户ip
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		return ip;
	}
	private boolean isNull(Object value) {
		if (value == null || value.toString().equals("")) {
			return true;
		} else {
			return false;
		}

	}
	
}
