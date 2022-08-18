package com.radugunn.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.radugunn.app.R;
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
    OnItemClickListner itemClickListner;
    public List<CategoryList.DynamicPrice> categoryList = new ArrayList<>();

    int selectedPosition = 0;

    public DynamicPriceAdapter(Context activity, OnItemClickListner itemClickListner) {
        this.context = activity;
        this.itemClickListner = itemClickListner;
    }

    public void addAll(List<CategoryList.DynamicPrice> list) {
        this.categoryList = list;
    }

    public void updateSelection(int dynamicQuantityPos) {
        selectedPosition = dynamicQuantityPos;
        notifyDataSetChanged();
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
            String minQuantity = categoryList.get(pos).minQuantity;
            String maxQuantity = categoryList.get(pos).maxQuantity;
            holder.tvDiscountQuantity.setText(minQuantity + " - " + maxQuantity);
            holder.tvDiscountPrice.setText(
                    Constant.CURRENCYSYMBOL + Utils.calculatedPrice(
                            Constant.regularPrice,
                            categoryList.get(pos).discountAmount));

            holder.tvDynamicDiscount.setText(categoryList.get(pos).discountAmount + "%");

            holder.llMainDynamicPrice.setOnClickListener(v -> {
                Constant.calculatedPrice = Utils.calculatedPrice(
                        Constant.regularPrice,
                        categoryList.get(pos).discountAmount);
                itemClickListner.onItemClick(pos, String.valueOf(pos), Constant.dynamicOuterPosition);
                selectedPosition = pos;
                notifyDataSetChanged();
            });

            changeLayout(holder, selectedPosition, pos);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvDiscountQuantity)
        TextView tvDiscountQuantity;

        @BindView(R.id.llDynamicRoot)
        LinearLayout llDynamicRoot;

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

        int selectedColor = Color.parseColor(((BaseActivity) context).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR));
        int unSelectedColor = context.getResources().getColor(R.color.black);

//        Drawable drawable = view.llDynamicRoot.getBackground();

        if (selectedPosition == position) {
            view.tvDiscountQuantity.setTextColor(selectedColor);
            view.tvDiscountPrice.setTextColor(selectedColor);
            view.tvDynamicDiscount.setTextColor(selectedColor);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                drawable.setTint(selectedColor);
//                view.llDynamicRoot.setBackgroundDrawable(drawable);
//            }
//            view.llDynamicRoot.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            view.tvDiscountQuantity.setTextColor(unSelectedColor);
            view.tvDiscountPrice.setTextColor(unSelectedColor);
            view.tvDynamicDiscount.setTextColor(unSelectedColor);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                drawable.setTint(unSelectedColor);
//                view.llDynamicRoot.setBackgroundDrawable(drawable);
//            }
//            view.llDynamicRoot.getBackground().setColorFilter(unSelectedColor, PorterDuff.Mode.SRC_ATOP);
        }
    }

}
