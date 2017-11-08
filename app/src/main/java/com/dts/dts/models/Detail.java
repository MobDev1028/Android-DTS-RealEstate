
package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Detail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("search_result_id")
    @Expose
    private Integer searchResultId;
    @SerializedName("property_id")
    @Expose
    private Integer propertyId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("user_search_id")
    @Expose
    private Integer userSearchId;
    @SerializedName("matching")
    @Expose
    private Integer matching;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("inquired")
    @Expose
    private Object inquired;
    @SerializedName("shown")
    @Expose
    private Object shown;
    @SerializedName("custom_msg_sent")
    @Expose
    private Object customMsgSent;
    @SerializedName("disqualified")
    @Expose
    private Object disqualified;
    @SerializedName("propertyFields")
    @Expose
    private Property propertyFields;

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
     *     The searchResultId
     */
    public Integer getSearchResultId() {
        return searchResultId;
    }

    /**
     * 
     * @param searchResultId
     *     The search_result_id
     */
    public void setSearchResultId(Integer searchResultId) {
        this.searchResultId = searchResultId;
    }

    /**
     * 
     * @return
     *     The propertyId
     */
    public Integer getPropertyId() {
        return propertyId;
    }

    /**
     * 
     * @param propertyId
     *     The property_id
     */
    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
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
     *     The matching
     */
    public Integer getMatching() {
        return matching;
    }

    /**
     * 
     * @param matching
     *     The matching
     */
    public void setMatching(Integer matching) {
        this.matching = matching;
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
     *     The inquired
     */
    public Object getInquired() {
        return inquired;
    }

    /**
     * 
     * @param inquired
     *     The inquired
     */
    public void setInquired(Object inquired) {
        this.inquired = inquired;
    }

    /**
     * 
     * @return
     *     The shown
     */
    public Object getShown() {
        return shown;
    }

    /**
     * 
     * @param shown
     *     The shown
     */
    public void setShown(Object shown) {
        this.shown = shown;
    }

    /**
     * 
     * @return
     *     The customMsgSent
     */
    public Object getCustomMsgSent() {
        return customMsgSent;
    }

    /**
     * 
     * @param customMsgSent
     *     The custom_msg_sent
     */
    public void setCustomMsgSent(Object customMsgSent) {
        this.customMsgSent = customMsgSent;
    }

    /**
     * 
     * @return
     *     The disqualified
     */
    public Object getDisqualified() {
        return disqualified;
    }

    /**
     * 
     * @param disqualified
     *     The disqualified
     */
    public void setDisqualified(Object disqualified) {
        this.disqualified = disqualified;
    }

    /**
     * 
     * @return
     *     The propertyFields
     */
    public Property getPropertyFields() {
        return propertyFields;
    }

    /**
     * 
     * @param propertyFields
     *     The propertyFields
     */
    public void setPropertyFields(Property propertyFields) {
        this.propertyFields = propertyFields;
    }

}
