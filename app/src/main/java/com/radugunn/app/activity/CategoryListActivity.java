package com.radugunn.app.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.radugunn.app.R;
import com.radugunn.app.adapter.CategoryGridAdapter;
import com.radugunn.app.adapter.CategoryListAdapter;
import com.radugunn.app.adapter.SortAdapter;
import com.radugunn.app.customview.EqualSpacingItemDecoration;
import com.radugunn.app.customview.GridSpacingItemDecoration;
import com.radugunn.app.customview.textview.TextViewBold;
import com.radugunn.app.customview.textview.TextViewLight;
import com.radugunn.app.customview.textview.TextViewRegular;
import com.radugunn.app.javaclasses.FilterSelectedList;
import com.radugunn.app.model.CategoryList;
import com.radugunn.app.model.FilterOtherOption;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.Config;
import com.radugunn.app.utils.Constant;
import com.radugunn.app.utils.RequestParamUtils;
import com.radugunn.app.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoryListActivity extends BaseActivity implements OnResponseListner {

    private static final String TAG = "CategoryListActivity";
    private final int REQUEST_CODE = 101;
    @BindView(R.id.rvCategoryList)
    RecyclerView rvCategoryList;

    @BindView(R.id.rvCategoryGrid)
    RecyclerView rvCategoryGrid;

    @BindView(R.id.llCategory)
    LinearLayout llCategory;

    @BindView(R.id.llEmpty)
    LinearLayout llEmpty;

    @BindView(R.id.tvEmptyTitle)
    TextViewBold tvEmptyTitle;

    @BindView(R.id.tvEmptyDesc)
    TextViewLight tvEmptyDesc;

    @BindView(R.id.progress_wheel)
    ProgressWheel progress_wheel;

    @BindView(R.id.llProgress)
    LinearLayout llProgress;

    @BindView(R.id.tvContinueShopping)
    TextViewRegular tvContinueShopping;

    @BindView(R.id.tvSort)
    TextViewRegular tvSort;

    @BindView(R.id.tvFilter)
    TextViewRegular tvFilter;

    @BindView(R.id.rvSort)
    RecyclerView rvSort;

    @BindView(R.id.ivListOrGrid)
    ImageView ivListOrGrid;

    @BindView(R.id.ivGrid)
    ImageView ivGrid;

    @BindView(R.id.bottom_sheet)
    NestedScrollView bottom_sheet;

    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    List<CategoryList> categoryLists = new ArrayList<>();
    Boolean setNoItemFound = false;
    private CategoryGridAdapter categoryGridAdapter;
    private CategoryListAdapter categoryListAdapter;
    private SortAdapter sortAdapter;
    private boolean isGrid = true;
    private Bundle bundle;
    private String categoryId, sortBy;
    private int page = 1;
    private int sortPosition;
    private String search;
    private BottomSheetBehavior mBottomSheetBehavior;
    private boolean loading = true;
    private boolean isDealOfDayFound = false;
    private boolean isRecentlyAddedFound = false;
    private boolean isSelectedProductFound = false;
    private String productIds;
    private Boolean feature = false;
    String customerId;
    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);
        setScreenLayoutDirection();
        setToolbarTheme();
        setEmptyColor();
        settvImage();
        showSearch();
        showCart();
        setThemeColor();
        showBackButton();
        getIntentData();
        FilterSelectedList.filterJson = "";
        getCategoryListData(sortBy + "", true);
        setGridRecycleView();
        setListRecycleView();
        setbottomseet();
        setSortAdater();
        llProgress.setVisibility(View.GONE);
        setBottomBar("list", null);
        tvEmptyDesc.setText(R.string.simply_browse_item);
    }

    private void setThemeColor() {
        ivListOrGrid.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        ivGrid.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvSort.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvFilter.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvFilter.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));
            tvSort.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        }


        Drawable unwrappedDrawable = tvContinueShopping.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));


    }

    public void getIntentData() {
        customerId = getPreferences().getString(RequestParamUtils.ID, "");

        bundle = getIntent().getExtras();
        if (bundle != null) {
            categoryId = bundle.getString(RequestParamUtils.CATEGORY);
            sortBy = bundle.getString(RequestParamUtils.ORDER_BY);
            sortPosition = bundle.getInt(RequestParamUtils.POSITION);
            search = bundle.getString(RequestParamUtils.SEARCH);
            search = bundle.getString(RequestParamUtils.SEARCH);
            feature = bundle.getBoolean(RequestParamUtils.FEATURE);
            if (bundle.getString(RequestParamUtils.DEAL_OF_DAY) != null) {
                isDealOfDayFound = true;
                productIds = bundle.getString(RequestParamUtils.DEAL_OF_DAY);
            } else {
                isDealOfDayFound = false;
            }
           /* if (bundle.getString(RequestParamUtils.SLECTED_PRODUCT) != null) {
                isSelectedProductFound = true;
                productIds = bundle.getString(RequestParamUtils.SLECTED_PRODUCT);
            } else {
                isSelectedProductFound = false;
            }*/
        }
    }

    public void getCategoryListData(String sortType, boolean isDialogShow) {
        if (Utils.isInternetConnected(this)) {
            if (isDialogShow) {
                // showProgress("");
                if (Config.SHIMMER_VIEW) {
                    mShimmerViewContainer.startShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.VISIBLE);
                } else {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    showProgress("");
                }
            }

            PostApi postApi = new PostApi(CategoryListActivity.this, RequestParamUtils.getCategoryListData, this, getlanuage());
            try {
                JSONObject jsonObject;
                if (FilterSelectedList.filterJson.equals("")) {
                    jsonObject = new JSONObject();
                } else {
                    jsonObject = new JSONObject(FilterSelectedList.filterJson);
                }
                if (isDealOfDayFound) {
                    jsonObject.put(RequestParamUtils.INCLUDE, productIds);
                }
               /* if (isSelectedProductFound) {
                    jsonObject.put(RequestParamUtils.FEATURE, isSelectedProductFound);
                }*/
                if (feature) {
                    jsonObject.put(RequestParamUtils.FEATURE, feature);
                }
                jsonObject.put(RequestParamUtils.CATEGORY, categoryId);
                jsonObject.put(RequestParamUtils.USER_ID, customerId);
                jsonObject.put(RequestParamUtils.PAGE, page);
                jsonObject.put(RequestParamUtils.ORDER_BY, sortType);
                jsonObject.put(RequestParamUtils.SEARCH, search);

                postApi.callPostApi(new URLS().PRODUCT_URL + getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    public void setbottomseet() {

        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        //By default set BottomSheet Behavior as Collapsed and Height 0
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setPeekHeight(0);
        //If you want to handle callback of Sheet Behavior you can use below code
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("Bottom Sheet", "State Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d("Bottom Sheet", "State Dragging");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d("Bottom Sheet", "State Expanded");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d("Bottom Sheet", "State Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d("Bottom Sheet", "State Settling");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void setGridRecycleView() {
        ivListOrGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
        categoryGridAdapter = new CategoryGridAdapter(this);
        rvCategoryGrid.getItemAnimator().setChangeDuration(700);

        mLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        rvCategoryGrid.setLayoutManager(mLayoutManager);
        categoryGridAdapter.setHasStableIds(true);
        rvCategoryGrid.setAdapter(categoryGridAdapter);
        rvCategoryGrid.setNestedScrollingEnabled(false);
        rvCategoryGrid.setHasFixedSize(true);
        rvCategoryGrid.setItemViewCacheSize(20);
        rvCategoryGrid.setDrawingCacheEnabled(true);
        rvCategoryGrid.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        rvCategoryGrid.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        rvCategoryGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (setNoItemFound != true) {
                                loading = false;
                                page = page + 1;
                                Log.e("End ", "Last Item Wow  and page no:- " + page);
                                llProgress.setVisibility(View.VISIBLE);
                                progress_wheel.setBarColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                                getCategoryListData(Constant.getSortList(CategoryListActivity.this).get(sortAdapter.getSelectedPosition()).getSyntext(), false);
                                //Do pagination.. i.e. fetch new data
                            }
                        }
                    }
                }
            }
        });
    }

    public void setListRecycleView() {
        categoryListAdapter = new CategoryListAdapter(this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCategoryList.setLayoutManager(mLayoutManager);
        rvCategoryList.setAdapter(categoryListAdapter);
        rvCategoryList.setNestedScrollingEnabled(false);
        rvCategoryList.addItemDecoration(new EqualSpacingItemDecoration(dpToPx(10), EqualSpacingItemDecoration.VERTICAL)); // 16px. In practice, you'll want to use getDimensionPixelSize

        rvCategoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (setNoItemFound != true) {
                                loading = false;
                                page = page + 1;
                                Log.e("End ", "Last Item Wow  and page no:- " + page);
                                llProgress.setVisibility(View.VISIBLE);
                                progress_wheel.setBarColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                                getCategoryListData(Constant.getSortList(CategoryListActivity.this).get(sortAdapter.getSelectedPosition()).getSyntext(), false);
                                //Do pagination.. i.e. fetch new data
                            }
                        }
                    }
                }
            }
        });
    }

    public void setSortAdater() {
        List<String> sortList = new ArrayList<>();
        sortAdapter = new SortAdapter(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSort.setLayoutManager(mLayoutManager);
        rvSort.setAdapter(sortAdapter);
        rvSort.setNestedScrollingEnabled(false);
        for (int i = 0; i < Constant.getSortList(this).size(); i++) {
            sortList.add(Constant.getSortList(this).get(i).getName());
        }
        sortAdapter.addAll(sortList);
        sortAdapter.setSelectedPosition(sortPosition);
    }

    @OnClick(R.id.tvSort)
    public void tvSortClick() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
            //If state is in collapse mode expand it
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        else
            //else if state is expanded collapse it
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.ivGrid)
    public void ivGridClick() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvCategoryGrid.setVisibility(View.VISIBLE);
                rvCategoryList.setVisibility(View.GONE);
                mLayoutManager.setSpanCount(2);
                categoryGridAdapter.notifyDataSetChanged();
            }
        });
        isGrid = true;

    }

    @OnClick(R.id.ivListOrGrid)
    public void ivListOrGridClick() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvCategoryGrid.setVisibility(View.GONE);
                rvCategoryList.setVisibility(View.VISIBLE);
                categoryListAdapter.notifyDataSetChanged();
            }
        });

        isGrid = false;


    }

    @OnClick(R.id.tvDone)
    public void tvDoneClick() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        page = 1;
        categoryListAdapter.newList();
        categoryGridAdapter.newList();
        getCategoryListData(Constant.getSortList(this).get(sortAdapter.getSelectedPosition()).getSyntext(), true);
    }

    @OnClick(R.id.tvCancel)
    public void tvCancelClick() {

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.llFilter)
    public void llFilterClick() {

        if (categoryId == null || categoryId.equals("")) {
            Intent intent = new Intent(CategoryListActivity.this, SearchCategoryListActivity.class);
            intent.putExtra(RequestParamUtils.from, RequestParamUtils.filter);
            intent.putExtra(RequestParamUtils.SEARCH, search);
            intent.putExtra(RequestParamUtils.ORDER_BY, Constant.getSortList(CategoryListActivity.this).get(sortAdapter.getSelectedPosition()).getSyntext());
            intent.putExtra(RequestParamUtils.POSITION, sortAdapter.getSelectedPosition());
            startActivityForResult(intent, REQUEST_CODE);

        } else {
            Intent intent = new Intent(CategoryListActivity.this, FilterActivity.class);
            intent.putExtra(RequestParamUtils.CATEGORY, categoryId);
            intent.putExtra(RequestParamUtils.onSale, isDealOfDayFound);
            startActivity(intent);

        }

    }

    @Override
    public void onResponse(String response, String methodName) {

        if (methodName.equals(RequestParamUtils.getCategoryListData)) {
            if (response != null && response.length() > 0) {
                try {
                    
                    JSONArray jsonArray = new JSONArray(response);
                    categoryLists = new ArrayList<>();
                    if (loading || FilterSelectedList.isFilterCalled) {
                        FilterSelectedList.isFilterCalled = false;

                    }
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String jsonResponse = jsonArray.get(i).toString();
                            CategoryList categoryListRider = new Gson().fromJson(
                                    jsonResponse, new TypeToken<CategoryList>() {
                                    }.getType());
                            categoryLists.add(categoryListRider);

                            Log.e(TAG, "onResponse: Harsh Dynamic "+ new Gson().toJson(categoryLists.get(i).dynamicPrice));
                        }

                    } catch (Exception e) {
                        Log.e("Exception ==> ", e.getMessage());
                    }
                    llCategory.setVisibility(View.VISIBLE);
                    llEmpty.setVisibility(View.GONE);
                    categoryGridAdapter.addAll(categoryLists);
                    categoryListAdapter.addAll(categoryLists);
                    //   dismissProgress();
                    loading = true;

                    if (Config.SHIMMER_VIEW) {
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    } else {
                        dismissProgress();
                    }
                } catch (Exception e) {
                    //dismissProgress();
                    if (Config.SHIMMER_VIEW) {
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    } else {
                        dismissProgress();
                    }

                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("message").equals(getString(R.string.no_product_found))) {
                            setNoItemFound = true;
                            if (rvCategoryList.getAdapter().getItemCount() == 0 || rvCategoryGrid.getAdapter().getItemCount() == 0) {
                                llCategory.setVisibility(View.GONE);
                                llEmpty.setVisibility(View.VISIBLE);
                                tvEmptyTitle.setText(getString(R.string.no_product_found));
                                tvContinueShopping.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finish();
                                    }
                                });
                            }
                        }
                    } catch (JSONException e1) {
                        Log.e("noproductJSONException", e1.getMessage());
                    }
                    if (loading || FilterSelectedList.isFilterCalled) {
                        categoryLists = new ArrayList<>();
                        FilterSelectedList.isFilterCalled = false;
                        llCategory.setVisibility(View.GONE);
                        llEmpty.setVisibility(View.VISIBLE);
                        tvEmptyTitle.setText(getString(R.string.no_product_found));
                        tvContinueShopping.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                    }
                }
            } else {
                llEmpty.setVisibility(View.VISIBLE);
                tvEmptyTitle.setText(getString(R.string.no_product_found));
                tvContinueShopping.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
            llProgress.setVisibility(View.GONE);
        } else if (methodName.equals(RequestParamUtils.removeWishList) || methodName.equals(RequestParamUtils.addWishList)) {
            dismissProgress();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(this + "OnRestart: IsFilter" + FilterSelectedList.isFilterCalled, "Json : " + FilterSelectedList.filterJson);

        categoryGridAdapter.notifyDataSetChanged();
        categoryListAdapter.notifyDataSetChanged();
        if (FilterSelectedList.isFilterCalled) {
            page = 1;
            setNoItemFound = false;
            loading = true;
            categoryListAdapter.newList();
            categoryGridAdapter.newList();
            getCategoryListData(Constant.getSortList(CategoryListActivity.this).get(sortAdapter.getSelectedPosition()).getSyntext(), true);
        }
        showCart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                finish();
                startActivity(data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearFilter();
    }

    public void clearFilter() {
        for (int i = 0; i < FilterSelectedList.selectedOtherOptionList.size(); i++) {
            FilterOtherOption filterOtherOption = FilterSelectedList.selectedOtherOptionList.get(i);
            List<String> option = filterOtherOption.options;
            for (int j = 0; j < option.size(); j++) {
                option.set(j, "");
            }
        }
        if (FilterSelectedList.selectedColorOptionList.size() > 0) {
            for (int k = 0; k < FilterSelectedList.selectedColorOptionList.get(0).options.size(); k++) {
                FilterSelectedList.selectedColorOptionList.get(0).options.set(k, "");
            }
        }
        FilterSelectedList.isFilterCalled = false;
        FilterActivity.clearFilter = true;
    }
}
