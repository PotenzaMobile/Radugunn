package com.radugunn.app.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.radugunn.app.R;
import com.radugunn.app.adapter.SearchInnerCategoryAdapter;
import com.radugunn.app.interfaces.OnItemClickListner;
import com.radugunn.app.model.Home;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.Constant;
import com.radugunn.app.utils.RequestParamUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchCategoryInnerListActivity extends BaseActivity implements OnItemClickListner {

    @BindView(R.id.rvSearchCategory)
    RecyclerView rvSearchCategory;
    private List<Home.AllCategory> list = new ArrayList<>();
    private Map<Integer, List<Home.AllCategory>> childList = new HashMap<>();
    private int cat_id;
    private Bundle bundle;
    private SearchInnerCategoryAdapter searchInnerCategoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_category_inner_list);
        ButterKnife.bind(this);
        setToolbarTheme();
        setScreenLayoutDirection();
        settvTitle(getResources().getString(R.string.all_category));
        getIntentData();
        getList(cat_id);
        if (list.size() == 0) {
            finish();
            Intent intent = new Intent(this, CategoryListActivity.class);
            intent.putExtra(RequestParamUtils.CATEGORY, cat_id + "");
            intent.putExtra(RequestParamUtils.ORDER_BY, SearchCategoryListActivity.sortBy);
            intent.putExtra(RequestParamUtils.POSITION, SearchCategoryListActivity.sortPosition);
            startActivity(intent);
        }
        showSearch();
        showCart();
        showBackButton();
        setSerachAdapter();
    }


    public void getIntentData() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            cat_id = bundle.getInt(RequestParamUtils.CATEGORY);
        }
    }

    public void setSerachAdapter() {
        searchInnerCategoryAdapter = new SearchInnerCategoryAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSearchCategory.setLayoutManager(mLayoutManager);
        rvSearchCategory.setAdapter(searchInnerCategoryAdapter);
        rvSearchCategory.setNestedScrollingEnabled(false);
        searchInnerCategoryAdapter.addAll(list, childList);
    }

    @Override
    public void onItemClick(int position, String value, int outerPos) {

    }

    public void getList(int id) {
        for (int i = 0; i < Constant.MAINCATEGORYLIST.size(); i++) {
            if (Constant.MAINCATEGORYLIST.get(i).parent == id) {

                list.add(Constant.MAINCATEGORYLIST.get(i));
            }
        }

        for (int j = 0; j < list.size(); j++) {

            List<Home.AllCategory> tempList = new ArrayList<>();
            for (int k = 0; k < Constant.MAINCATEGORYLIST.size(); k++) {
                if (list.get(j).id.intValue() == Constant.MAINCATEGORYLIST.get(k).parent.intValue()) {
                    tempList.add(Constant.MAINCATEGORYLIST.get(k));
                }

                if (list.get(j).product_count.equals("0")){
                    list.remove(j);
                }
            }
            childList.put(list.get(j).id, tempList);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        showCart();
    }
}
