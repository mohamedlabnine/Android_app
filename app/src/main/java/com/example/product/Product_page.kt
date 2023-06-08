package com.example.product

import androidx.cardview.widget.CardView
import com.android.volley.RequestQueue
import com.example.product.LoadingDialog
import android.os.Bundle
import com.example.product.R
import com.android.volley.toolbox.Volley
import android.content.Intent
import android.view.Menu
import com.example.product.Order_Product
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.product.Markets_List
import com.example.product.DataConfig
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.android.volley.Response
import com.example.product.Login
import org.json.JSONException
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.example.product.Market_page
import com.example.product.databinding.ActivityMainBinding
import com.example.product.databinding.ActivityProductPageBinding
import java.util.HashMap

class Product_page : AppCompatActivity() {
    lateinit var binding: ActivityProductPageBinding
    var myQueue: RequestQueue? = null
    var loadingDialog: LoadingDialog? = null
    val mFragmentManager = supportFragmentManager
    val mFragmentTransaction = mFragmentManager.beginTransaction()
    val mFragment = ProductFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProductPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myQueue = Volley.newRequestQueue(this)
        val mBundle = Bundle()
        mBundle.putString("name", getIntent().getStringExtra("name"))
        mBundle.putString("price", getIntent().getStringExtra("price"))
        mBundle.putString("use_case", getIntent().getStringExtra("use_case"))
        mBundle.putString("component", getIntent().getStringExtra("component"))
        mBundle.putString("caution", getIntent().getStringExtra("caution"))
        mBundle.putString("description", getIntent().getStringExtra("description"))
        mBundle.putInt("id_market", getIntent().getIntExtra("id_market", 0))
        mBundle.putString("image", getIntent().getStringExtra("image"))
        mBundle.putInt("id", getIntent().getIntExtra("id", 0))
        mFragment.arguments = mBundle
        mFragmentTransaction.add(R.id.productframeLayout, mFragment).commit()

        getMarket()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        var search=menu.findItem(R.id.search)
        var SearchView=search.actionView as SearchView
        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0.equals("")){
                    Toast.makeText(this@Product_page,"Search is empty",Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(this@Product_page, SearchActivity::class.java)
                    intent.putExtra("search", p0)
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true

            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logout()
                true
            }
            R.id.market -> {
                val intent = Intent(applicationContext, Markets_List::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun logout() {
        loadingDialog = LoadingDialog(this)
        loadingDialog!!.startAlertDialog()
        val url = DataConfig.logout_Api
        val request: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
                loadingDialog!!.dismissDialog()
                try {
                    val respObj = JSONObject(response)
                    Toast.makeText(
                        this@Product_page,
                        respObj.getString("result"),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@Product_page, Login::class.java)
                    startActivity(intent)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error -> error.printStackTrace() }) {
                override fun getHeaders(): Map<String, String> {
                    val map: MutableMap<String, String> = HashMap()
                    map["Content-Type"] = "application/json"
                    map["Authorization"] = "Bearer " + DataConfig.token
                    return map
                }

                override fun getParams(): Map<String, String>? {
                    return HashMap()
                }
            }
        myQueue!!.add(request)
    }






        fun getMarket() {
            val url = DataConfig.getMarket_Api + "/" + intent.getIntExtra("id_market", 0)
            val request: StringRequest =
                object : StringRequest(Method.GET, url, Response.Listener { response ->
                    try {
                        val respObj = JSONObject(response)
                        if (respObj.getString("result") == "success") {
                            val Market = respObj.getJSONObject("data")
                            Glide.with(binding.marketimage.context).load(
                                "https://yassirnasri.000webhostapp.com/markets/" + Market.getString(
                                    "image"
                                )
                            ).into(
                                binding.marketimage
                            )
                            binding.marketname.text = Market.getString("name")
                            binding.marketpage.setOnClickListener {
                                val intent = Intent(applicationContext, Market_page::class.java)
                                try {
                                    intent.putExtra("name", Market.getString("name"))
                                    intent.putExtra("location", Market.getString("location"))
                                    intent.putExtra("latitude", Market.getDouble("latitude"))
                                    intent.putExtra("longitude", Market.getDouble("longitude"))
                                    intent.putExtra("description", Market.getString("description"))
                                    intent.putExtra("id", Market.getInt("id"))
                                    intent.putExtra("image", Market.getString("image"))
                                    startActivity(intent)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@Product_page,
                                respObj.getString("result"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error -> error.printStackTrace() }) {
                    override fun getHeaders(): Map<String, String> {
                        val map: MutableMap<String, String> = HashMap()
                        map["Content-Type"] = "application/json"
                        map["Authorization"] = "Bearer " + DataConfig.token
                        return map
                    }

                    override fun getParams(): Map<String, String>? {
                        return HashMap()
                    }
                }
            myQueue!!.add(request)
        }
}