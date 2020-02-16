package com.example.router;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.Arrays;

@Component
public class PreFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getRequest().getHeader("Authorization") == null) {
            for (Cookie cookie : ctx.getRequest().getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        for (Cookie cookie : ctx.getRequest().getCookies()) {
            if ("token".equals(cookie.getName())) {
                String authorizationHeader = "Bearer " + cookie.getValue();
                ctx.addZuulRequestHeader("Authorization", authorizationHeader);
                break;
            }
        }
        return null;
    }
}
