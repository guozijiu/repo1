package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    AuthService authService;
    @Override
    public String filterType() {

        return "pre";
    }

    //过滤器序号，越小越优先执行
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //返回true表示执行过滤器
        return true;
    }

    //过滤器内容
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext=RequestContext.getCurrentContext();
        //得到request
        HttpServletRequest request = requestContext.getRequest();
        //得到response
        HttpServletResponse response = requestContext.getResponse();
        //取出cookie中的身份令牌
        String tokenFromCookie = authService.getTokenFromCookie(request);
        if (StringUtils.isEmpty(tokenFromCookie)){
            //拒绝访问
            access_denied();
        }
        //从header中取jwt
        String jwtFromHeader = authService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(jwtFromHeader)){
            //拒绝访问
            access_denied();
        }
        //从redis取出jwt过期时间
        long expire = authService.getExpire(tokenFromCookie);
        if (expire<0){
            access_denied();
        }
        return null;
    }
    //拒绝访问
    private void access_denied(){
        RequestContext requestContext=RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        //拒绝访问
            requestContext.setSendZuulResponse(false);
            //设置响应代码
            requestContext.setResponseStatusCode(200);
           //设置响应信息
            ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
            //转成json
           String jsonString = JSON.toJSONString(responseResult);
            requestContext.setResponseBody(jsonString);
            //转成json,设置contentType
            response.setContentType("application/json;charset=utf-8");
    }

}
