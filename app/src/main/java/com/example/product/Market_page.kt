package com.example.product


import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.product.Login
import com.example.product.databinding.ActivityMarketPageBinding
import org.json.JSONException
import org.json.JSONObject

class Market_page : AppCompatActivity() {
    lateinit var binding: ActivityMarketPageBinding
    var myQueue: RequestQueue? = null
    var loadingDialog: LoadingDialog? = null
    var adapter: ProductAdapter? = null
    var data = ArrayList<Product>()
    val mFragmentManager = supportFragmentManager
    val mFragmentTransaction = mFragmentManager.beginTransaction()
    val mFragment = MarketFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMarketPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myQueue = Volley.newRequestQueue(this)
        adapter = ProductAdapter(applicationContext)
        binding.recyclerview2.setLayoutManager(GridLayoutManager(this, 2))
        adapter!!.setdata(data)
        binding.recyclerview2.setAdapter(adapter)
        val mBundle = Bundle()
        mBundle.putString("name", getIntent().getStringExtra("name"))
        mBundle.putString("description", getIntent().getStringExtra("description"))
        mBundle.putDouble("latitude", getIntent().getDoubleExtra("latitude",0.0))
        mBundle.putDouble("longitude", getIntent().getDoubleExtra("longitude",0.0))
        mBundle.putString("image", getIntent().getStringExtra("image"))
        mBundle.putFloat("distance", calculateDistance())
        mFragment.arguments = mBundle
        mFragmentTransaction.add(R.id.marketframelayout, mFragment).commit()

        getMarketProducts()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        var search=menu.findItem(R.id.search)
        var SearchView=search.actionView as SearchView
        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0.equals("")){
                    Toast.makeText(this@Market_page,"Search is empty",Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(this@Market_page, SearchActivity::class.java)
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



    fun calculateDistance(): Float {
        var startPoint = Location("A")
        startPoint.setLatitude(DataConfig.location!!.latitude)
        startPoint.setLongitude(DataConfig.location!!.longitude)
        val endPoint = Location("B")
        endPoint.setLatitude(getIntent().getDoubleExtra("latitude",0.0))
        endPoint.setLongitude(getIntent().getDoubleExtra("longitude",0.0))
        val distance: Float = startPoint.distanceTo(endPoint)
        return distance/1000

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
                        this@Market_page,
                        respObj.getString("result"),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@Market_page, Login::class.java)
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


    fun getMarketProducts(){
            val url = DataConfig.getMarketProducts_Api + "/" + intent.getIntExtra("id", 0)
            val request: StringRequest =
                object : StringRequest(Method.GET, url, Response.Listener { response ->
                    try {
                        val respObj = JSONObject(response)
                        if (respObj.getString("result") == "success") {
                            val Products = respObj.getJSONArray("data")
                            try {
                                for (i in 0 until Products.length()) {
                                    data.add(
                                        Product(
                                            Products.getJSONObject(i).getInt("id"),
                                            Products.getJSONObject(i).getString("name"),
                                            Products.getJSONObject(i).getString("price"),
                                            Products.getJSONObject(i).getString("use_case"),
                                            Products.getJSONObject(i).getString("component"),
                                            Products.getJSONObject(i).getString("caution"),
                                            Products.getJSONObject(i).getString("description"),
                                            Products.getJSONObject(i).getInt("id_market"),
                                            Products.getJSONObject(i).getString("image")
                                        )
                                    )
                                    adapter!!.notifyDataSetChanged()
                                }
                            } catch (e: Exception) {
                            }
                        } else {
                            Toast.makeText(
                                this@Market_page,
                                respObj.getString("result"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
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