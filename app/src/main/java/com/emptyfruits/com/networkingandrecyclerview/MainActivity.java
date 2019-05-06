package com.emptyfruits.com.networkingandrecyclerview;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.emptyfruits.com.networkingandrecyclerview.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    public static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.noResults.setMinValue(2);
        mainBinding.noResults.setMaxValue(40);
    }

    public void passQuery(View view) {
        String query = mainBinding.searchEditText.getText().toString();
        int max_results = (mainBinding.noResults.getValue());
        if (max_results < 40 && !query.equals("")) {
            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("query", query);
            intent.putExtra("max_results", max_results);
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    (max_results > 40
                            ? "The maximum allowed value is 40!!" : "Please enter a query!!")
                    , Toast.LENGTH_SHORT).show();
        }
    }
}

