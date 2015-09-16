// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        GaeUserDAO.java  (01-Nov-2011)
// Author:      tim

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


package com.cilogi.jserror.db.user;

import com.cilogi.jserror.db.BaseDAO;
import com.cilogi.util.Secrets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;

import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class GaeUserDAO extends BaseDAO<GaeUser> {
    static final Logger LOG = Logger.getLogger(GaeUserDAO.class.getName());

    private static final Set<String> admins = Sets.newHashSet(
            "tim@timniblett.net",
            "tim.niblett@cilogi.com"
    );

    private static final String TOKEN = Secrets.get("token");

    static {
        ObjectifyService.register(GaeUser.class);
    }

    private static void adminUsers() {

    }

    public GaeUserDAO() {
        super(GaeUser.class);
    }

    /**
     * Save user with authorization information
     * @param user  User
     * @param changeCount should the user count be incremented
     * @return the user, after changes
     */
    public GaeUser saveUser(final GaeUser user, final boolean changeCount) {
        return ofy().transact(new Work<GaeUser>() {
            public GaeUser run() {
                put(user);
                return user;
            }
        });
    }

    public GaeUser deleteUser(final GaeUser user) {
        return ofy().transact(new Work<GaeUser>() {
             public GaeUser run() {
                delete(user.getName());
                return user;
             }
        });
    }


    public GaeUser createRegisteredUser(final String userName) {
        return ofy().transact(new Work<GaeUser>() {
            public GaeUser run() {
                GaeUser user = get(userName);
                if (user == null) {
                    Set<String> roles = Sets.newHashSet();
                    if (admins.contains(userName)) {
                        roles.add("admin");
                    }
                    user = new GaeUser(userName, roles, Sets.<String>newHashSet(), true);
                    if ("tim@timniblett.net".equals(userName)) {
                        user.setToken(TOKEN);
                    }
                    put(user);
                } else {
                    if (!user.isRegistered()) {
                        user.setDateRegistered(new Date());
                        put(user);
                    }
                }
                return user;
            }
        });
    }

    public GaeUser updateAdminFromGoogle(GaeUser gaeUser, boolean isAdmin) {
        if (gaeUser == null) {
            return null;
        }
        boolean current = gaeUser.getRoles().contains("admin");
        if (current != isAdmin) {
            if (current) {
                gaeUser.getRoles().add("admin");
            } else {
                gaeUser.getRoles().remove("admin");
            }
            saveUser(gaeUser, false);
        }
        return gaeUser;
    }
}
