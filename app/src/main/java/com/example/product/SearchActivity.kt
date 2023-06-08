package com.example.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.product.databinding.ActivityMainBinding
import com.example.product.databinding.ActivitySearchBinding
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    var data = ArrayList<Product>()
    var adapter: ProductAdapter? = null
    var loadingDialog: LoadingDialog? = null
    var myQueue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySearchBinding.inflate(layoutInflater)
        myQueue = Volley.newRequestQueue(this)
        setContentView(binding.root)
        binding.recyclerview3.setLayoutManager(GridLayoutManager(this, 2))
        adapter = ProductAdapter(applicationContext)
        getsearch()
    }
    fun getsearch() {
        var p:Product
        val search = intent.getStringExtra("search")
        for(p in DataConfig.ProductsList!! ){
            if(p.name.contains(search.toString(),ignoreCase = true)){
                data.add(p)
            }
        }
        adapter!!.setdata(data)
        binding.recyclerview3.setAdapter(adapter)
        }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        var search=menu.findItem(R.id.search)
        var SearchView=search.actionView as SearchView
        SearchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0.equals("")){
                    Toast.makeText(this@SearchActivity,"Search is empty",Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(this@SearchActivity, SearchActivity::class.java)
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
                        this@SearchActivity,
                        respObj.getString("result"),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@SearchActivity, Login::class.java)
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
}