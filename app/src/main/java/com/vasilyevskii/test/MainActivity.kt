package com.vasilyevskii.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.vasilyevskii.test.adapter.RecycleViewAdapter
import com.vasilyevskii.test.api.PixliService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {


    private val pixliService: PixliService
        get() = (application as App).pixliService


    private val compositeDisposable = CompositeDisposable()
    private val recycleViewAdapter = RecycleViewAdapter()



    private lateinit var recyclerView: RecyclerView
    private lateinit var startProgressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.list_data)
        recyclerView.adapter = recycleViewAdapter

        startProgressBar = findViewById(R.id.start_progressbar)

        loadData()
    }

    private fun loadData(){
        compositeDisposable.add(pixliService.getPixliApi().getDataList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                recycleViewAdapter.dataDTO = it
                visibleRecycleView()
            },{
                Toast.makeText(this@MainActivity, "Data loading error", Toast.LENGTH_SHORT).show();
            }))
    }

    private fun visibleRecycleView(){
        startProgressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }




}