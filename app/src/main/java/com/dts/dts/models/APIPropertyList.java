
package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIPropertyList {

    @SerializedName("property_meta_data")
    @Expose
    private PropertyMetaData propertyMetaData;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("metadata")
    @Expose
    private Metadata metadata;

    /**
     * 
     * @return
     *     The propertyMetaData
     */
    public PropertyMetaData getPropertyMetaData() {
        return propertyMetaData;
    }

    /**
     * 
     * @param propertyMetaData
     *     The property_meta_data
     */
    public void setPropertyMetaData(PropertyMetaData propertyMetaData) {
        this.propertyMetaData = propertyMetaData;
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
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

}
