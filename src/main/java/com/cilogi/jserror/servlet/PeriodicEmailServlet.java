// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        PeriodicEmail.java  (16/09/15)
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
import com.cilogi.jserror.db.user.GaeUserDAO;
import com.cilogi.jserror.util.SendEmail;
import com.cilogi.util.mustache.MustacheRender;
import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class PeriodicEmailServlet extends BaseServlet {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(PeriodicEmailServlet.class);

    private static final int DEFAULT_SECONDS = 1800;
    private static final String ADMIN_NAME = "tim@timniblett.net";

    private final SendEmail sendEmail;
    private final String emailTo;
    private final MustacheRender render;
    @Inject
    public PeriodicEmailServlet(SendEmail sendEmail, @Named("email.to") String emailTo) {
        this.sendEmail = sendEmail;
        this.emailTo = emailTo;
        render = new MustacheRender();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GaeUser admin = new GaeUserDAO().get("tim@timniblett.net");
        if (admin != null) {
            String token = admin.getToken();
            List<ErrorRecord> records = new ErrorRecordDAO().latest(token, DEFAULT_SECONDS);
            LOG.info("Email handler, " + records.size() + " new errors");
            if (records.size() > 0) {
                Map<String,Object> map = new HashMap<>();
                map.put("token", token);
                map.put("userName", ADMIN_NAME);
                map.put("time", DEFAULT_SECONDS);
                map.put("records", records);
                String html =  render.render("email", map);
                sendEmail.send(emailTo, "New JS Errors", html);
                issue(MediaType.PLAIN_TEXT_UTF_8, HttpServletResponse.SC_OK, records.size() + " records", response);
            } else {
                issue(MediaType.PLAIN_TEXT_UTF_8, HttpServletResponse.SC_OK, "nothing", response);
            }
        }
    }

}
