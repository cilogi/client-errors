// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        ErrorServlet.java  (16/09/15)
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


package com.cilogi.jserror.servlet.error;

import com.cilogi.jserror.db.error.ErrorRecord;
import com.cilogi.jserror.db.error.ErrorRecordDAO;
import com.cilogi.jserror.servlet.BaseServlet;
import com.cilogi.jserror.util.NamedCache;
import com.cilogi.util.IOUtil;
import com.google.appengine.api.memcache.Expiration;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Singleton
public class ErrorServlet extends BaseServlet {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(ErrorServlet.class);

    private static final int SAVE_INTERVAL = 60 * 1;

    private NamedCache cache;

    @Inject
    public ErrorServlet() {
        cache = new NamedCache(getClass().getName());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            byte[] jsonData = IOUtil.copyStream(request.getInputStream());
            if (jsonData == null || jsonData.length == 0) {
                issueJson(response, HttpServletResponse.SC_BAD_REQUEST, "message", "No data in request");
            } else {
                String jsonString = new String(jsonData, Charsets.UTF_8);
                try {
                    ErrorRecord record = ErrorRecord.parse(jsonString).hash();
                    record.setDate(new Date().getTime());
                    String token = record.getToken();
                    if (token == null) {
                        issueJson(response, HttpServletResponse.SC_BAD_REQUEST, "message", "No token in " + jsonString);
                    } else {
                        String hash = token + record.getMd5Hash();
                        if (!cache.contains(hash)) {
                            LOG.info("Caching value for " + record);
                            cache.put(hash, hash, Expiration.byDeltaSeconds(SAVE_INTERVAL));
                            new ErrorRecordDAO().put(record);
                        } else {
                            LOG.info("Cache hit  for " + record);
                        }
                        issueJson(response, HttpServletResponse.SC_OK, "message", "ok");
                    }
                } catch (Exception e) {
                    issueJson(response, HttpServletResponse.SC_BAD_REQUEST, "message", "Can't parse from " + jsonString + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.warn("internal error: " + e.getMessage());
            issueJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "message", "internal error: " + e.getMessage());
        }
    }

}
