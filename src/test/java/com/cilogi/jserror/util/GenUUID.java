// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        GenUUID.java  (16/09/15)
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


package com.cilogi.jserror.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class GenUUID {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(GenUUID.class);

    public GenUUID() {

    }

    public static void main(String[] args) {
        try {
            System.out.println(UUID.randomUUID().toString());
        } catch (Exception e) {
            LOG.error("oops: " + e.getMessage());
        }
    }
}
