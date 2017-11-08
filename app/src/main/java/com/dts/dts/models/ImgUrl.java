
package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImgUrl {

    @SerializedName("raw")
    @Expose
    private String raw;
    @SerializedName("xs")
    @Expose
    private String xs;
    @SerializedName("sm")
    @Expose
    private String sm;
    @SerializedName("md")
    @Expose
    private String md;
    @SerializedName("lg")
    @Expose
    private String lg;

    /**
     * 
     * @return
     *     The raw
     */
    public String getRaw() {
        return raw;
    }

    /**
     * 
     * @param raw
     *     The raw
     */
    public void setRaw(String raw) {
        this.raw = raw;
    }

    /**
     * 
     * @return
     *     The xs
     */
    public String getXs() {
        return xs;
    }

    /**
     * 
     * @param xs
     *     The xs
     */
    public void setXs(String xs) {
        this.xs = xs;
    }

    /**
     * 
     * @return
     *     The sm
     */
    public String getSm() {
        return sm;
    }

    /**
     * 
     * @param sm
     *     The sm
     */
    public void setSm(String sm) {
        this.sm = sm;
    }

    /**
     * 
     * @return
     *     The md
     */
    public String getMd() {
        return md;
    }

    /**
     * 
     * @param md
     *     The md
     */
    public void setMd(String md) {
        this.md = md;
    }

    /**
     * 
     * @return
     *     The lg
     */
    public String getLg() {
        return lg;
    }

    /**
     * 
     * @param lg
     *     The lg
     */
    public void setLg(String lg) {
        this.lg = lg;
    }

}
