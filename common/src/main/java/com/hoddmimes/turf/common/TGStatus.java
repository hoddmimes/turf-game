package com.hoddmimes.turf.common;

import com.hoddmimes.turf.common.generated.TG_Response;

public class TGStatus {
    public static TG_Response create(boolean pSuccess, String pReason, String pLoadPage) {
        TG_Response tStatus = new TG_Response();
        tStatus.setOptionPage(pLoadPage);
        tStatus.setReason(pReason);
        tStatus.setSuccess(pSuccess);
        return tStatus;
    }

    public static TG_Response createSuccessResponse() {
        TG_Response tStatus = new TG_Response();
        tStatus.setOptionPage(null);
        tStatus.setReason("Successfully completed");
        tStatus.setSuccess(true);
        return tStatus;
    }

    public static TG_Response create(boolean pSuccess, String pReason) {
        TG_Response tStatus = new TG_Response();
        tStatus.setOptionPage(null);
        tStatus.setReason(pReason);
        tStatus.setSuccess(pSuccess);
        return tStatus;
    }

    public static TG_Response createError(String pReason, Throwable pException) {
        TG_Response tStatus = new TG_Response();
        String tReason = pReason + ((pException == null) ? "" : ("  Exec: " + pException.getMessage()));

        tStatus.setOptionPage(null);
        tStatus.setReason(tReason);
        tStatus.setSuccess(false);
        return tStatus;
    }

}
