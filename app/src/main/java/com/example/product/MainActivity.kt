package com.example.product


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.product.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

 class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    var myQueue: RequestQueue? = null
    var loadingDialog: LoadingDialog? = null
    var data = ArrayList<Product>()
    var adapter: ProductAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myQueue = Volley.newRequestQueue(this)
        binding.recyclerview.setLayoutManager(GridLayoutManager(this, 2))
        adapter = ProductAdapter(applicationContext)
        adapter!!.setdata(data)
        binding.recyclerview.setAdapter(adapter)
        getProducts()



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        var search=menu.findItem(R.id.search)
        var SearchView=search.actionView as SearchView
        SearchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0.equals("")){
                    Toast.makeText(this@MainActivity,"Search is empty",Toast.LENGTH_LONG).show()
                }
                else{
                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
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
                        this@MainActivity,
                        respObj.getString("result"),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@MainActivity, Login::class.java)
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


        fun getProducts() {
            val url = DataConfig.getProducts_Api
            val request: StringRequest =
                object : StringRequest(Method.GET, url, Response.Listener { response ->
                    try {
                        val respObj = JSONArray(response)
                        try {
                            for (i in 0 until response.length) {
                                data.add(
                                    Product(
                                        respObj.getJSONObject(i).getInt("id"),
                                        respObj.getJSONObject(i).getString("name"),
                                        respObj.getJSONObject(i).getString("price"),
                                        respObj.getJSONObject(i).getString("use_case"),
                                        respObj.getJSONObject(i).getString("component"),
                                        respObj.getJSONObject(i).getString("caution"),
                                        respObj.getJSONObject(i).getString("description"),
                                        respObj.getJSONObject(i).getInt("id_market"),
                                        respObj.getJSONObject(i).getString("image")
                                    )
                                )

                                adapter!!.notifyDataSetChanged()
                                DataConfig.ProductsList=data
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