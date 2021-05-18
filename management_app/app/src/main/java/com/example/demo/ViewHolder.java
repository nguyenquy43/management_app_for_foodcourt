package com.example.demo;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    public TextView _name, _price;
    public ImageView _image;
    public Button _detail;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        _name = itemView.findViewById(R.id.foodName);
        _price = itemView.findViewById(R.id.foodPrice);
        _image = itemView.findViewById(R.id.foodView);
        _detail = itemView.findViewById(R.id.detailBut);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select action:");
        contextMenu.add(0,0, getAdapterPosition(), "Edit");
        contextMenu.add(0, 1, getAdapterPosition(), "Delete");
    }
}
