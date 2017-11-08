
package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertiesSummary {

    @SerializedName("APT")
    @Expose
    private Integer aPT;
    @SerializedName("CONDO")
    @Expose
    private Integer cONDO;
    @SerializedName("HOUSE")
    @Expose
    private Integer hOUSE;
    @SerializedName("ROOM")
    @Expose
    private Integer rOOM;
    @SerializedName("COMM")
    @Expose
    private Integer cOMM;

    /**
     * 
     * @return
     *     The aPT
     */
    public Integer getAPT() {
        return aPT;
    }

    /**
     * 
     * @param aPT
     *     The APT
     */
    public void setAPT(Integer aPT) {
        this.aPT = aPT;
    }

    /**
     * 
     * @return
     *     The cONDO
     */
    public Integer getCONDO() {
        return cONDO;
    }

    /**
     * 
     * @param cONDO
     *     The CONDO
     */
    public void setCONDO(Integer cONDO) {
        this.cONDO = cONDO;
    }

    /**
     * 
     * @return
     *     The hOUSE
     */
    public Integer getHOUSE() {
        return hOUSE;
    }

    /**
     * 
     * @param hOUSE
     *     The HOUSE
     */
    public void setHOUSE(Integer hOUSE) {
        this.hOUSE = hOUSE;
    }

    /**
     * 
     * @return
     *     The rOOM
     */
    public Integer getROOM() {
        return rOOM;
    }

    /**
     * 
     * @param rOOM
     *     The ROOM
     */
    public void setROOM(Integer rOOM) {
        this.rOOM = rOOM;
    }

    /**
     * 
     * @return
     *     The cOMM
     */
    public Integer getCOMM() {
        return cOMM;
    }

    /**
     * 
     * @param cOMM
     *     The COMM
     */
    public void setCOMM(Integer cOMM) {
        this.cOMM = cOMM;
    }

}
