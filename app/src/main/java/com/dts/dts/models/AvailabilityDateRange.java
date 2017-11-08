
package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvailabilityDateRange {

    @SerializedName("start")
    @Expose
    private Object start;
    @SerializedName("start_formatted")
    @Expose
    private Object startFormatted;
    @SerializedName("end")
    @Expose
    private Object end;
    @SerializedName("end_formatted")
    @Expose
    private Object endFormatted;

    /**
     * 
     * @return
     *     The start
     */
    public Object getStart() {
        return start;
    }

    /**
     * 
     * @param start
     *     The start
     */
    public void setStart(Object start) {
        this.start = start;
    }

    /**
     * 
     * @return
     *     The startFormatted
     */
    public Object getStartFormatted() {
        return startFormatted;
    }

    /**
     * 
     * @param startFormatted
     *     The start_formatted
     */
    public void setStartFormatted(Object startFormatted) {
        this.startFormatted = startFormatted;
    }

    /**
     * 
     * @return
     *     The end
     */
    public Object getEnd() {
        return end;
    }

    /**
     * 
     * @param end
     *     The end
     */
    public void setEnd(Object end) {
        this.end = end;
    }

    /**
     * 
     * @return
     *     The endFormatted
     */
    public Object getEndFormatted() {
        return endFormatted;
    }

    /**
     * 
     * @param endFormatted
     *     The end_formatted
     */
    public void setEndFormatted(Object endFormatted) {
        this.endFormatted = endFormatted;
    }

}
