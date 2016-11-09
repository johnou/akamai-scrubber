// Copyright (c) 2005-2009 Akamai Technologies, Inc.
// The information herein is proprietary and confidential to Akamai, and
// it may only be used under appropriate agreements with the Company.
// Access to this information does not imply or grant you any right to
// use the information, all such rights being expressly reserved.

package com.akamai.purge.api;

public class PurgeResult {

    private final int estimatedSeconds;
    private final String progressUri;
    private final String purgeId;
    private final int httpStatus;
    private final String detail;
    private final int pingAfterSeconds;

    public PurgeResult(int estimatedSeconds, String progressUri, String purgeId, int httpStatus, String detail, int pingAfterSeconds) {
        this.estimatedSeconds = estimatedSeconds;
        this.progressUri = progressUri;
        this.purgeId = purgeId;
        this.httpStatus = httpStatus;
        this.detail = detail;
        this.pingAfterSeconds = pingAfterSeconds;
    }

    /**
     * The set of possible results of submitting a purge.  To get more
     * details, see the Akamai CCUAPI docuemtation.
     */
    public enum ResultType {
        SUCCESS,
        INVALID_USER_CREDENTIAL,
        QUEUE_LIMIT_REACHED, // does this exist with REST?
        ;
    }

    /**
     * Get the result of the purge submission as an object which can
     * be used to drive further behavior in the system.
     */
    public ResultType getResult() throws PurgeException
    {
        int resultCode = getHttpStatus();

        if (resultCode == 201) {
            return ResultType.SUCCESS;
        } else if (resultCode == 332) {
            return ResultType.QUEUE_LIMIT_REACHED;
        } else if (resultCode == 401) {
            return ResultType.INVALID_USER_CREDENTIAL;
        }

        throw new PurgeException("Unknown result code:  " + resultCode +
                "\nPlease contact Akamai with details");
    }

    public int getEstimatedSeconds() {
        return estimatedSeconds;
    }

    public String getProgressUri() {
        return progressUri;
    }

    public String getPurgeId() {
        return purgeId;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getDetail() {
        return detail;
    }

    public int getPingAfterSeconds() {
        return pingAfterSeconds;
    }
}
