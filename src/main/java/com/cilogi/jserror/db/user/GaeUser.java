// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        GaeUser.java  (26-Oct-2011)
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Cache
@Entity
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GaeUser implements Serializable {
    static final Logger LOG = Logger.getLogger(GaeUser.class.getName());

    private static final int MAX_NUMBER_OF_GUIDES = 3;
    private static final long serialVersionUID = 4315625153176608584L;

    @Id @Getter
    private String name;

    @Getter
    private Set<String> roles;

    @Getter
    private Set<String> permissions;

    @Index
    @Setter @Getter
    private Date dateRegistered;

    @Setter
    @Getter
    private boolean suspended;

    @Setter
    @Getter
    private Date acceptedTerms;

    @Getter
    @Setter
    private String token;

    /** For objectify to create instances on retrieval */
    @SuppressWarnings({"unused"})
    private GaeUser() {
        this.roles = new HashSet<>();
        this.permissions = new HashSet<>();
    }

    public GaeUser(String name) {
        this(name, new HashSet<String>(), new HashSet<String>());
    }

    public GaeUser(String name, Set<String> roles, Set<String> permissions) {
        this(name,roles, permissions, false);
    }

    GaeUser(@NonNull String name, @NonNull Set<String> roles, @NonNull Set<String> permissions, boolean isRegistered) {
        this.name = name;
        this.roles = Sets.newHashSet(roles);
        this.permissions = Sets.newHashSet(permissions);
        this.dateRegistered = isRegistered ? new Date() : null;
        this.suspended = false;
        this.token = UUID.randomUUID().toString();
    }


    @JsonIgnore
    public boolean isRegistered() {
        return getDateRegistered() != null;
    }

    @JsonIgnore
    public boolean isAdmin() {
        return getRoles().contains("admin");
    }

    public String toJSONString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOG.severe("Can't convert GaeUser " + this + " to JSON string");
            return "";
        }
    }

    @SuppressWarnings({"unused"})
    public JSONObject toJSONObject() {
        try {
            return new JSONObject(toJSONString());
        } catch (Exception e) {
            LOG.severe("Can't convert GaeUser " + this + " to JSONObject");
            return null;
        }
    }
}
