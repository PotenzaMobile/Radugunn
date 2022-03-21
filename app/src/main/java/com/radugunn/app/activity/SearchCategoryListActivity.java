package com.radugunn.app.activity;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.radugunn.app.R;
import com.radugunn.app.adapter.SearchCategoryAdapter;
import com.radugunn.app.customview.GridSpacingItemDecoration;
import com.radugunn.app.interfaces.OnItemClickListner;
import com.radugunn.app.model.Home;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.Constant;
import com.radugunn.app.utils.RequestParamUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchCategoryListActivity extends BaseActivity implements OnItemClickListner {

    @BindView(R.id.rvSearchCategory)
    RecyclerView rvSearchCategory;

    @BindView(R.id.svHome)
    NestedScrollView svHome;

    private       SearchCategoryAdapter  searchCategoryAdapter;
    private       Bundle                 bundle;
    private       String                 from;
    public static int                    sortPosition;
    private       List<Home.AllCategory> list = new ArrayList<>();
    public static String                 search, sortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_category_list);
        ButterKnife.bind(this);
        setToolbarTheme();
        setScreenLayoutDirection();
        settvTitle(getResources().getString(R.string.all_category));
        getIntentData();
        showSearch();
        showCart();
        showBackButton();
        setSerachAdapter();
        setBottomBar("search", svHome);

    }

    public void getIntentData() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            from = bundle.getString(RequestParamUtils.from);
            search = bundle.getString(RequestParamUtils.SEARCH);
            sortBy = bundle.getString(RequestParamUtils.ORDER_BY);
            sortPosition = bundle.getInt(RequestParamUtils.POSITION);
        }
    }

// intent.putExtra(RequestParamUtils.ORDER_BY, Constant.getSortList().get(sortAdapter.getSelectedPosition()).getSyntext());
//            intent.putExtra(RequestParamUtils.POSITION,sortPosition);

    public void setSerachAdapter() {
        searchCategoryAdapter = new SearchCategoryAdapter(this, this);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        rvSearchCategory.setLayoutManager(mLayoutManager);
        rvSearchCategory.setAdapter(searchCategoryAdapter);
        rvSearchCategory.setNestedScrollingEnabled(false);
        searchCategoryAdapter.setFrom(from);
        rvSearchCategory.setNestedScrollingEnabled(false);
        rvSearchCategory.setHasFixedSize(true);
        rvSearchCategory.setItemViewCacheSize(20);
        rvSearchCategory.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
        for (int i = 0; i < Constant.MAINCATEGORYLIST.size(); i++) {
            if (Constant.MAINCATEGORYLIST.get(i).parent == 0) {
                list.add(Constant.MAINCATEGORYLIST.get(i));
            }
        }
        searchCategoryAdapter.addAll(list);

    }

    @Override
    public void onItemClick(int position, String value, int outerPos) {

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        showCart();
    }
}
