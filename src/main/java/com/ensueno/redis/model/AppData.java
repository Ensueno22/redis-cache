package com.ensueno.redis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class AppData {

    private String regDate;

    private String uptDate;

    private String grpId;

    private String id;

    private String appName;

    private String privateFlag;

    private String appGrpName;

    private String appGrpKey;

    private String registerId;

    // 공통
    private String appKey;

    // GCM 필수
    private String gcmApiKey1;

    private String gcmApiKey3;

    private String gcmApiKey2;

    // XMPP connection ID (option)
    private String gcmProductId;


    // iOS 발송 필수
    private String pushCert;

    private String enc2Pa;

    //new P8 Certificate [only APNS value exist] : team_id/key_id/app_bundle_id
    private String teamId;

    private String keyId;

    private String appBundleId;
}
