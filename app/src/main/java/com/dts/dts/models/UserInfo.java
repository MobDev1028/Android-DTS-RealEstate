
package com.dts.dts.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("cid")
    @Expose
    private String cid;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("last_login_ts")
    @Expose
    private String lastLoginTs;
    @SerializedName("roles")
    @Expose
    private List<Object> roles = new ArrayList<Object>();
    @SerializedName("contact_preferences")
    @Expose
    private List<Object> contactPreferences = new ArrayList<Object>();

    /**
     * 
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return
     *     The cid
     */
    public String getCid() {
        return cid;
    }

    /**
     * 
     * @param cid
     *     The cid
     */
    public void setCid(String cid) {
        this.cid = cid;
    }

    /**
     * 
     * @return
     *     The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 
     * @param userName
     *     The user_name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 
     * @return
     *     The lastLoginTs
     */
    public String getLastLoginTs() {
        return lastLoginTs;
    }

    /**
     * 
     * @param lastLoginTs
     *     The last_login_ts
     */
    public void setLastLoginTs(String lastLoginTs) {
        this.lastLoginTs = lastLoginTs;
    }

    /**
     * 
     * @return
     *     The roles
     */
    public List<Object> getRoles() {
        return roles;
    }

    /**
     * 
     * @param roles
     *     The roles
     */
    public void setRoles(List<Object> roles) {
        this.roles = roles;
    }

    /**
     * 
     * @return
     *     The contactPreferences
     */
    public List<Object> getContactPreferences() {
        return contactPreferences;
    }

    /**
     * 
     * @param contactPreferences
     *     The contact_preferences
     */
    public void setContactPreferences(List<Object> contactPreferences) {
        this.contactPreferences = contactPreferences;
    }

}
