package com.lechuang.dalaba.model.bean;

/**
 * @author: LGH
 * @since: 2018/3/30
 * @describe:
 */

public class WXAccessTokenBean {


    /**
     * access_token : hSTsG7nq8e0yEFhOZFT-wAdTsjT9jC0AYvGRiqGwwR6Hko99o_mmYR8KO18kMxeDOz33d9tnBhMzu_NLsIha2HqvTm1OGPL1weBdvXZVFFc
     * expires_in : 7200
     * refresh_token : AeN69M27vttqCedxoIOSeY6cxvbt1N584HjEOclUXtNWxRaZWgmtfvn2jWIDX4tq5t-7Btlc1UkEyyFhV7HVIMXe-V6RPjoZdF525vLzev8
     * openid : olmt4wfxS21G4VeeVX16_zUhZezY
     * scope : snsapi_userinfo
     * unionid : o5aWQwAa7niCIXhAIRBOwglIJ7UQ
     */

    public String access_token;
    public int expires_in;
    public String refresh_token;
    public String openid;
    public String scope;
    public String unionid;

    public int errcode = 0;
    public String errmsg;
}
