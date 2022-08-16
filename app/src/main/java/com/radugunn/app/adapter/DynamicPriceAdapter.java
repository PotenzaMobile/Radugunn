package com.radugunn.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.radugunn.app.R;
import com.radugunn.app.activity.ProductDetailActivity;
import com.radugunn.app.interfaces.OnItemClickListner;
import com.radugunn.app.model.CategoryList;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.Constant;
import com.radugunn.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Harsh Prajapati on 15-08-2022.
 */
public class DynamicPriceAdapter extends RecyclerView.Adapter<DynamicPriceAdapter.ViewHolder> {

    Context context;
    Activity activity;
    OnItemClickListner itemClickListner;
    public List<CategoryList.DynamicPrice> categoryList = new ArrayList<>();

    int selectedPosition = -1;

    /*private final CategoryList categoryLists ;*/

//    List<CategoryList> categoryLists;

    public DynamicPriceAdapter(Context activity, OnItemClickListner itemClickListner/*, CategoryList categoryList*/) {
        this.context = activity;
        this.itemClickListner = itemClickListner;
        /*categoryLists = categoryList;*/
    }

    public void addAll(List<CategoryList.DynamicPrice> list) {
        this.categoryList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discount_table, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();

        if (categoryList != null) {

            holder.tvDiscountQuantity.setText(categoryList.get(pos).minQuantity + " - " + categoryList.get(pos).maxQuantity);
            holder.tvDiscountPrice.setText(
                    Utils.calculatedPrice(
                            Constant.regularPrice,
                            categoryList.get(pos).discountAmount)
            ) ;
            holder.tvDynamicDiscount.setText(categoryList.get(pos).discountAmount + "%");

            holder.llMainDynamicPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListner.onItemClick(pos, String.valueOf(pos), Constant.dynamicOuterPosition);
                    //Toast.makeText(context, String.valueOf(pos), Toast.LENGTH_SHORT).show();

                    changeLayout(holder,selectedPosition,pos);
                }
            });



        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tvDiscountQuantity)
        TextView tvDiscountQuantity;

        @BindView(R.id.tvDiscountPrice)
        TextView tvDiscountPrice;

        @BindView(R.id.tvDynamicDiscount)
        TextView tvDynamicDiscount;

        @BindView(R.id.llMainDynamicPrice)
        LinearLayout llMainDynamicPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public void changeLayout(ViewHolder view, int selectedPosition, int position) {
        if (selectedPosition == position){
            view.tvDiscountQuantity.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            view.tvDiscountPrice.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            view.tvDynamicDiscount.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        }else{
            /*view.tvDiscountQuantity.setTextColor(Color.parseColor(String.valueOf(((BaseActivity) context).getResources().getColor(R.color.black))));
            view.tvDiscountPrice.setTextColor(Color.parseColor(String.valueOf(((BaseActivity) context).getResources().getColor(R.color.black))));
            view.tvDynamicDiscount.setTextColor(Color.parseColor(String.valueOf(((BaseActivity) context).getResources().getColor(R.color.black))));*/
        }
    }

}
