
package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchAgentInfo {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("user_search_id")
    @Expose
    private Integer userSearchId;
    @SerializedName("last_execution")
    @Expose
    private String lastExecution;
    @SerializedName("lock_date")
    @Expose
    private Object lockDate;
    @SerializedName("lock_code")
    @Expose
    private Object lockCode;
    @SerializedName("next_execution")
    @Expose
    private String nextExecution;
    @SerializedName("disabled")
    @Expose
    private Integer disabled;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("deleted")
    @Expose
    private Integer deleted;

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
     *     The userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     *     The user_id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
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
     *     The lastExecution
     */
    public String getLastExecution() {
        return lastExecution;
    }

    /**
     * 
     * @param lastExecution
     *     The last_execution
     */
    public void setLastExecution(String lastExecution) {
        this.lastExecution = lastExecution;
    }

    /**
     * 
     * @return
     *     The lockDate
     */
    public Object getLockDate() {
        return lockDate;
    }

    /**
     * 
     * @param lockDate
     *     The lock_date
     */
    public void setLockDate(Object lockDate) {
        this.lockDate = lockDate;
    }

    /**
     * 
     * @return
     *     The lockCode
     */
    public Object getLockCode() {
        return lockCode;
    }

    /**
     * 
     * @param lockCode
     *     The lock_code
     */
    public void setLockCode(Object lockCode) {
        this.lockCode = lockCode;
    }

    /**
     * 
     * @return
     *     The nextExecution
     */
    public String getNextExecution() {
        return nextExecution;
    }

    /**
     * 
     * @param nextExecution
     *     The next_execution
     */
    public void setNextExecution(String nextExecution) {
        this.nextExecution = nextExecution;
    }

    /**
     * 
     * @return
     *     The disabled
     */
    public Integer getDisabled() {
        return disabled;
    }

    /**
     * 
     * @param disabled
     *     The disabled
     */
    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
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
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The deleted
     */
    public Integer getDeleted() {
        return deleted;
    }

    /**
     * 
     * @param deleted
     *     The deleted
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

}
