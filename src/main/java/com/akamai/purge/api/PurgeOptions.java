// Copyright (c) 2005-2009 Akamai Technologies, Inc.
// The information herein is proprietary and confidential to Akamai, and
// it may only be used under appropriate agreements with the Company.
// Access to this information does not imply or grant you any right to
// use the information, all such rights being expressly reserved.

package com.akamai.purge.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// N.B.  DO NOT CHANGE THE OPTION PROCESSING LOGIC IN THIS CLASS!!!
//
// The mapping of option fields to message strings is NOT obvious from
// the WSDL, where it's all subsumed in a single element of the
// purgeRequest message of type "ArrayOfString".  Similarly, the
// "foo=" strings in the option settings are not documented as part of
// the service API in any reasonable way.
//
// This is obviously a terrible design, and we can only plead age as
// an excuse: the service interface was put together in the "stone
// age" of web services before the various standards had solidified,
// and once an interface is out there and in serious use it's pretty
// much impossible to change it without breaking all users.
//
// We hope to have a new version of the service (with a new WSDL
// representing a better interface) in the future which will make the
// options specification (among other things) cleaner.  Until then,
// however, we recommend that you use this class directly in your own
// client to avoid problems with the handling of purging options.
//

/**
 * This class represents the optional fields to be sent to the CCUAPI service.
 * <p>
 * N.B.  The options represented here are OPTIONAL!!!  No harm will
 * come to you if you leave them empty, and some harm might come to
 * you if you fill them in with junk!!  For instance, we once saw a
 * case where a user at FOOBAR company (names changed to protect the
 * guilty) supplied an email address like "bogus@foobar.com" which was
 * (of course) bogus.  This resulted in the CCU system sending emails
 * to this bogus account, having those emails rejected by the FOOBAR
 * company SPAM filter, and ultimately resulted in blacklisting
 * causing the entire FOOBAR company to cease receiving email
 * notifications from the CCU system altogether.  So if you don't have
 * something good to put in the option slot, don't put in anything at
 * all!
 */ 
public class PurgeOptions {
    private PurgeType type;
    private Action action;
    private Domain domain;

    /**
     * Default options:
     *  - invalidation 
     *  - individual URLs or ARLs 
     *  - production machines
     *  - no email notification
     */
    public PurgeOptions() {
        this.type = PurgeType.DEFAULT;
        this.action = Action.DEFAULT;
        this.domain = Domain.DEFAULT;
    }

    /**
     * @param type The type of purge (cpcode or arl/url -- note that these can't be mixed in one request)
     * @param action What to do with purged content (invalidate or remove)
     * @param domain Where to do the purge (production or staging or both?)
     */
    public PurgeOptions(PurgeType type, Action action, Domain domain) {
        this.type = type;
        this.action = action;
        this.domain = domain;
    }      
 
    /**
     * There are two types of purge "actions" which are available
     * through the CCUAPI.  Both actions ensure that the content will
     * not be served up without being updated, but they differ in how
     * aggressive the server will be about removing the old marterial
     * from disk.
     */
    public enum Action {
        /**
         * The DEFAULT action is to INVALIDATE, since this generally
         * results in less load on the origin.
         */
    	DEFAULT (""),
        /**
         * The REMOVE action is very aggressive and guarantees that
         * the old object will be deleted from the disk as soon as the
         * purge request is processed by the edge server.  Among other
         * things, this means that the server will have to do a full
         * HTTP GET to the origin to get a new object if it's
         * requested again.
         */
    	REMOVE ("remove"),
        /**
         * The INVALIDATE action leaves the object on disk.  If a
         * request for it is made, that results in an HTTP IMS GET
         * request to the origin (much lighter load) to see if there's
         * been a change, and a GET if there has been.
         */
    	INVALIDATE ("invalidate");
    	
    	private final String name;
    	private String actionName() { return name; }
    	private Action(String name) { this.name = name; }
    }
    
    /**
     * There are two types of "domains" which can be purged, the "edge
     * staging" network and the "production" network.
     */
    public enum Domain {
        /**
         * The DEFAULT domain is PRODUCTION.
         */
    	DEFAULT (""),
    	PRODUCTION ("production"),
    	STAGING ("staging");
    	
    	private final String name;
    	private String domainName() { return name; }
    	private Domain(String name) { this.name = name; }
    }

    /**
     * Objects to be purged come in two flavors: URI and CPCODE.
     * These types of objects cannot be mixed in the same request.
     */
    public enum PurgeType {
        /**
         * The DEFAULT type is URI.
         */
    	DEFAULT (""),
    	CPCODE ("cpcode"),
        /**
         * Appropriate for URLs and ARLs.
         */
    	URI ("arl");

    	private final String name;
    	private String typeName() { return name; }
    	private PurgeType(String name) { this.name = name; }
    }

    public PurgeType getType() {
        return type;
    }
}
