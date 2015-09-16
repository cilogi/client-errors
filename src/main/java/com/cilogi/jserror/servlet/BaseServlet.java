// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        BaseServlet.java  (14/09/15)
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

import com.cilogi.jserror.db.user.GaeUser;
import com.cilogi.jserror.db.user.GaeUserDAO;
import com.cilogi.util.mustache.MustacheRender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.net.MediaType;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;


public class BaseServlet extends HttpServlet {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(BaseServlet.class);

    protected BaseServlet() {
    }

    protected void issue(MediaType mimeType, int returnCode, byte[] output, HttpServletResponse response) throws IOException {
        response.setContentType(mimeType.toString());
        response.setStatus(returnCode);
        response.getOutputStream().write(output);
    }

    protected void issue(MediaType mimeType, int returnCode, String output, HttpServletResponse response) throws IOException {
        issue(mimeType, returnCode, output.getBytes(Charsets.UTF_8), response);
    }

    protected void issueJson(HttpServletResponse response, int returnCode, Object... args) throws IOException {
        issue(MediaType.JSON_UTF_8, returnCode, new ObjectMapper().writeValueAsString(args), response);
    }

    protected void showView(HttpServletResponse response, String templateName, Object model) throws IOException {
        MustacheRender render = new MustacheRender();
        String html = render.render(templateName, model);
        issue(MediaType.HTML_UTF_8, HttpServletResponse.SC_OK, html.getBytes(Charsets.UTF_8), response);
    }

    protected void cache(HttpServletResponse response, int seconds) {
        Preconditions.checkArgument(seconds > 0);
        response.setHeader("Pragma", "Public");
        response.setHeader("Cache-Control", "public, no-transform, max-age="+seconds);

    }

    protected boolean booleanParameter(@NonNull String name, HttpServletRequest request, boolean deflt) {
        String s = request.getParameter(name);
        return (s == null) ? deflt : Boolean.parseBoolean(s);
    }

    protected int intParameter(@NonNull String name, HttpServletRequest request, int deflt) {
        String s = request.getParameter(name);
        try {
            return (s == null) ? deflt : Integer.parseInt(s);
        } catch (Exception e) {
            LOG.info("Incorrect int argument: " + s);
            return deflt;
        }
    }

    @SuppressWarnings({"unchecked"})
    protected GaeUser getCurrentUser() {
        User user = UserServiceFactory.getUserService().getCurrentUser();
        if (user == null) {
            return null;
        } else {
            GaeUser gaeUser = new GaeUserDAO().createRegisteredUser(user.getEmail());
            if (gaeUser != null && (gaeUser.isSuspended() || !gaeUser.isRegistered())) {
                return null;
            }
            return gaeUser;
        }
    }
}
