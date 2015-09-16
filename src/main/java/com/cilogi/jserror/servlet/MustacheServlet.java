// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        MustacheServlet.java  (14/09/15)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Cilogi (the Author) and may not be used, sold, licenced, 
// transferred, copied or reproduced in whole or in part in 
// any manner or form or in or on any media to any person other than 
// in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.cilogi.jserror.servlet;

import com.cilogi.jserror.util.NamedCache;
import com.cilogi.util.mustache.MustacheRender;
import com.google.appengine.api.memcache.Expiration;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.net.MediaType;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Singleton
public class MustacheServlet extends BaseServlet {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(MustacheServlet.class);

    private static final int CACHE_SECONDS = 30 * 60;

    private static final String MUSTACHE_EXTENSION = ".mustache";

    private final LoadingCache<String,String> cache;
    private final NamedCache memcache;
    private final MustacheRender render;

    @Inject
    public MustacheServlet() {
        render = new MustacheRender();
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(100)
                .maximumSize(10)
                .expireAfterWrite(CACHE_SECONDS, TimeUnit.SECONDS)
                .build(new CacheLoader<String,String>() {
                    public String load(String template) throws JSONException {
                        return loadRender(template);
                    }
                });
        memcache = new NamedCache(getClass().getName());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        String template = changeExtension(uri);
        try {
            String html = cache.get(template);
            cache(response, CACHE_SECONDS);
            issue(MediaType.HTML_UTF_8, HttpServletResponse.SC_OK, html, response);
        } catch (Exception e) {
            issue(MediaType.PLAIN_TEXT_UTF_8, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error: " + e.getMessage(), response);
        }
    }

    private String load(String template) throws ExecutionException {
        return cache.get(template);
    }

    private String loadRender(String template) {
        String cached = memcache.getString(template);
        if (cached == null) {
            cached = render.render(template, new Object());
            memcache.put(template, cached, Expiration.byDeltaSeconds(CACHE_SECONDS));
        }
        return cached;
    }

    static final String changeExtension(String in) {
        int index = in.lastIndexOf('.');
        if (index == -1) {
            return in + MUSTACHE_EXTENSION;
        } else {
            return in.substring(0, index) + MUSTACHE_EXTENSION;
        }
    }

}
