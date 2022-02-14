package com.vasilyevskii.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vasilyevskii.test.adapter.RecycleViewAdapter
import com.vasilyevskii.test.api.PixliService
import io.reactivex.disposables.CompositeDisposable

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


    private val pixliService: PixliService
        get() = (application as App).pixliService

    private val picasso: Picasso
        get() = (application as App).picasso



    private lateinit var recyclerView: RecyclerView
    private val recycleViewAdapter = RecycleViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.list_data)
        recyclerView.adapter = recycleViewAdapter


        loadData()
    }

    private fun loadData(){
        GlobalScope.launch {

        }
    }


}