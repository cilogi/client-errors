// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        ErrorRecord.java  (16/09/15)
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


package com.cilogi.jserror.db.error;

import com.cilogi.util.Digest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@Entity
@Cache
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ErrorRecord implements Serializable {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(ErrorRecord.class);
    private static final long serialVersionUID = -7350495612278799278L;

    @Id
    private Long id;
    private String md5Hash;
    private String details;
    private String userAgent;
    @Index
    private String token;
    @Index
    private long date;


    public static ErrorRecord parse(String s) throws IOException {
        return new ObjectMapper().readValue(s, ErrorRecord.class);
    }

    private ErrorRecord() {

    }

    public ErrorRecord(String userAgent, String details) {
        this.userAgent = userAgent;
        this.details = details;
        this.date = new Date().getTime();
        hash();
    }

    public final ErrorRecord hash() {
        this.md5Hash = (details == null) ? null : Digest.digest64(details, Digest.Algorithm.MD5);
        return this;
    }

    public String toJSONString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
