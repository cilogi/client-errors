// Copyright (c) 2010 Tim Niblett All Rights Reserved.
//
// File:        RouteModule.java  (05-Oct-2010)
// Author:      tim
// $Id$
//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.cilogi.jserror.guice;

import com.cilogi.jserror.servlet.IndexServlet;
import com.cilogi.jserror.servlet.MustacheServlet;
import com.cilogi.jserror.servlet.PeriodicEmailServlet;
import com.cilogi.jserror.servlet.error.ErrorServlet;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.common.collect.ImmutableMap;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.thetransactioncompany.cors.CORSFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RouteModule extends ServletModule {
    @SuppressWarnings({"unused"})
    static final Logger LOG = LoggerFactory.getLogger(RouteModule.class.getName());

    public RouteModule() {

    }

    @Override
    protected void configureServlets() {
        filter("/*").through(ObjectifyFilter.class);
        filter("/*").through(AppstatsFilter.class);
        filter("/*").through(CORSFilter.class, ImmutableMap.<String,String>of(
                "cors.maxAge", Integer.toString(-1),
                "cors.allowOrigin", "*"
        ));
        serve("/appstats/*").with(AppstatsServlet.class);
        serve("/jserror").with(ErrorServlet.class);
        serve("/index.html").with(IndexServlet.class);
        serve("/sample.html").with(MustacheServlet.class);

        serve("/admin/email").with(PeriodicEmailServlet.class);

    }

    @SuppressWarnings({"unused"})
    private static boolean isDevelopmentServer() {
        SystemProperty.Environment.Value server = SystemProperty.environment.value();
        return server == SystemProperty.Environment.Value.Development;
    }
}
