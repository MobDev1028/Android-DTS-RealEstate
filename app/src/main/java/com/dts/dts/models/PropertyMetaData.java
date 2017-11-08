
package com.dts.dts.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertyMetaData {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("per_page")
    @Expose
    private Integer perPage;
    @SerializedName("current_page")
    @Expose
    private String currentPage;
    @SerializedName("last_page")
    @Expose
    private Integer lastPage;
    @SerializedName("next_page_url")
    @Expose
    private Object nextPageUrl;
    @SerializedName("prev_page_url")
    @Expose
    private Object prevPageUrl;
    @SerializedName("from")
    @Expose
    private Integer from;
    @SerializedName("to")
    @Expose
    private Integer to;
    @SerializedName("property")
    @Expose
    private List<Property> property = new ArrayList<Property>();

    /**
     * 
     * @return
     *     The total
     */
    public Integer getTotal() {
        return total;
    }

    public void addProperty(Property p)
    {
        property.add(p);
    }
    /**
     * 
     * @param total
     *     The total
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 
     * @return
     *     The perPage
     */
    public Integer getPerPage() {
        return perPage;
    }

    /**
     * 
     * @param perPage
     *     The per_page
     */
    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    /**
     * 
     * @return
     *     The currentPage
     */
    public String getCurrentPage() {
        return currentPage;
    }

    /**
     * 
     * @param currentPage
     *     The current_page
     */
    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * 
     * @return
     *     The lastPage
     */
    public Integer getLastPage() {
        return lastPage;
    }

    /**
     * 
     * @param lastPage
     *     The last_page
     */
    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    /**
     * 
     * @return
     *     The nextPageUrl
     */
    public Object getNextPageUrl() {
        return nextPageUrl;
    }

    /**
     * 
     * @param nextPageUrl
     *     The next_page_url
     */
    public void setNextPageUrl(Object nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    /**
     * 
     * @return
     *     The prevPageUrl
     */
    public Object getPrevPageUrl() {
        return prevPageUrl;
    }

    /**
     * 
     * @param prevPageUrl
     *     The prev_page_url
     */
    public void setPrevPageUrl(Object prevPageUrl) {
        this.prevPageUrl = prevPageUrl;
    }

    /**
     * 
     * @return
     *     The from
     */
    public Integer getFrom() {
        return from;
    }

    /**
     * 
     * @param from
     *     The from
     */
    public void setFrom(Integer from) {
        this.from = from;
    }

    /**
     * 
     * @return
     *     The to
     */
    public Integer getTo() {
        return to;
    }

    /**
     * 
     * @param to
     *     The to
     */
    public void setTo(Integer to) {
        this.to = to;
    }

    /**
     * 
     * @return
     *     The property
     */
    public List<Property> getProperty() {
        return property;
    }

    /**
     * 
     * @param property
     *     The property
     */
    public void setProperty(List<Property> property) {
        this.property = property;
    }

}
