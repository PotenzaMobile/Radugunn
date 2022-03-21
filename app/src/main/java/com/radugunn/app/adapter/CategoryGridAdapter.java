package com.radugunn.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.radugunn.app.R;
import com.radugunn.app.activity.ProductDetailActivity;
import com.radugunn.app.customview.MaterialRatingBar;
import com.radugunn.app.customview.like.animation.SparkButton;
import com.radugunn.app.customview.roundedimageview.RoundedTransformationBuilder;
import com.radugunn.app.customview.textview.TextViewRegular;
import com.radugunn.app.javaclasses.AddToCartVariation;
import com.radugunn.app.javaclasses.AddToWishList;
import com.radugunn.app.model.CategoryList;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.Constant;
import com.radugunn.app.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.CategoryGridHolder> {

    private static final String TAG = "CategoryGridAdapter";
    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    //    Transformation transformation;
    private final Transformation mTransformation;

    public CategoryGridAdapter(Activity activity) {
        this.activity = activity;
//        transformation = new RoundedTransformationBuilder()
//                .cornerRadiusDp(((BaseActivity)activity).dpToPx(5))
//                .oval(false)
//                .build(activity);

        mTransformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
    }

    public void addAll(List<CategoryList> list) {
        for (CategoryList item : list) {
            add(item);
        }
//        this.list = list;
//        notifyDataSetChanged();
    }

    public void add(CategoryList item) {
        this.list.add(item);
        if (list.size() > 1) {
            notifyItemInserted(list.size() - 1);
        } else {
            notifyDataSetChanged();
        }
    }

    public void newList() {
        this.list = new ArrayList<>();
    }

    @Override
    public CategoryGridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_category, parent, false);

        return new CategoryGridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryGridHolder holder, final int position) {
        holder.llSale.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClickProduct(position);

            }
        });

        Log.e(TAG, "Harsh: CategoryListResponse "+ new Gson().toJson(list.get(position).stockQuantity) );
        if (list.get(position).inStock) {
            Log.e(TAG, "Harsh: Quantity: Set Data: "+list.get(position).stockQuantity );
            holder.tvStock.setText(String.valueOf(list.get(position).stockQuantity));
            holder.tvAvailibility.setText(R.string.in_stock);
            holder.tvStock.setTextColor(activity.getResources().getColor(R.color.green));
            holder.tvAvailibility.setTextColor(activity.getResources().getColor(R.color.green));
        } else {
            holder.tvStock.setVisibility(View.GONE);
            holder.tvAvailibility.setText(R.string.out_of_stock);
            holder.tvAvailibility.setTextColor(Color.RED);
        }
        //Add product in cart if add to cart enable from admin panel
        new AddToCartVariation(activity).addToCart(holder.ivAddToCart, new Gson().toJson(list.get(position)));
//
        //Add product in wishlist and remove product from wishlist and check wishlist enable or not
        new AddToWishList(activity).addToWishList(holder.ivWishList, new Gson().toJson(list.get(position)), holder.tvPrice1);

        if (!list.get(position).averageRating.equals("")) {
            holder.ratingBar.setRating(Float.parseFloat(list.get(position).averageRating));
        } else {
            holder.ratingBar.setRating(0);
        }
        if (list.get(position).appthumbnail != null) {

//            holder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            Glide.with(activity)
//                    .load(list.get(position).appthumbnail)
//                    .asBitmap().format(DecodeFormat.PREFER_ARGB_8888)
//                    .error(R.drawable.placeholder)
//                    .transform(transformation)
//                    .placeholder(R.drawable.placeholder)
//                    .into(holder.ivImage);

            holder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(list.get(position).appthumbnail)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.no_image_available)
                    .fit()
                    .transform(mTransformation)
                    .into(holder.ivImage);

        } else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvName.setText(Html.fromHtml(list.get(position).name, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvName.setText(Html.fromHtml(list.get(position).name));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml));
        }
        holder.tvPrice.setTextSize(15);
        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);


        holder.ll_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClickProduct(position);
            }
        });

        ViewTreeObserver vto = holder.ivImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                holder.ivImage.getViewTreeObserver().removeOnPreDrawListener(this);
//                Log.e("Height: " + holder.ivImage.getMeasuredHeight(), " Width: " + holder.ivImage.getMeasuredWidth());
                return true;
            }
        });

        if (!list.get(position).type.contains(RequestParamUtils.variable) && list.get(position).onSale == true) {
            ((BaseActivity) activity).showDiscount(holder.tvDiscount, list.get(position).salePrice, list.get(position).regularPrice);
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewRecycled(CategoryGridHolder holder) {
        super.onViewRecycled(holder);
        Picasso.get()
                .cancelRequest(holder.ivImage);
    }

    public void ClickProduct(int position) {
        if (list.get(position).type.equals(RequestParamUtils.external)) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).externalUrl));
            activity.startActivity(browserIntent);
        } else {
            Constant.CATEGORYDETAIL = list.get(position);
            Intent intent = new Intent(activity, ProductDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(RequestParamUtils.ProductID, String.valueOf(list.get(position).id));
            intent.putExtras(bundle);
            activity.startActivity(intent);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {

        return list.get(position).id;
//        return super.getItemId(position);
    }

    public class CategoryGridHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.ll_content)
        LinearLayout ll_content;

        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.ivWishList)
        SparkButton ivWishList;

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.tvDiscount)
        TextViewRegular tvDiscount;

        @BindView(R.id.llSale)
        FrameLayout llSale;

        @BindView(R.id.ivAddToCart)
        ImageView ivAddToCart;

        @BindView(R.id.main)
        LinearLayout main;

        @BindView(R.id.tvAvailibility)
        TextView tvAvailibility;

        @BindView(R.id.tvStock)
        TextView tvStock;

        @BindView(R.id.tvAvailibilitytext)
        TextView tvAvailibilitytext;

        public CategoryGridHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
