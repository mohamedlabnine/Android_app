package com.example.product


import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import com.android.volley.RequestQueue
import com.example.product.LoadingDialog
import android.os.Bundle
import com.example.product.R
import com.android.volley.toolbox.Volley
import android.content.Intent
import android.view.View
import android.widget.Button
import com.example.product.Login
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.example.product.DataConfig
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import org.json.JSONException
import com.android.volley.VolleyError
import com.example.product.databinding.ActivityLoginBinding
import com.example.product.databinding.ActivitySignUpBinding
import java.util.HashMap

class Sign_Up : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    var name: String? = null
    var password: String? = null
    var email: String? = null
    var myQueue: RequestQueue? = null
    var loadingDialog: LoadingDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myQueue = Volley.newRequestQueue(this)
        loadingDialog = LoadingDialog(this)
        binding.loginText.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
        })
        binding.buttonSignUp?.setOnClickListener(View.OnClickListener {
            if (valid_data()) {
                register()
            } else {
                Toast.makeText(this@Sign_Up, "all fields are required", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun valid_data(): Boolean {
        name = binding.name.text.toString()
        password = binding.password.text.toString()
        email = binding.email.text.toString()
        return if (name != "" && password != "" && email != "") {
            true
        } else {
            false
        }
    }

    fun register() {
        loadingDialog!!.startAlertDialog()
        val url = DataConfig.register_Api
        val request: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
                loadingDialog!!.dismissDialog()
                try {
                    val respObj = JSONObject(response)
                    Toast.makeText(this@Sign_Up, respObj.getString("result"), Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(applicationContext, Login::class.java)
                    startActivity(intent)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { }) {
                override fun getParams(): Map<String, String>? {
                    // below line we are creating a map for
                    // storing our values in key and value pair.
                    val params: MutableMap<String, String> = HashMap()
                    // on below line we are passing our key
                    // and value pair to our parameters.
                    params["name"] = name!!
                    params["email"] = email!!
                    params["password"] = password!!
                    return params
                }
            }
        myQueue!!.add(request)
    }
}