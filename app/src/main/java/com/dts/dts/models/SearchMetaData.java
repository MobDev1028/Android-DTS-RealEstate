
package com.dts.dts.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchMetaData {

    @SerializedName("user_searches")
    @Expose
    private List<UserSearch> userSearches = new ArrayList<UserSearch>();
    @SerializedName("properties")
    @Expose
    private Object properties;
    @SerializedName("search_agent_results")
    @Expose
    private Object searchAgentResults;
    @SerializedName("paginator")
    @Expose
    private Object paginator;

    /**
     * 
     * @return
     *     The userSearches
     */
    public List<UserSearch> getUserSearches() {
        return userSearches;
    }

    /**
     * 
     * @param userSearches
     *     The user_searches
     */
    public void setUserSearches(List<UserSearch> userSearches) {
        this.userSearches = userSearches;
    }

    /**
     * 
     * @return
     *     The properties
     */
    public Object getProperties() {
        return properties;
    }

    /**
     * 
     * @param properties
     *     The properties
     */
    public void setProperties(Object properties) {
        this.properties = properties;
    }

    /**
     * 
     * @return
     *     The searchAgentResults
     */
    public Object getSearchAgentResults() {
        return searchAgentResults;
    }

    /**
     * 
     * @param searchAgentResults
     *     The search_agent_results
     */
    public void setSearchAgentResults(Object searchAgentResults) {
        this.searchAgentResults = searchAgentResults;
    }

    /**
     * 
     * @return
     *     The paginator
     */
    public Object getPaginator() {
        return paginator;
    }

    /**
     * 
     * @param paginator
     *     The paginator
     */
    public void setPaginator(Object paginator) {
        this.paginator = paginator;
    }

}
