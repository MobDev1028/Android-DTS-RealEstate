package com.dts.dts.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dts.dts.R;
import com.dts.dts.adapters.AmenityGridAdapter;
import com.dts.dts.utils.LocalPreferences;
import com.dts.dts.utils.SearchJsonCreator;
import com.dts.dts.adapters.PlaceAutoCompleteAdapter;
import com.dts.dts.views.CustomDatePickerDialog;
import com.dts.dts.views.DelayAutoCompleteTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.apptik.widget.MultiSlider;

import static java.security.AccessController.getContext;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private static final int PRICE_MIN = 0;
//    private static final int PRICE_MAX = 10000;
    private static final int PRICE_MAX = 6000;

    private static final int THRESHOLD = 0;
    private MultiSlider priceRanger;
    private TextView priceTextView;
    private int priceBottomLimit = PRICE_MIN;
    private int priceTopLimit = PRICE_MAX;
    private TextView btnToggleFilters;
    private List<String> buildingAmenities;
    private AmenityGridAdapter buildingAmenityAdapter;
    private AmenityGridAdapter unitAmenityAdapter;
    private List<String> unitAmenities;
    private TextView startDateView;
    private TextView endDateView;

    private TextView shortTerm;
    private TextView longTerm;
    private TextView appartment;
    private TextView condo;
    private TextView house;
    private TextView other;

    private Spinner frequency;

    private ImageView closeButton;

    private SearchJsonCreator jsonCreator = new SearchJsonCreator();

    private int numOfBath;
    private int numOfBeds;

    private ArrayList<String> terms = new ArrayList<>();
    private ArrayList<String> listingType = new ArrayList<>();

    private boolean isSearchProcessing;
    private DelayAutoCompleteTextView bookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initializeViews();

        overridePendingTransition(R.anim.slide_in_up, 0);
    }

    private void initializeViews() {
        priceRanger = (MultiSlider) findViewById(R.id.range_price);
        priceTextView = (TextView) findViewById(R.id.txt_property_price);
        btnToggleFilters = (TextView) findViewById(R.id.btn_toggle_additional_filter);
        startDateView = (TextView) findViewById(R.id.txt_start_date);
        endDateView = (TextView) findViewById(R.id.txt_end_date);
        frequency = (Spinner) findViewById(R.id.frequency);

        findViewById(R.id.btn_reset_search).setOnClickListener(this);
        findViewById(R.id.img_logo_ditch).setOnClickListener(this);
        findViewById(R.id.btn_search_now).setOnClickListener(this);

        closeButton = (ImageView) findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookTitle.setText("");
                closeButton.setVisibility(View.INVISIBLE);
                bookTitle.setFocusable(true);
                bookTitle.setFocusableInTouchMode(true);
                bookTitle.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(bookTitle, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        String searchJson = LocalPreferences.getSearchCriteria(this);

        setupPlacesSearchView();
        setupMultiSlider();
        setPriceText();
        setUpLastTerm();
        setupListingType();
        setupBedroomMultiOptions();
        setupBathMultiOptions();
        setupCatDogFilter();
        setupAdditionalFilters();
        setupSearchTypeOptions();


        loadSavedSearchCriteria();

        setupUI(findViewById(R.id.parent_layout));
    }


    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof DelayAutoCompleteTextView)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(bookTitle.getWindowToken(), 0);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    private void loadSavedSearchCriteria(){
        String searchJson = LocalPreferences.getSearchCriteria(this);

        if (searchJson == null || searchJson.isEmpty()){
            setDefaultValues();
            return;
        }
        else{
            initItems();
        }
//        Gson gson = new GsonBuilder().create();
//        Log.d(TAG, "parseSearchJson: " + searchJson);
//        List<String> keysUnitAM =
//                Arrays.asList(getResources().getStringArray(R.array.key_unit_amenities));
//        List<String> keysBuildAM =
//                Arrays.asList(getResources().getStringArray(R.array.key_build_amenities));
//        JsonArray criteriaArray =
//                gson.fromJson(searchJson, JsonObject.class).getAsJsonArray("criteria");

        parseSearchJson(searchJson);
    }

    private void initItems()
    {
        bookTitle.setText("Current Location");
        priceRanger.getThumb(0).setValue(PRICE_MIN);
        priceRanger.getThumb(1).setValue(PRICE_MAX);
        priceBottomLimit = PRICE_MIN;
        priceTopLimit = PRICE_MAX;


        shortTerm.setSelected(true);
        onClick(shortTerm);

        longTerm.setSelected(true);
        onClick(longTerm);

        appartment.setSelected(true);
        onClick(appartment);

        condo.setSelected(true);
        onClick(condo);

        house.setSelected(true);
        onClick(house);

        other.setSelected(true);
        onClick(other);

        numOfBeds = -1;
        findViewById(R.id.bed_option_1).setSelected(false);
        findViewById(R.id.bed_option_2).setSelected(false);
        findViewById(R.id.bed_option_3).setSelected(false);
        findViewById(R.id.bed_option_4).setSelected(false);

        numOfBath = -1;
        findViewById(R.id.bath_option_1).setSelected(false);
        findViewById(R.id.bath_option_2).setSelected(false);
        findViewById(R.id.bath_option_3).setSelected(false);
        findViewById(R.id.bath_option_4).setSelected(false);
        findViewById(R.id.bath_option_5).setSelected(false);

        findViewById(R.id.btn_filter_dogs).setSelected(true);
        onClick(findViewById(R.id.btn_filter_dogs));
        findViewById(R.id.btn_filter_cats).setSelected(true);
        onClick(findViewById(R.id.btn_filter_cats));


        frequency.setSelection(0);

        btnToggleFilters.setVisibility(View.VISIBLE);
        selectSearchOptionType(findViewById(R.id.btn_search_regular));
        findViewById(R.id.btn_search_regular).setSelected(true);

        if (unitAmenityAdapter != null)
            unitAmenityAdapter.clearSelections();
        if (buildingAmenityAdapter != null) {
            buildingAmenityAdapter.clearSelections();
        }


        findViewById(R.id.ly_additional_filters).setVisibility(View.GONE);
        findViewById(R.id.ly_search_agent_options).setVisibility(View.GONE);
    }

    private void setUpLastTerm()
    {
        shortTerm = (TextView) findViewById(R.id.shortterm);
        longTerm = (TextView) findViewById(R.id.longterm);
        shortTerm.setSelected(true);
        onClick(shortTerm);
        longTerm.setSelected(false);
        onClick(longTerm);

        shortTerm.setOnClickListener(this);
        longTerm.setOnClickListener(this);
    }

    private void setupListingType()
    {
        appartment = (TextView) findViewById(R.id.apartment);
        condo = (TextView) findViewById(R.id.condo);
        house = (TextView) findViewById(R.id.house);
        other = (TextView) findViewById(R.id.other);

        appartment.setSelected(false);
        onClick(appartment);

        condo.setSelected(false);
        onClick(condo);

        house.setSelected(false);
        onClick(house);

        other.setSelected(false);
        onClick(other);

        appartment.setOnClickListener(this);
        condo.setOnClickListener(this);
        house.setOnClickListener(this);
        other.setOnClickListener(this);


    }

    private void setupPlacesSearchView() {
        bookTitle = (DelayAutoCompleteTextView) findViewById(R.id.et_places);
        bookTitle.setThreshold(THRESHOLD);
        bookTitle.setAdapter(new PlaceAutoCompleteAdapter(this)); // 'this' is Activity instance
//        bookTitle.setLoadingIndicator(
//                (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator));
        bookTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                /*Book book = (Book) adapterView.getItemAtPosition(position);
                bookTitle.setText(book.getTitle());*/
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(bookTitle.getWindowToken(), 0);
            }
        });

        bookTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                bookTitle.setCursorVisible(true);
                bookTitle.setFocusable(true);
                bookTitle.requestFocus();
                bookTitle.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(bookTitle, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        bookTitle.setText("Current Location");
//        bookTitle.setCursorVisible(false);

        bookTitle.setFocusable(false);

        bookTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0)
                    closeButton.setVisibility(View.VISIBLE);
                else
                    closeButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setDefaultValues() {
        bookTitle.setText("Current Location");
        priceRanger.getThumb(0).setValue(PRICE_MIN);
        priceRanger.getThumb(1).setValue(PRICE_MAX);
        priceBottomLimit = PRICE_MIN;
        priceTopLimit = PRICE_MAX;

        numOfBeds = -1;
        findViewById(R.id.bed_option_1).setSelected(false);
        findViewById(R.id.bed_option_2).setSelected(false);
        findViewById(R.id.bed_option_3).setSelected(false);
        findViewById(R.id.bed_option_4).setSelected(false);

        numOfBath = -1;
        findViewById(R.id.bath_option_1).setSelected(false);
        findViewById(R.id.bath_option_2).setSelected(false);
        findViewById(R.id.bath_option_3).setSelected(false);
        findViewById(R.id.bath_option_4).setSelected(false);
        findViewById(R.id.bath_option_5).setSelected(false);

        findViewById(R.id.btn_filter_dogs).setSelected(true);
        onClick(findViewById(R.id.btn_filter_dogs));
        findViewById(R.id.btn_filter_cats).setSelected(true);
        onClick(findViewById(R.id.btn_filter_cats));

        shortTerm.setSelected(true);
        onClick(shortTerm);

        longTerm.setSelected(false);
        onClick(longTerm);

        appartment.setSelected(false);
        onClick(appartment);

        condo.setSelected(false);
        onClick(condo);

        house.setSelected(false);
        onClick(house);

        other.setSelected(false);
        onClick(other);


        frequency.setSelection(0);

        btnToggleFilters.setVisibility(View.VISIBLE);
        selectSearchOptionType(findViewById(R.id.btn_search_regular));
        findViewById(R.id.btn_search_regular).setSelected(true);

        if (unitAmenityAdapter != null)
            unitAmenityAdapter.clearSelections();
        if (buildingAmenityAdapter != null) {
            buildingAmenityAdapter.clearSelections();
        }


        findViewById(R.id.ly_additional_filters).setVisibility(View.GONE);
        findViewById(R.id.ly_search_agent_options).setVisibility(View.GONE);

    }

    private void parseSearchJson(String searchJson) {

        Gson gson = new GsonBuilder().create();
        Log.d(TAG, "parseSearchJson: " + searchJson);
        List<String> keysUnitAM =
                Arrays.asList(getResources().getStringArray(R.array.key_unit_amenities));
        List<String> keysBuildAM =
                Arrays.asList(getResources().getStringArray(R.array.key_build_amenities));
        JsonArray criteriaArray =
                gson.fromJson(searchJson, JsonObject.class).getAsJsonArray("criteria");

        if (buildingAmenities != null)
            buildingAmenities.clear();
        if (unitAmenities != null)
            unitAmenities.clear();

        for (int i = 0; i < criteriaArray.size(); i++) {
            JsonObject object = criteriaArray.get(i).getAsJsonObject();
            if (object.get("field").getAsString().equals("region")){
                String city = object.get("value").getAsString();
                city = city.replace("city|", "");
                bookTitle.setText(city);
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(bookTitle.getWindowToken(), 0);
            }
            else if(object.get("field").getAsString().equals("geo")){
                bookTitle.setText("Current Location");
            }

            if (object.get("field").getAsString().equals("price")) {
                if (object.get("operator").getAsString().equals("<=")) {
                    priceTopLimit = object.get("value").getAsInt();
                    priceRanger.getThumb(1).setValue(priceTopLimit);
                } else if (object.get("operator").getAsString().equals(">=")) {
                    priceBottomLimit = object.get("value").getAsInt();
                    priceRanger.getThumb(0).setValue(priceBottomLimit);
                }
            }
            if (object.get("field").getAsString().equals("term")){
                JsonArray savedTerms = object.getAsJsonArray("value");
                for (int j = 0; j < savedTerms.size(); j++){
                    String termItem = savedTerms.get(j).getAsString();
                    if (termItem.equals("short")) {
                        shortTerm.setSelected(false);
                        onClick(shortTerm);
                    } else if (termItem.equals("long")) {
                        longTerm.setSelected(false);
                        onClick(longTerm);
                    }

                }
            }

            if (object.get("field").getAsString().equals("type")) {
                JsonArray savedTypes = object.getAsJsonArray("value");
                for (int j = 0; j < savedTypes.size(); j++)
                {
                    String typeItem = savedTypes.get(j).getAsString();
                    if (typeItem.equals("apt")) {
                        appartment.setSelected(false);
                        onClick(appartment);
                    } else if (typeItem.equals("condo")) {
                        condo.setSelected(false);
                        onClick(condo);
                    } else if (typeItem.equals("house")) {
                        house.setSelected(false);
                        onClick(house);
                    } else if (typeItem.equals("other")) {
                        other.setSelected(false);
                        onClick(other);
                    }
                }
            }
            if (object.get("field").getAsString().equals("bed")) {
                numOfBeds = object.get("value").getAsInt();
                switch (numOfBeds) {
                    case 1:
                        findViewById(R.id.bed_option_1).setSelected(true);
                        break;
                    case 2:
                        findViewById(R.id.bed_option_2).setSelected(true);
                        break;
                    case 3:
                        findViewById(R.id.bed_option_3).setSelected(true);
                        break;
                    case 4:
                        findViewById(R.id.bed_option_4).setSelected(true);
                        break;
                }
            }
            if (object.get("field").getAsString().equals("bath")) {
                numOfBath = object.get("value").getAsInt();
                switch (numOfBath) {
                    case 1:
                        findViewById(R.id.bath_option_1).setSelected(true);
                        break;
                    case 2:
                        findViewById(R.id.bath_option_2).setSelected(true);
                        break;
                    case 3:
                        findViewById(R.id.bath_option_3).setSelected(true);
                        break;
                    case 4:
                        findViewById(R.id.bath_option_4).setSelected(true);
                        break;
                    case 5:
                        findViewById(R.id.bath_option_5).setSelected(true);
                        break;
                }

            }
            if (object.get("field").getAsString().equals("dog")) {
                findViewById(R.id.btn_filter_dogs).setSelected(false);
                onClick(findViewById(R.id.btn_filter_dogs));

            }

            if (object.get("field").getAsString().equals("cat")) {
                findViewById(R.id.btn_filter_cats).setSelected(false);
                onClick(findViewById(R.id.btn_filter_cats));
            }


//            if (object.get("field").getAsString().startsWith("build_"))
//            {
//                if (buildingAmenities == null)
//                    buildingAmenities = Arrays.asList(getResources().getStringArray(R.array.text_build_amenities));
//                if (buildingAmenityAdapter == null)
//                    buildingAmenityAdapter = new AmenityGridAdapter(this, buildingAmenities);
//
//                buildingAmenityAdapter.setSelection(keysBuildAM.indexOf(object.get("field").getAsString()));
//
//            }
//
//            if (object.get("field").getAsString().startsWith("unit_")) {
//                if (unitAmenities == null)
//                    unitAmenities = Arrays.asList(getResources().getStringArray(R.array.text_unit_amenities));
//
//                if (unitAmenityAdapter == null)
//                    unitAmenityAdapter = new AmenityGridAdapter(this, unitAmenities);
//
//                unitAmenityAdapter.setSelection(keysUnitAM.indexOf(object.get("field").getAsString()));
//            }



            if (keysUnitAM.contains(object.get("field").getAsString())) {
                if (btnToggleFilters.getTag().equals("hidden")) {
                    toggleAdditionalFilters();
                }
                if (unitAmenities == null)
                    unitAmenities = Arrays.asList(getResources().getStringArray(R.array.text_unit_amenities));
                if (unitAmenityAdapter == null)
                    unitAmenityAdapter = new AmenityGridAdapter(this, unitAmenities);

                unitAmenityAdapter.setSelection(keysUnitAM.indexOf(object.get("field").getAsString()));

            } else if (keysBuildAM.contains(object.get("field").getAsString())) {
                if (btnToggleFilters.getTag().equals("hidden")) {
                    toggleAdditionalFilters();
                }
                if (buildingAmenities == null)
                    buildingAmenities = Arrays.asList(getResources().getStringArray(R.array.text_build_amenities));
                if (buildingAmenityAdapter == null)
                    buildingAmenityAdapter = new AmenityGridAdapter(this, buildingAmenities);

                buildingAmenityAdapter.setSelection(keysBuildAM.indexOf(object.get("field").getAsString()));
            }

        }
    }

    private void setupSearchTypeOptions() {
        findViewById(R.id.btn_search_regular).setOnClickListener(this);
        findViewById(R.id.btn_search_agent).setOnClickListener(this);

        findViewById(R.id.btn_search_regular).setSelected(true);
        startDateView.setOnClickListener(this);
        endDateView.setOnClickListener(this);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String currentDate = dateFormat.format(c.getTime());


        c.add(Calendar.DAY_OF_YEAR, 7);
        Date date = c.getTime();
        String endDate = dateFormat.format(c.getTime());

        startDateView.setText(currentDate);
        endDateView.setText(endDate);
    }

    private void setupAdditionalFilters() {
//        findViewById(R.id.btn_filter_apartment).setOnClickListener(this);
//        findViewById(R.id.btn_filter_condo).setOnClickListener(this);
//        findViewById(R.id.btn_filter_house).setOnClickListener(this);
//        findViewById(R.id.btn_filter_other).setOnClickListener(this);
//
        findViewById(R.id.btn_amenities_building).setOnClickListener(this);
        findViewById(R.id.btn_amenities_unit).setOnClickListener(this);
        findViewById(R.id.btn_close_amenities).setOnClickListener(this);
        findViewById(R.id.btn_select_amenities).setOnClickListener(this);

        btnToggleFilters.setOnClickListener(this);
    }

    private void setupCatDogFilter() {
        findViewById(R.id.btn_filter_cats).setOnClickListener(this);
        findViewById(R.id.btn_filter_dogs).setOnClickListener(this);
    }

    private void setupBedroomMultiOptions() {
        findViewById(R.id.bed_option_1).setOnClickListener(this);
        findViewById(R.id.bed_option_2).setOnClickListener(this);
        findViewById(R.id.bed_option_3).setOnClickListener(this);
        findViewById(R.id.bed_option_4).setOnClickListener(this);
    }

    private void setupBathMultiOptions() {
        findViewById(R.id.bath_option_1).setOnClickListener(this);
        findViewById(R.id.bath_option_2).setOnClickListener(this);
        findViewById(R.id.bath_option_3).setOnClickListener(this);
        findViewById(R.id.bath_option_4).setOnClickListener(this);
        findViewById(R.id.bath_option_5).setOnClickListener(this);
    }

    private void setupMultiSlider() {
        priceRanger.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    priceBottomLimit = thumb.getValue();
                } else {
                    priceTopLimit = thumb.getValue();
                }
                setPriceText();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void setPriceText() {
        if (priceTopLimit < PRICE_MAX)
            priceTextView.setText(String.format("Over ($%dK to $%dK)"
                , priceBottomLimit, priceTopLimit));
        else
            priceTextView.setText(String.format("Over ($%dK to All)"
                    , priceBottomLimit));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
//        performSearch();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(bookTitle.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_logo_ditch:
                onBackPressed();
                break;
            case R.id.bed_option_1:
            case R.id.bed_option_2:
            case R.id.bed_option_3:
            case R.id.bed_option_4:
                selectBedroomOptionsItem(view);
                break;
            case R.id.bath_option_1:
            case R.id.bath_option_2:
            case R.id.bath_option_3:
            case R.id.bath_option_4:
            case R.id.bath_option_5:
                selectBathOptionsItem(view);
                break;
            case R.id.btn_filter_cats:
            case R.id.btn_filter_dogs:
                view.setSelected(!view.isSelected());
                break;
//            case R.id.btn_filter_apartment:
//            case R.id.btn_filter_condo:
//            case R.id.btn_filter_house:
//            case R.id.btn_filter_other:
//                view.setSelected(!view.isSelected());
//                break;
            case R.id.btn_toggle_additional_filter:
                toggleAdditionalFilters();
                break;
            case R.id.btn_search_agent:
                selectSearchOptionType(view);
                findViewById(R.id.ly_search_agent_options).setVisibility(View.VISIBLE);
                break;
            case R.id.btn_search_regular:
                selectSearchOptionType(view);
                findViewById(R.id.ly_search_agent_options).setVisibility(View.GONE);
                break;
            case R.id.btn_amenities_unit:
                showUnitAmenities();
                break;
            case R.id.btn_amenities_building:
                showBuildingAmenities();
                break;
            case R.id.btn_close_amenities:
                if (unitAmenityAdapter != null) {
                    unitAmenityAdapter.clearSelections();
                    for (int i = 0; i < selectedAmenties.size(); i++) {
                        unitAmenityAdapter.toggleSelection(selectedAmenties.get(i));
                    }
                }
                if (buildingAmenityAdapter != null) {
                    buildingAmenityAdapter.clearSelections();
                    for (int i = 0; i < selectedAmenties.size(); i++) {
                        buildingAmenityAdapter.toggleSelection(selectedAmenties.get(i));
                    }
                }

            case R.id.btn_select_amenities:
                View grid = findViewById(R.id.ly_amenity_grid);
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        findViewById(R.id.ly_amenity_grid).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                grid.startAnimation(animation);

                break;
            case R.id.txt_start_date:
                showDateDialog("start");
                break;
            case R.id.txt_end_date:
                showDateDialog("end");
                break;
            case R.id.btn_search_now:
                performSearch();
                break;
            case R.id.btn_reset_search:
                setDefaultValues();
                break;
            case R.id.shortterm:

                int count = terms.size();
                for (int i = count-1; i >= 0; i--) {
                    String object = terms.get(i);
                    if (object.equalsIgnoreCase("short")) {
                        terms.remove(object);
                        break;
                    }
                }
                shortTerm.setSelected(!shortTerm.isSelected());
                break;
            case R.id.longterm:
                count = terms.size();
                for (int i = count-1; i >= 0; i--) {
                    String object = terms.get(i);
                    if (object.equalsIgnoreCase("long")) {
                        terms.remove(object);
                        break;
                    }
                }

                longTerm.setSelected(!longTerm.isSelected());
                break;
            case R.id.apartment:
            case R.id.condo:
            case R.id.house:
            case R.id.other:
                TextView textView = (TextView) view;
                String type = textView.getText().toString();
                if (type.equalsIgnoreCase("Apartment")) {
                    type = "apt";
                }
                type = type.toLowerCase();

                count = listingType.size();
                for (int i = count-1; i >= 0; i--) {
                    String object = listingType.get(i);
                    if (object.equalsIgnoreCase(type)) {
                        listingType.remove(object);
                        break;
                    }
                }

                textView.setSelected(!textView.isSelected());
                break;
        }
    }

    public List<Integer> selectedAmenties;


    private void showBuildingAmenities() {
        TextView title = (TextView) findViewById(R.id.amenities_title);

        findViewById(R.id.ly_amenity_grid).setVisibility(View.VISIBLE);
        View view = findViewById(R.id.ly_amenity_grid);
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up));
        title.setText("Building Amenities");

        GridView gridView = (GridView) findViewById(R.id.grid_amenities);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                buildingAmenityAdapter.toggleSelection(i);
//            }
//        });

        if (buildingAmenities == null) {
            buildingAmenities = Arrays.asList(getResources().getStringArray(R.array.text_build_amenities));
        }

        if (buildingAmenityAdapter == null)
            buildingAmenityAdapter = new AmenityGridAdapter(this, buildingAmenities);

        selectedAmenties = buildingAmenityAdapter.getSelectedItems();

        gridView.setAdapter(buildingAmenityAdapter);
    }


    private void showUnitAmenities() {
        TextView title = (TextView) findViewById(R.id.amenities_title);
        title.setText("Unit Amenities");

        View view = findViewById(R.id.ly_amenity_grid);
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up));
        findViewById(R.id.ly_amenity_grid).setVisibility(View.VISIBLE);

        GridView gridView = (GridView) findViewById(R.id.grid_amenities);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                unitAmenityAdapter.toggleSelection(i);
//            }
//        });

        if (unitAmenities == null) {
            unitAmenities = Arrays.asList(getResources().getStringArray(R.array.text_unit_amenities));
        }

        if (unitAmenityAdapter == null)
            unitAmenityAdapter = new AmenityGridAdapter(this, unitAmenities);
        selectedAmenties = unitAmenityAdapter.getSelectedItems();
        gridView.setAdapter(unitAmenityAdapter);
    }

    private void selectSearchOptionType(View view) {
        findViewById(R.id.btn_search_regular).setSelected(false);
        findViewById(R.id.btn_search_agent).setSelected(false);

        view.setSelected(true);
    }

    private void toggleAdditionalFilters() {
        boolean isHidden = btnToggleFilters.getTag().equals("hidden");

        findViewById(R.id.ly_additional_filters)
                .setVisibility(isHidden ? View.VISIBLE : View.GONE);

        btnToggleFilters.setText(isHidden ? R.string.reset_all_filters
                : R.string.show_more_filters);

        btnToggleFilters.setTextColor(ContextCompat.getColor(this, isHidden
                ? R.color.colorSearchFiltersToggle : android.R.color.black));

        btnToggleFilters.setTag(isHidden ? "shown" : "hidden");
    }

    private void selectBedroomOptionsItem(View view) {
        findViewById(R.id.bed_option_1).setSelected(false);
        findViewById(R.id.bed_option_2).setSelected(false);
        findViewById(R.id.bed_option_3).setSelected(false);
        findViewById(R.id.bed_option_4).setSelected(false);

        numOfBeds = Integer.parseInt(((TextView) view).getText().toString());

        view.setSelected(true);
    }

    private void selectBathOptionsItem(View view) {
        findViewById(R.id.bath_option_1).setSelected(false);
        findViewById(R.id.bath_option_2).setSelected(false);
        findViewById(R.id.bath_option_3).setSelected(false);
        findViewById(R.id.bath_option_4).setSelected(false);
        findViewById(R.id.bath_option_5).setSelected(false);

        numOfBath = Integer.parseInt(view.getTag().toString());

        view.setSelected(true);
    }

    private void showDateDialog(String type) {
        FragmentManager fm = getSupportFragmentManager();
        CustomDatePickerDialog newFragment = new CustomDatePickerDialog(this);
        newFragment.show(fm, type + "_date_picker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        String date = String.format(Locale.ENGLISH
                , "%d-%s-%s", year, monthOfYear < 10 ? "0" + monthOfYear : monthOfYear + ""
                , dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "");
        switch (datePicker.getTag().toString()) {
            case "start_date_picker":
                startDateView.setText(date);
                break;
            case "end_date_picker":
                endDateView.setText(date);
                break;
        }
    }

    private void performSearch() {
        jsonCreator.clearAll();


        if (isSearchProcessing) {
            return;
        }
        isSearchProcessing = true;

        String address = bookTitle.getText().toString();
        String locality = "";
        if (address == null || address.length() == 0 || address.equalsIgnoreCase("Current Location")) {
            locality = "";
        } else {
            String[] addresses = address.split(",");
            if (addresses.length > 0){
                locality = addresses[0];
            }
        }

        if (locality != null && locality.length() > 0) {
            String region = "city|"+ locality;
            jsonCreator.addJsonItem("region", "=", region);
        } else {
            Location currentCoordinate = LocalPreferences.getSelectedLat(this);

            String geo = currentCoordinate.getLatitude() + "|" + currentCoordinate.getLongitude() + "|10";
            jsonCreator.addJsonItem("geo", "=", geo);
        }


//        jsonCreator.addJsonItem("price", ">=", priceBottomLimit + "");


        getPermanentItemsJson();

        getListingTypeJson();

        getUnitAmenityJson();
        getBuildAmenityJson();
        getAgentOptions();

        String jsonParams = jsonCreator.createJson();

        Log.d(TAG, "performSearch: " + jsonParams);

        LocalPreferences.saveSearchCriteria(this, jsonParams);
        LocalPreferences.saveFlagSearchNow(this, true);
        LocalPreferences.saveFlagSearchAgent(this,findViewById(R.id.btn_search_agent).isSelected());

        finish();

        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);

    }

    private void getAgentOptions(){


//        if(findViewById(R.id.btn_search_regular).isSelected() && findViewById(R.id.btn_search_agent).isSelected() == false){
//            return;
//        }
//        else {
//
//            String scheduleString = "{\"criteria\":[" + jsonString + "]}";
//
//            NSDictionary* scheduleDict = @{@"frequency":[self.frequencyTextField.text lowercaseString], @"start":self.startTextField.text, @"end":self.endTextField.text};
//            userInfo = @{@"criteria":searchCriteria, @"schedule":scheduleDict};
//        }
    }

    private void getPermanentItemsJson() {
        jsonCreator.addJsonItem("price", ">=", priceBottomLimit + "");
        if (priceTopLimit < PRICE_MAX)
            jsonCreator.addJsonItem("price", "<=", priceTopLimit + "");

        if (numOfBath >= 0)
            jsonCreator.addJsonItem("bath", ">=", numOfBath + "");
        if (numOfBeds >= 0)
            jsonCreator.addJsonItem("bed", "=", numOfBeds + "");

        if (findViewById(R.id.btn_filter_dogs).isSelected()) {
            jsonCreator.addJsonItem("dog", "=", "1");
        }
        if (findViewById(R.id.btn_filter_cats).isSelected()) {
            jsonCreator.addJsonItem("cat", "=", "1");
        }
    }

    private void getListingTypeJson() {
        String term = "";
        if (shortTerm.isSelected()) {
            if (!term.isEmpty())
                term += ",";
            term += "\"short\"";
        }
        if (longTerm.isSelected()) {
            if (!term.isEmpty())
                term += ",";
            term += "\"long\"";
        }
        if (!term.isEmpty()) {
            term = "[" + term + "]";
            jsonCreator.addJsonItemAsArray("term", "in", term);
        }
        String listTypes = "";

        if (appartment.isSelected()) {
            if (!listTypes.isEmpty())
                listTypes += ",";
            listTypes += "\"apt\"";
        }
        if (condo.isSelected()) {
            if (!listTypes.isEmpty())
                listTypes += ",";
            listTypes += "\"condo\"";
        }
        if (house.isSelected()) {
            if (!listTypes.isEmpty())
                listTypes += ",";
            listTypes += "\"house\"";
        }
        if (other.isSelected()) {
            if (!listTypes.isEmpty())
                listTypes += ",";
            listTypes += "\"other\"";
        }

        if (!listTypes.isEmpty()) {
            listTypes = "[" + listTypes + "]";
            jsonCreator.addJsonItemAsArray("type", "in", listTypes);
        }
    }

    private void getBuildAmenityJson() {
        if (buildingAmenities != null) {
            List<Integer> selectedItems = buildingAmenityAdapter.getSelectedItems();
            String[] keysBuildAM = getResources().getStringArray(R.array.key_build_amenities);
            for (String amenity : buildingAmenities) {
                if (selectedItems.contains(buildingAmenities.indexOf(amenity)))
                    jsonCreator.addJsonItem(keysBuildAM[buildingAmenities.indexOf(amenity)], "=", "1");
            }
        }
    }

    private void getUnitAmenityJson() {
        if (unitAmenities != null) {
            List<Integer> selectedItems = unitAmenityAdapter.getSelectedItems();
            String[] keysUnitAM = getResources().getStringArray(R.array.key_unit_amenities);
            for (String amenity : unitAmenities) {
                if (selectedItems.contains(unitAmenities.indexOf(amenity)))
                    jsonCreator.addJsonItem(keysUnitAM[unitAmenities.indexOf(amenity)], "=", "1");
            }
        }
    }
}
