
package com.dts.dts.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Property {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("author_user_id")
    @Expose
    private Integer authorUserId;
    @SerializedName("reviewed")
    @Expose
    private String reviewed;
    @SerializedName("deactivated")
    @Expose
    private Object deactivated;
    @SerializedName("deactivation_type")
    @Expose
    private Object deactivationType;
    @SerializedName("main_photo_id")
    @Expose
    private Object mainPhotoId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("year_built")
    @Expose
    private Integer yearBuilt;
    @SerializedName("lot_size")
    @Expose
    private Float lotSize;
    @SerializedName("modification_timestamp")
    @Expose
    private Object modificationTimestamp;
    @SerializedName("cat")
    @Expose
    private Integer cat;
    @SerializedName("dog")
    @Expose
    private Integer dog;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("bed")
    @Expose
    private Integer bed;
    @SerializedName("bath")
    @Expose
    private Integer bath;
//    @SerializedName("lot_size")
//    @Expose
//    private Integer lot_size;

    @SerializedName("price")
    @Expose
    private Float price;
    @SerializedName("term")
    @Expose
    private String term;
    @SerializedName("address1")
    @Expose
    private String address1;
    @SerializedName("address2")
    @Expose
    private String address2;
    @SerializedName("zip")
    @Expose
    private String zip;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state_or_province")
    @Expose
    private String stateOrProvince;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("unit_amen_ac")
    @Expose
    private Integer unitAmenAc;
    @SerializedName("unit_amen_parking_reserved")
    @Expose
    private Integer unitAmenParkingReserved;
    @SerializedName("unit_amen_balcony")
    @Expose
    private Integer unitAmenBalcony;
    @SerializedName("unit_amen_deck")
    @Expose
    private Integer unitAmenDeck;
    @SerializedName("unit_amen_ceiling_fan")
    @Expose
    private Integer unitAmenCeilingFan;
    @SerializedName("unit_amen_dishwasher")
    @Expose
    private Integer unitAmenDishwasher;
    @SerializedName("unit_amen_fireplace")
    @Expose
    private Integer unitAmenFireplace;
    @SerializedName("unit_amen_furnished")
    @Expose
    private Integer unitAmenFurnished;
    @SerializedName("unit_amen_laundry")
    @Expose
    private Integer unitAmenLaundry;
    @SerializedName("unit_amen_floor_carpet")
    @Expose
    private Integer unitAmenFloorCarpet;
    @SerializedName("unit_amen_floor_hard_wood")
    @Expose
    private Integer unitAmenFloorHardWood;
    @SerializedName("unit_amen_carpet")
    @Expose
    private Integer unitAmenCarpet;
    @SerializedName("build_amen_fitness_center")
    @Expose
    private Integer buildAmenFitnessCenter;
    @SerializedName("build_amen_biz_center")
    @Expose
    private Integer buildAmenBizCenter;
    @SerializedName("build_amen_concierge")
    @Expose
    private Integer buildAmenConcierge;
    @SerializedName("build_amen_doorman")
    @Expose
    private Integer buildAmenDoorman;
    @SerializedName("build_amen_dry_cleaning")
    @Expose
    private Integer buildAmenDryCleaning;
    @SerializedName("build_amen_elevator")
    @Expose
    private Integer buildAmenElevator;
    @SerializedName("build_amen_park_garage")
    @Expose
    private Integer buildAmenParkGarage;
    @SerializedName("build_amen_swim_pool")
    @Expose
    private Integer buildAmenSwimPool;
    @SerializedName("build_amen_secure_entry")
    @Expose
    private Integer buildAmenSecureEntry;
    @SerializedName("build_amen_storage")
    @Expose
    private Integer buildAmenStorage;
    @SerializedName("keywords")
    @Expose
    private String keywords;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("disabled")
    @Expose
    private Integer disabled;
    @SerializedName("deleted")
    @Expose
    private Integer deleted;
    @SerializedName("availability_date")
    @Expose
    private Object availabilityDate;
    @SerializedName("bot")
    @Expose
    private Integer bot;
    @SerializedName("selected_unit_amenities")
    @Expose
    private String selectedUnitAmenities;
    @SerializedName("selected_building_amenities")
    @Expose
    private String selectedBuildingAmenities;
    @SerializedName("imgs")
    @Expose
    private List<Img> imgs = new ArrayList<Img>();
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("liked")
    @Expose
    private Integer liked;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("inquired")
    @Expose
    private Integer inquired;
    @SerializedName("img_url")
    @Expose
    private ImgUrl_ imgUrl;
    @SerializedName("created_at_formatted")
    @Expose
    private String createdAtFormatted;
    @SerializedName("updated_at_formatted")
    @Expose
    private String updatedAtFormatted;
    @SerializedName("date_liked_formatted")
    @Expose
    private String dateLikedFormatted;
    @SerializedName("author_user_info")
    @Expose
    private AuthorUserInfo authorUserInfo;
    @SerializedName("hidden")
    @Expose
    private int hidden;
    @SerializedName("security_deposit")
    @Expose
    private Float security_deposit;
    @SerializedName("move_in_cost")
    @Expose
    private String move_in_cost;

    /**
     *
     * @return
     *     The security_deposit
     */
    public Float getSecurity_deposit() {
        return security_deposit;
    }

    /**
     *
     * @param security_deposit
     *     The id
     */
    public void setSecurity_deposit(Float security_deposit) {
        this.security_deposit = security_deposit;
    }


    /**
     *
     * @return
     *     The move_in_cost
     */
    public String getMove_in_cost() {
        return move_in_cost;
    }

    /**
     *
     * @param move_in_cost
     *     The id
     */
    public void setMove_in_cost(String move_in_cost) {
        this.move_in_cost = move_in_cost;
    }


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
     *     The key
     */
    public String getKey() {
        return key;
    }

    /**
     * 
     * @param key
     *     The key
     */
    public void setKey(String key) {
        this.key = key;
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
     *     The source
     */
    public String getSource() {
        return source;
    }

    /**
     * 
     * @param source
     *     The source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 
     * @return
     *     The authorUserId
     */
    public Integer getAuthorUserId() {
        return authorUserId;
    }

    /**
     * 
     * @param authorUserId
     *     The author_user_id
     */
    public void setAuthorUserId(Integer authorUserId) {
        this.authorUserId = authorUserId;
    }

    /**
     * 
     * @return
     *     The reviewed
     */
    public String getReviewed() {
        return reviewed;
    }

    /**
     * 
     * @param reviewed
     *     The reviewed
     */
    public void setReviewed(String reviewed) {
        this.reviewed = reviewed;
    }

    /**
     * 
     * @return
     *     The deactivated
     */
    public Object getDeactivated() {
        return deactivated;
    }

    /**
     * 
     * @param deactivated
     *     The deactivated
     */
    public void setDeactivated(Object deactivated) {
        this.deactivated = deactivated;
    }

    /**
     * 
     * @return
     *     The deactivationType
     */
    public Object getDeactivationType() {
        return deactivationType;
    }

    /**
     * 
     * @param deactivationType
     *     The deactivation_type
     */
    public void setDeactivationType(Object deactivationType) {
        this.deactivationType = deactivationType;
    }

    /**
     * 
     * @return
     *     The mainPhotoId
     */
    public Object getMainPhotoId() {
        return mainPhotoId;
    }

    /**
     * 
     * @param mainPhotoId
     *     The main_photo_id
     */
    public void setMainPhotoId(Object mainPhotoId) {
        this.mainPhotoId = mainPhotoId;
    }

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    public int getHidden(){return hidden;}
    public void setHidden(int hidden){this.hidden = hidden;}
    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The yearBuilt
     */
    public Integer getYearBuilt() {
        return yearBuilt;
    }

    /**
     * 
     * @param yearBuilt
     *     The year_built
     */
    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    /**
     * 
     * @return
     *     The lotSize
     */
    public Float getLotSize() {
        return lotSize;
    }

    /**
     * 
     * @param lotSize
     *     The lot_size
     */
    public void setLotSize(Float lotSize) {
        this.lotSize = lotSize;
    }

    /**
     * 
     * @return
     *     The modificationTimestamp
     */
    public Object getModificationTimestamp() {
        return modificationTimestamp;
    }

    /**
     * 
     * @param modificationTimestamp
     *     The modification_timestamp
     */
    public void setModificationTimestamp(Object modificationTimestamp) {
        this.modificationTimestamp = modificationTimestamp;
    }

    /**
     * 
     * @return
     *     The cat
     */
    public Integer getCat() {
        return cat;
    }

    /**
     * 
     * @param cat
     *     The cat
     */
    public void setCat(Integer cat) {
        this.cat = cat;
    }

    /**
     * 
     * @return
     *     The dog
     */
    public Integer getDog() {
        return dog;
    }

    /**
     * 
     * @param dog
     *     The dog
     */
    public void setDog(Integer dog) {
        this.dog = dog;
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
     *     The bed
     */
    public Integer getBed() {
        return bed;
    }

    /**
     * 
     * @param bed
     *     The bed
     */
    public void setBed(Integer bed) {
        this.bed = bed;
    }

    /**
     * 
     * @return
     *     The bath
     */
    public Integer getBath() {
        return bath;
    }

    /**
     * 
     * @param bath
     *     The bath
     */
    public void setBath(Integer bath) {
        this.bath = bath;
    }

//    /**
//     *
//     * @return
//     *     The lot_size
//     */
//    public Integer getLot_size() {
//        return lot_size;
//    }
//
//    /**
//     *
//     * @param sqft
//     *     The sqft
//     */
//    public void setLot_size(Integer sqft) {
//        this.lot_size = lot_size;
//    }
//
//    /**
//     *
//     * @return
//     *     The price
//     */
    public Float getPrice() {
        return price;
    }

    /**
     * 
     * @param price
     *     The price
     */
    public void setPrice(Float price) {
        this.price = price;
    }

    /**
     * 
     * @return
     *     The term
     */
    public String getTerm() {
        return term;
    }

    /**
     * 
     * @param term
     *     The term
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * 
     * @return
     *     The address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * 
     * @param address1
     *     The address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * 
     * @return
     *     The address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * 
     * @param address2
     *     The address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * 
     * @return
     *     The zip
     */
    public String getZip() {
        return zip;
    }

    /**
     * 
     * @param zip
     *     The zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * 
     * @return
     *     The city
     */
    public String getCity() {
        return city;
    }

    /**
     * 
     * @param city
     *     The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 
     * @return
     *     The stateOrProvince
     */
    public String getStateOrProvince() {
        return stateOrProvince;
    }

    /**
     * 
     * @param stateOrProvince
     *     The state_or_province
     */
    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    /**
     * 
     * @return
     *     The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * @param country
     *     The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 
     * @return
     *     The unitAmenAc
     */
    public Integer getUnitAmenAc() {
        return unitAmenAc;
    }

    /**
     * 
     * @param unitAmenAc
     *     The unit_amen_ac
     */
    public void setUnitAmenAc(Integer unitAmenAc) {
        this.unitAmenAc = unitAmenAc;
    }

    /**
     * 
     * @return
     *     The unitAmenParkingReserved
     */
    public Integer getUnitAmenParkingReserved() {
        return unitAmenParkingReserved;
    }

    /**
     * 
     * @param unitAmenParkingReserved
     *     The unit_amen_parking_reserved
     */
    public void setUnitAmenParkingReserved(Integer unitAmenParkingReserved) {
        this.unitAmenParkingReserved = unitAmenParkingReserved;
    }

    /**
     * 
     * @return
     *     The unitAmenBalcony
     */
    public Integer getUnitAmenBalcony() {
        return unitAmenBalcony;
    }

    /**
     * 
     * @param unitAmenBalcony
     *     The unit_amen_balcony
     */
    public void setUnitAmenBalcony(Integer unitAmenBalcony) {
        this.unitAmenBalcony = unitAmenBalcony;
    }

    /**
     * 
     * @return
     *     The unitAmenDeck
     */
    public Integer getUnitAmenDeck() {
        return unitAmenDeck;
    }

    /**
     * 
     * @param unitAmenDeck
     *     The unit_amen_deck
     */
    public void setUnitAmenDeck(Integer unitAmenDeck) {
        this.unitAmenDeck = unitAmenDeck;
    }

    /**
     * 
     * @return
     *     The unitAmenCeilingFan
     */
    public Integer getUnitAmenCeilingFan() {
        return unitAmenCeilingFan;
    }

    /**
     * 
     * @param unitAmenCeilingFan
     *     The unit_amen_ceiling_fan
     */
    public void setUnitAmenCeilingFan(Integer unitAmenCeilingFan) {
        this.unitAmenCeilingFan = unitAmenCeilingFan;
    }

    /**
     * 
     * @return
     *     The unitAmenDishwasher
     */
    public Integer getUnitAmenDishwasher() {
        return unitAmenDishwasher;
    }

    /**
     * 
     * @param unitAmenDishwasher
     *     The unit_amen_dishwasher
     */
    public void setUnitAmenDishwasher(Integer unitAmenDishwasher) {
        this.unitAmenDishwasher = unitAmenDishwasher;
    }

    /**
     * 
     * @return
     *     The unitAmenFireplace
     */
    public Integer getUnitAmenFireplace() {
        return unitAmenFireplace;
    }

    /**
     * 
     * @param unitAmenFireplace
     *     The unit_amen_fireplace
     */
    public void setUnitAmenFireplace(Integer unitAmenFireplace) {
        this.unitAmenFireplace = unitAmenFireplace;
    }

    /**
     * 
     * @return
     *     The unitAmenFurnished
     */
    public Integer getUnitAmenFurnished() {
        return unitAmenFurnished;
    }

    /**
     * 
     * @param unitAmenFurnished
     *     The unit_amen_furnished
     */
    public void setUnitAmenFurnished(Integer unitAmenFurnished) {
        this.unitAmenFurnished = unitAmenFurnished;
    }

    /**
     * 
     * @return
     *     The unitAmenLaundry
     */
    public Integer getUnitAmenLaundry() {
        return unitAmenLaundry;
    }

    /**
     * 
     * @param unitAmenLaundry
     *     The unit_amen_laundry
     */
    public void setUnitAmenLaundry(Integer unitAmenLaundry) {
        this.unitAmenLaundry = unitAmenLaundry;
    }

    /**
     * 
     * @return
     *     The unitAmenFloorCarpet
     */
    public Integer getUnitAmenFloorCarpet() {
        return unitAmenFloorCarpet;
    }

    /**
     * 
     * @param unitAmenFloorCarpet
     *     The unit_amen_floor_carpet
     */
    public void setUnitAmenFloorCarpet(Integer unitAmenFloorCarpet) {
        this.unitAmenFloorCarpet = unitAmenFloorCarpet;
    }

    /**
     * 
     * @return
     *     The unitAmenFloorHardWood
     */
    public Integer getUnitAmenFloorHardWood() {
        return unitAmenFloorHardWood;
    }

    /**
     * 
     * @param unitAmenFloorHardWood
     *     The unit_amen_floor_hard_wood
     */
    public void setUnitAmenFloorHardWood(Integer unitAmenFloorHardWood) {
        this.unitAmenFloorHardWood = unitAmenFloorHardWood;
    }

    /**
     * 
     * @return
     *     The unitAmenCarpet
     */
    public Integer getUnitAmenCarpet() {
        return unitAmenCarpet;
    }

    /**
     * 
     * @param unitAmenCarpet
     *     The unit_amen_carpet
     */
    public void setUnitAmenCarpet(Integer unitAmenCarpet) {
        this.unitAmenCarpet = unitAmenCarpet;
    }

    /**
     * 
     * @return
     *     The buildAmenFitnessCenter
     */
    public Integer getBuildAmenFitnessCenter() {
        return buildAmenFitnessCenter;
    }

    /**
     * 
     * @param buildAmenFitnessCenter
     *     The build_amen_fitness_center
     */
    public void setBuildAmenFitnessCenter(Integer buildAmenFitnessCenter) {
        this.buildAmenFitnessCenter = buildAmenFitnessCenter;
    }

    /**
     * 
     * @return
     *     The buildAmenBizCenter
     */
    public Integer getBuildAmenBizCenter() {
        return buildAmenBizCenter;
    }

    /**
     * 
     * @param buildAmenBizCenter
     *     The build_amen_biz_center
     */
    public void setBuildAmenBizCenter(Integer buildAmenBizCenter) {
        this.buildAmenBizCenter = buildAmenBizCenter;
    }

    /**
     * 
     * @return
     *     The buildAmenConcierge
     */
    public Integer getBuildAmenConcierge() {
        return buildAmenConcierge;
    }

    /**
     * 
     * @param buildAmenConcierge
     *     The build_amen_concierge
     */
    public void setBuildAmenConcierge(Integer buildAmenConcierge) {
        this.buildAmenConcierge = buildAmenConcierge;
    }

    /**
     * 
     * @return
     *     The buildAmenDoorman
     */
    public Integer getBuildAmenDoorman() {
        return buildAmenDoorman;
    }

    /**
     * 
     * @param buildAmenDoorman
     *     The build_amen_doorman
     */
    public void setBuildAmenDoorman(Integer buildAmenDoorman) {
        this.buildAmenDoorman = buildAmenDoorman;
    }

    /**
     * 
     * @return
     *     The buildAmenDryCleaning
     */
    public Integer getBuildAmenDryCleaning() {
        return buildAmenDryCleaning;
    }

    /**
     * 
     * @param buildAmenDryCleaning
     *     The build_amen_dry_cleaning
     */
    public void setBuildAmenDryCleaning(Integer buildAmenDryCleaning) {
        this.buildAmenDryCleaning = buildAmenDryCleaning;
    }

    /**
     * 
     * @return
     *     The buildAmenElevator
     */
    public Integer getBuildAmenElevator() {
        return buildAmenElevator;
    }

    /**
     * 
     * @param buildAmenElevator
     *     The build_amen_elevator
     */
    public void setBuildAmenElevator(Integer buildAmenElevator) {
        this.buildAmenElevator = buildAmenElevator;
    }

    /**
     * 
     * @return
     *     The buildAmenParkGarage
     */
    public Integer getBuildAmenParkGarage() {
        return buildAmenParkGarage;
    }

    /**
     * 
     * @param buildAmenParkGarage
     *     The build_amen_park_garage
     */
    public void setBuildAmenParkGarage(Integer buildAmenParkGarage) {
        this.buildAmenParkGarage = buildAmenParkGarage;
    }

    /**
     * 
     * @return
     *     The buildAmenSwimPool
     */
    public Integer getBuildAmenSwimPool() {
        return buildAmenSwimPool;
    }

    /**
     * 
     * @param buildAmenSwimPool
     *     The build_amen_swim_pool
     */
    public void setBuildAmenSwimPool(Integer buildAmenSwimPool) {
        this.buildAmenSwimPool = buildAmenSwimPool;
    }

    /**
     * 
     * @return
     *     The buildAmenSecureEntry
     */
    public Integer getBuildAmenSecureEntry() {
        return buildAmenSecureEntry;
    }

    /**
     * 
     * @param buildAmenSecureEntry
     *     The build_amen_secure_entry
     */
    public void setBuildAmenSecureEntry(Integer buildAmenSecureEntry) {
        this.buildAmenSecureEntry = buildAmenSecureEntry;
    }

    /**
     * 
     * @return
     *     The buildAmenStorage
     */
    public Integer getBuildAmenStorage() {
        return buildAmenStorage;
    }

    /**
     * 
     * @param buildAmenStorage
     *     The build_amen_storage
     */
    public void setBuildAmenStorage(Integer buildAmenStorage) {
        this.buildAmenStorage = buildAmenStorage;
    }

    /**
     * 
     * @return
     *     The keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * 
     * @param keywords
     *     The keywords
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * 
     * @return
     *     The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param latitude
     *     The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * 
     * @return
     *     The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param longitude
     *     The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
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
     *     The availabilityDate
     */
    public Object getAvailabilityDate() {
        return availabilityDate;
    }

    /**
     * 
     * @param availabilityDate
     *     The availability_date
     */
    public void setAvailabilityDate(Object availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    /**
     * 
     * @return
     *     The bot
     */
    public Integer getBot() {
        return bot;
    }

    /**
     * 
     * @param bot
     *     The bot
     */
    public void setBot(Integer bot) {
        this.bot = bot;
    }

    /**
     * 
     * @return
     *     The selectedUnitAmenities
     */
    public String getSelectedUnitAmenities() {
        return selectedUnitAmenities;
    }

    /**
     * 
     * @param selectedUnitAmenities
     *     The selected_unit_amenities
     */
    public void setSelectedUnitAmenities(String selectedUnitAmenities) {
        this.selectedUnitAmenities = selectedUnitAmenities;
    }

    /**
     * 
     * @return
     *     The selectedBuildingAmenities
     */
    public String getSelectedBuildingAmenities() {
        return selectedBuildingAmenities;
    }

    /**
     * 
     * @param selectedBuildingAmenities
     *     The selected_building_amenities
     */
    public void setSelectedBuildingAmenities(String selectedBuildingAmenities) {
        this.selectedBuildingAmenities = selectedBuildingAmenities;
    }

    /**
     * 
     * @return
     *     The imgs
     */
    public List<Img> getImgs() {
        return imgs;
    }

    /**
     * 
     * @param imgs
     *     The imgs
     */
    public void setImgs(List<Img> imgs) {
        this.imgs = imgs;
    }

    /**
     * 
     * @return
     *     The likes
     */
    public Integer getLikes() {
        return likes;
    }

    /**
     * 
     * @param likes
     *     The likes
     */
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    /**
     * 
     * @return
     *     The liked
     */
    public Integer getLiked() {
        return liked;
    }

    /**
     * 
     * @param liked
     *     The liked
     */
    public void setLiked(Integer liked) {
        this.liked = liked;
    }

    /**
     * 
     * @return
     *     The address
     */
    public String getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 
     * @return
     *     The inquired
     */
    public Integer getInquired() {
        return inquired;
    }

    /**
     * 
     * @param inquired
     *     The inquired
     */
    public void setInquired(Integer inquired) {
        this.inquired = inquired;
    }

    /**
     * 
     * @return
     *     The imgUrl
     */
    public ImgUrl_ getImgUrl() {
        return imgUrl;
    }

    /**
     * 
     * @param imgUrl
     *     The img_url
     */
    public void setImgUrl(ImgUrl_ imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * 
     * @return
     *     The createdAtFormatted
     */
    public String getCreatedAtFormatted() {
        return createdAtFormatted;
    }

    /**
     * 
     * @param createdAtFormatted
     *     The created_at_formatted
     */
    public void setCreatedAtFormatted(String createdAtFormatted) {
        this.createdAtFormatted = createdAtFormatted;
    }

    /**
     * 
     * @return
     *     The updatedAtFormatted
     */
    public String getUpdatedAtFormatted() {
        return updatedAtFormatted;
    }

    /**
     * 
     * @param updatedAtFormatted
     *     The updated_at_formatted
     */
    public void setUpdatedAtFormatted(String updatedAtFormatted) {
        this.updatedAtFormatted = updatedAtFormatted;
    }

    /**
     * 
     * @return
     *     The dateLikedFormatted
     */
    public String getDateLikedFormatted() {
        return dateLikedFormatted;
    }

    /**
     * 
     * @param dateLikedFormatted
     *     The date_liked_formatted
     */
    public void setDateLikedFormatted(String dateLikedFormatted) {
        this.dateLikedFormatted = dateLikedFormatted;
    }

    /**
     * 
     * @return
     *     The authorUserInfo
     */
    public AuthorUserInfo getAuthorUserInfo() {
        return authorUserInfo;
    }

    /**
     * 
     * @param authorUserInfo
     *     The author_user_info
     */
    public void setAuthorUserInfo(AuthorUserInfo authorUserInfo) {
        this.authorUserInfo = authorUserInfo;
    }

    /*public static Comparator<Property> sTimeComparator = new Comparator<Property>() {
        @Override
        public int compare(Property lhs, Property rhs) {
            lhs.
            2016-04-08 05:30:57
            return lhLong.compareTo(rhLong);
        }
    };*/

}
