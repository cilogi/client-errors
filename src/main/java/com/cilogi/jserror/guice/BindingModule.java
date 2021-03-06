// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        BindingModule.java  (12-Oct-2011)
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


import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.googlecode.objectify.ObjectifyFilter;
import com.thetransactioncompany.cors.CORSFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class BindingModule extends AbstractModule {
    @SuppressWarnings({"unused"})
    static final Logger LOG = LoggerFactory.getLogger(BindingModule.class);


    public BindingModule() {
    }

    @Override
    protected void configure() {
        bind(ObjectifyFilter.class).in(Scopes.SINGLETON);

        bindString("email.from", "info@client-errors.appspotmail.com");
        bindString("email.to", "tim.niblett@cilogi.com");
        bind(CORSFilter.class).in(Scopes.SINGLETON);
        bind(AppstatsFilter.class).in(Scopes.SINGLETON);
        bind(AppstatsServlet.class).in(Scopes.SINGLETON);
    }

    private void bindString(String key, String value) {
        bind(String.class).annotatedWith(Names.named(key)).toInstance(value);
    }

    @SuppressWarnings({"unused"})
    private void bindBoolean(String key, boolean value) {
        bind(Boolean.class).annotatedWith(Names.named(key)).toInstance(value);
    }

    private static boolean isDevelopmentServer() {
        SystemProperty.Environment.Value server = SystemProperty.environment.value();
        return server == SystemProperty.Environment.Value.Development;
    }

    private static String defaultBucketName() {
        AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
        return appIdentity.getDefaultGcsBucketName();
    }
}
