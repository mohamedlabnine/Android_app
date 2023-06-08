package com.example.product


import android.widget.TextView
import com.android.volley.RequestQueue
import com.example.product.LoadingDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.os.Bundle
import com.example.product.R
import com.android.volley.toolbox.Volley
import android.text.TextWatcher
import android.text.Editable
import com.example.product.DataConfig
import com.android.volley.toolbox.StringRequest
import android.widget.Toast
import android.content.Intent
import android.view.Menu
import com.example.product.MainActivity
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.android.volley.Response
import com.example.product.Markets_List
import org.json.JSONObject
import com.example.product.Login
import com.example.product.databinding.ActivityMarketsListBinding
import com.example.product.databinding.ActivityOrderProductBinding
import org.json.JSONException
import java.util.HashMap

class Order_Product : AppCompatActivity() {
    lateinit var binding: ActivityOrderProductBinding
    var myQueue: RequestQueue? = null
    var loadingDialog: LoadingDialog? = null
    var valid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOrderProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myQueue = Volley.newRequestQueue(this)
        showProduct()
        binding.order.setOnClickListener(View.OnClickListener {
            if (valid) {
                validateOrder()
            }
        })
        binding.cardnumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                valid = if (charSequence.toString().length >= 16) {
                    binding.cardnumbercontainer.setHelperText("")
                    true
                } else {
                    binding.cardnumbercontainer.setHelperText("enter 16 digits")
                    false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    fun validateOrder() {
        loadingDialog = LoadingDialog(this)
        loadingDialog!!.startAlertDialog()
        val url = DataConfig.validateOrder_Api
        val request: StringRequest = object : StringRequest(Method.POST, url, Response.Listener {
            loadingDialog!!.dismissDialog()
            Toast.makeText(this@Order_Product, "you order is validated", Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }, Response.ErrorListener { }) {
            override fun getParams(): Map<String, String>? {
                // below line we are creating a map for
                // storing our values in key and value pair.
                val map: MutableMap<String, String> = HashMap()
                // on below line we are passing our key
                // and value pair to our parameters.
                map["user_id"] = java.lang.String.valueOf(DataConfig.user_id)
                map["user_email"] = DataConfig.user_email
                map["product_id"] = intent.getIntExtra("id", 0).toString()
                map["product_name"] = intent.getStringExtra("name")!!
                map["product_price"] = intent.getStringExtra("price")!!
                map["cardnumber"] = binding.cardnumber.text.toString()
                map["adress"] = "adress"
                return map
            }
        }
        myQueue!!.add(request)
    }

    fun showProduct() {
        Glide.with(binding.productimage.context).load(
            "https://yassirnasri.000webhostapp.com/products/" + intent.getStringExtra("image")
        ).into(
            binding.productimage
        )
        binding.productname.text = intent.getStringExtra("name")
        binding.productprice.text = intent.getStringExtra("price")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        var search=menu.findItem(R.id.search)
        var SearchView=search.actionView as SearchView
        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0.equals("")){
                    Toast.makeText(this@Order_Product,"Search is empty",Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(this@Order_Product, SearchActivity::class.java)
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
                        this@Order_Product,
                        respObj.getString("result"),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@Order_Product, Login::class.java)
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