
package com.dts.dts.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserSearch {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("search_data")
    @Expose
    private String searchData;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("disabled")
    @Expose
    private Integer disabled;
    @SerializedName("price_range")
    @Expose
    private PriceRange priceRange;
    @SerializedName("availability_date_range")
    @Expose
    private AvailabilityDateRange availabilityDateRange;
    @SerializedName("user_info")
    @Expose
    private UserInfo userInfo;
    @SerializedName("search_agent_info")
    @Expose
    private SearchAgentInfo searchAgentInfo;
    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<Result>();

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
     *     The searchData
     */
    public String getSearchData() {
        return searchData;
    }

    /**
     * 
     * @param searchData
     *     The search_data
     */
    public void setSearchData(String searchData) {
        this.searchData = searchData;
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
     *     The priceRange
     */
    public PriceRange getPriceRange() {
        return priceRange;
    }

    /**
     * 
     * @param priceRange
     *     The price_range
     */
    public void setPriceRange(PriceRange priceRange) {
        this.priceRange = priceRange;
    }

    /**
     * 
     * @return
     *     The availabilityDateRange
     */
    public AvailabilityDateRange getAvailabilityDateRange() {
        return availabilityDateRange;
    }

    /**
     * 
     * @param availabilityDateRange
     *     The availability_date_range
     */
    public void setAvailabilityDateRange(AvailabilityDateRange availabilityDateRange) {
        this.availabilityDateRange = availabilityDateRange;
    }

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
     *     The searchAgentInfo
     */
    public SearchAgentInfo getSearchAgentInfo() {
        return searchAgentInfo;
    }

    /**
     * 
     * @param searchAgentInfo
     *     The search_agent_info
     */
    public void setSearchAgentInfo(SearchAgentInfo searchAgentInfo) {
        this.searchAgentInfo = searchAgentInfo;
    }

    /**
     * 
     * @return
     *     The results
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }

}
