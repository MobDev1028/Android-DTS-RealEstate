
package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Metadata {

    @SerializedName("user_info")
    @Expose
    private UserInfo userInfo;
    @SerializedName("properties_summary")
    @Expose
    private PropertiesSummary propertiesSummary;

    /**
     * 
     * @return
     *     The userInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 
     * @param userInfo
     *     The user_info
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 
     * @return
     *     The propertiesSummary
     */
    public PropertiesSummary getPropertiesSummary() {
        return propertiesSummary;
    }

    /**
     * 
     * @param propertiesSummary
     *     The properties_summary
     */
    public void setPropertiesSummary(PropertiesSummary propertiesSummary) {
        this.propertiesSummary = propertiesSummary;
    }

}
