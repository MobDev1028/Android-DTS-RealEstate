
package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Img {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("property_id")
    @Expose
    private Integer propertyId;
    @SerializedName("media_filename")
    @Expose
    private String mediaFilename;
    @SerializedName("display_order")
    @Expose
    private Integer displayOrder;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("completed")
    @Expose
    private Integer completed;
    @SerializedName("deleted")
    @Expose
    private Integer deleted;
    @SerializedName("img_url")
    @Expose
    private ImgUrl imgUrl;

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
     *     The mediaFilename
     */
    public String getMediaFilename() {
        return mediaFilename;
    }

    /**
     * 
     * @param mediaFilename
     *     The media_filename
     */
    public void setMediaFilename(String mediaFilename) {
        this.mediaFilename = mediaFilename;
    }

    /**
     * 
     * @return
     *     The displayOrder
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * 
     * @param displayOrder
     *     The display_order
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The completed
     */
    public Integer getCompleted() {
        return completed;
    }

    /**
     * 
     * @param completed
     *     The completed
     */
    public void setCompleted(Integer completed) {
        this.completed = completed;
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

    /**
     * 
     * @return
     *     The imgUrl
     */
    public ImgUrl getImgUrl() {
        return imgUrl;
    }

    /**
     * 
     * @param imgUrl
     *     The img_url
     */
    public void setImgUrl(ImgUrl imgUrl) {
        this.imgUrl = imgUrl;
    }

}
