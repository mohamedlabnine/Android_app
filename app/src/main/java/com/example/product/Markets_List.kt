package com.example.product

import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.example.product.Market
import com.example.product.MarketAdapter
import com.example.product.LoadingDialog
import android.os.Bundle
import com.example.product.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.Volley
import android.view.MenuInflater
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.example.product.Markets_List
import com.example.product.DataConfig
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.android.volley.Response
import com.example.product.Login
import org.json.JSONException
import com.android.volley.VolleyError
import com.example.product.databinding.ActivityMainBinding
import com.example.product.databinding.ActivityMarketsListBinding
import org.json.JSONArray
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

class Markets_List : AppCompatActivity() {
    lateinit var binding: ActivityMarketsListBinding
    var myQueue: RequestQueue? = null
    var data = ArrayList<Market>()
    var adapter: MarketAdapter? = null
    var loadingDialog: LoadingDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMarketsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.marketlistrecyclerview.setLayoutManager(LinearLayoutManager(this))
        myQueue = Volley.newRequestQueue(this)
        adapter = MarketAdapter(applicationContext)
        adapter!!.setdata(data)
        binding.marketlistrecyclerview.setAdapter(adapter)
        getMarkets()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        var search=menu.findItem(R.id.search)
        var SearchView=search.actionView as SearchView
        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0.equals("")){
                    Toast.makeText(this@Markets_List,"Search is empty",Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(this@Markets_List, SearchActivity::class.java)
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
                        this@Markets_List,
                        respObj.getString("result"),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@Markets_List, Login::class.java)
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

        fun getMarkets() {
            val url = DataConfig.getMarkets_Api
            val request: StringRequest =
                object : StringRequest(Method.GET, url, Response.Listener { response ->
                    try {
                        val respObj = JSONArray(response)
                        try {
                            for (i in 0 until response.length) {
                                data.add(
                                    Market(
                                        respObj.getJSONObject(i).getInt("id"),
                                        respObj.getJSONObject(i).getString("name"),
                                        respObj.getJSONObject(i).getString("description"),
                                        respObj.getJSONObject(i).getString("location"),
                                        respObj.getJSONObject(i).getDouble("latitude"),
                                        respObj.getJSONObject(i).getDouble("longitude")
                                            .toDouble(),
                                        respObj.getJSONObject(i).getString("image")
                                    )
                                )
                                adapter!!.notifyDataSetChanged()
                            }
                        } catch (e: Exception) {
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