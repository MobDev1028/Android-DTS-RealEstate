
package com.dts.dts.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_search_id")
    @Expose
    private Integer userSearchId;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("msg_id")
    @Expose
    private Object msgId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("details")
    @Expose
    private List<Detail> details = new ArrayList<Detail>();

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The userSearchId
     */
    public Integer getUserSearchId() {
        return userSearchId;
    }

    /**
     * 
     * @param userSearchId
     *     The user_search_id
     */
    public void setUserSearchId(Integer userSearchId) {
        this.userSearchId = userSearchId;
    }

    /**
     * 
     * @return
     *     The key
     */
    public String getKey() {
        return key;
    }

    /**
     * 
     * @param key
     *     The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 
     * @return
     *     The msgId
     */
    public Object getMsgId() {
        return msgId;
    }

    /**
     * 
     * @param msgId
     *     The msg_id
     */
    public void setMsgId(Object msgId) {
        this.msgId = msgId;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 
     * @param updatedAt
     *     The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 
     * @return
     *     The details
     */
    public List<Detail> getDetails() {
        return details;
    }

    /**
     * 
     * @param details
     *     The details
     */
    public void setDetails(List<Detail> details) {
        this.details = details;
    }

}
