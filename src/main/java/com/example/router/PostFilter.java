package com.example.router;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class PostFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    private String response;

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        InputStream stream = ctx.getResponseDataStream();

        try {
            response = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
            ctx.setResponseBody(response);
            return response.contains("access_token");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseBody(response);
        JsonElement root = new JsonParser().parse(response);
        String accessToken = root.getAsJsonObject().get("access_token").getAsString();

        Cookie cookie = new Cookie("token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/r");
        ctx.getResponse().addCookie(cookie);

        return null;
    }
}
