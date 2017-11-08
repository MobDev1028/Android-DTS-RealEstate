
package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiUserSearch {

    @SerializedName("search_meta_data")
    @Expose
    private SearchMetaData searchMetaData;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("metadata")
    @Expose
    private Object metadata;

    /**
     * 
     * @return
     *     The searchMetaData
     */
    public SearchMetaData getSearchMetaData() {
        return searchMetaData;
    }

    /**
     * 
     * @param searchMetaData
     *     The search_meta_data
     */
    public void setSearchMetaData(SearchMetaData searchMetaData) {
        this.searchMetaData = searchMetaData;
    }

    /**
     * 
     * @return
     *     The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * 
     * @param success
     *     The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * 
     * @return
     *     The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     *     The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 
     * @return
     *     The metadata
     */
    public Object getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

}
