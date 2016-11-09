// Copyright (c) 2005-2009 Akamai Technologies, Inc.
// The information herein is proprietary and confidential to Akamai, and
// it may only be used under appropriate agreements with the Company.
// Access to this information does not imply or grant you any right to
// use the information, all such rights being expressly reserved.

package com.akamai.purge.api;

public class PurgeException extends Exception {
    public PurgeException(String message) {
        super(message);
    }
        
    public PurgeException(String message, Exception e) {
        super(message, e);
    }
}
