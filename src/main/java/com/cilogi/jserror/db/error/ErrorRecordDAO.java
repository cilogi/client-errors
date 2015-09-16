// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        ErrorRecordDAO.java  (16/09/15)
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

import com.cilogi.jserror.db.BaseDAO;
import com.googlecode.objectify.ObjectifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ErrorRecordDAO extends BaseDAO<ErrorRecord> {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(ErrorRecordDAO.class);

    static {
        ObjectifyService.register(ErrorRecord.class);
    }

    public ErrorRecordDAO() {
        super(ErrorRecord.class);
    }

    public List<ErrorRecord> latest(String token, int seconds) {
        long now = new Date().getTime();
        long start = now - (seconds * 1000L);
        return ofy().load().type(ErrorRecord.class).limit(100).filter("token", token).filter("date >", start).list();
    }
}
