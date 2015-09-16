// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        IndexServlet.java  (16/09/15)
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

import com.cilogi.jserror.db.error.ErrorRecord;
import com.cilogi.jserror.db.error.ErrorRecordDAO;
import com.cilogi.jserror.db.user.GaeUser;
import com.cilogi.jserror.util.NamedCache;
import com.cilogi.util.Pickle;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Singleton
public class IndexServlet extends BaseServlet {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(IndexServlet.class);

    private static final int CACHE_SECONDS = 30;

    private static final int DEFAULT_SECONDS = 1800;
    private static final long serialVersionUID = 8872456472819312137L;

    private final NamedCache memcache;
    private final LoadingCache<ERKey,List<ErrorRecord>> cache;

    @Inject
    public IndexServlet() {
        memcache = new NamedCache(getClass().getName());
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(100)
                .maximumSize(10)
                .expireAfterWrite(CACHE_SECONDS, TimeUnit.SECONDS)
                .build(new CacheLoader<ERKey,List<ErrorRecord>>() {
                    @SuppressWarnings({"unchecked"})
                    public List<ErrorRecord> load(ERKey key)  throws IOException, ClassNotFoundException {
                        ArrayList<ErrorRecord> list = null;
                        byte[] cached = memcache.get(key.key());
                        if (cached == null) {
                            list = loadErrors(key.getToken(), key.getNSeconds());
                            cached = Pickle.pickle(list);
                            memcache.put(key.key(), cached, Expiration.byDeltaSeconds(CACHE_SECONDS));
                        }
                        return (list == null) ? Pickle.unpickle(cached, ArrayList.class) : list;
                    }
                });

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InputStream is = getClass().getResourceAsStream("/templates/index.mustache");
        GaeUser user = getCurrentUser();
        if (user == null) {
            issue(MediaType.PLAIN_TEXT_UTF_8, HttpServletResponse.SC_FORBIDDEN, "forbidden", response);
        } else {
            int nSeconds = intParameter("seconds", request, DEFAULT_SECONDS);
            String token = user.getToken();
            Map<String,Object> map = new HashMap<>();
            map.put("token", token);
            map.put("userName", user.getName());
            map.put("logoutUrl", logoutUrl());
            map.put("time", nSeconds);
            try {
                //map.put("records", cache.get(new ERKey(token, nSeconds)));
                map.put("records", loadErrors(token, nSeconds));
            } catch (Exception e) {
                map.put("records", new ArrayList<>());
            }

            showView(response, "index.mustache", map);
        }
    }

    private ArrayList<ErrorRecord> loadErrors(String token, int nSeconds) {
        List<ErrorRecord> errs = new ErrorRecordDAO().latest(token, nSeconds);
        return Lists.newArrayList(errs);  // copy over to make serializable
    }

    private String logoutUrl() {
        UserService service = UserServiceFactory.getUserService();
        return service.createLogoutURL("/index.html");
    }

    @Data
    private static class ERKey implements Serializable {
        private static final long serialVersionUID = -8103977860883682129L;
        private String token;
        private int nSeconds;
        private ERKey() {}
        ERKey(String token, int nSeconds) {
            this.token = token;
            this.nSeconds = nSeconds;
        }
        public String key() {
            return token + ":" + nSeconds;
        }
    }

}
