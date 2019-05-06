package com.emptyfruits.com.networkingandrecyclerview;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emptyfruits.com.networkingandrecyclerview.databinding.ListItemBinding;

import java.util.ArrayList;

public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.BooksViewHolder> {
    public static final String TAG = MyBooksAdapter.class.getName();
    private Context mContext;
    private ArrayList<Book> booksList;
    private ListItemBinding binding;

    public MyBooksAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setBooksList(ArrayList<Book> booksList) {
        this.booksList = booksList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View myView = LayoutInflater.from(mContext).inflate(R.layout.list_item, viewGroup,
                false);
        binding = DataBindingUtil.bind(myView);
        return new BooksViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksViewHolder myBooksViewHolder, int i) {
        if (binding == null) {
            Log.e(TAG, "onBindViewHolder: binding null");
            throw new AssertionError();
        }
        binding.setBook(booksList.get(i));
    }

    @Override
    public int getItemCount() {

        return booksList == null ? 0 : booksList.size();
    }

    class BooksViewHolder extends RecyclerView.ViewHolder {

        public BooksViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

