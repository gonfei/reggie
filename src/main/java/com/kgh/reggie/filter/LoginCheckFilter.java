package com.kgh.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.kgh.reggie.commons.BaseContext;
import com.kgh.reggie.commons.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取本次请求的uri
        String requestURI = request.getRequestURI();

        //定义不需要处理的请求路径
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/commons/**",
                "/user/sendMsg",
                "/user/login",
        };

        //2.判断本次请求师傅需要处理
        boolean check = check(urls, requestURI);

        //不需要处理直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }

        //判断登录状态，已登录则放行
        if (request.getSession().getAttribute("employee") != null) {
            //获取用户id
            Long empId = (Long) request.getSession().getAttribute("employee");
            //将id传给线程
            BaseContext.setid(empId);

            filterChain.doFilter(request, response);
            return;
        }

        //判断前端登录状态
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));

            Long userId= (Long) request.getSession().getAttribute("user");

            BaseContext.setid(userId);

            filterChain.doFilter(request, response);
            return;
        }
        //未登录返回未登录结果,通过输出流方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    //路径匹配
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
