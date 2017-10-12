package com.chihun.learn.basicuidemo.recyclerview;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：wzd on 2017年03月28日 11:10
 * 邮箱：wangzhenduo@yunnex.com
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> views;
    public BaseViewHolder(View itemView) {
        super(itemView);
        this.views=new SparseArray<>();
        getViews(itemView);
    }

    private void getViews(View itemView) {
        if(itemView instanceof ViewGroup){
            ViewGroup layout = (ViewGroup) itemView;
            this.views.append(layout.getId(),layout);
            for(int i=0;i<layout.getChildCount();i++){
                View childView = layout.getChildAt(i);
                if(childView instanceof ViewGroup){
                    getViews(childView);
                } else {
                    this.views.append(childView.getId(),childView);
                }
            }
        } else {
            this.views.append(itemView.getId(),itemView);
        }
    }

    public View view(@IdRes int id){
        return this.views.get(id);
    }

    public void setOnClickListener(@IdRes int id, View.OnClickListener clickListener){
        this.views.get(id).setOnClickListener(clickListener);
    }
    public void setOnClickListener(View.OnClickListener clickListener){
        this.itemView.setOnClickListener(clickListener);
    }
}
