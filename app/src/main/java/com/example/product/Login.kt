package com.example.product


import android.annotation.SuppressLint
import android.content.Context
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import com.android.volley.RequestQueue
import com.example.product.LoadingDialog
import android.os.Bundle
import com.example.product.R
import com.android.volley.toolbox.Volley
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.view.View
import android.widget.Button
import com.example.product.Sign_Up
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.android.volley.Response
import com.example.product.DataConfig
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import com.example.product.MainActivity
import org.json.JSONException
import com.android.volley.VolleyError
import com.example.product.databinding.ActivityLoginBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.HashMap

class Login : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var binding:ActivityLoginBinding
    var location:Location?=null
    var state=false;
    var email: String? = null
    var password: String? = null
    var myQueue: RequestQueue? = null
   var loadingDialog: LoadingDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myQueue = Volley.newRequestQueue(this)
       loadingDialog = LoadingDialog(this)
        binding.signUpText?.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, Sign_Up::class.java)
            startActivity(intent)
            finish()
        })
        binding.buttonLogin?.setOnClickListener(View.OnClickListener {
            if(state) {
                if (valid_data()) {
                    login()
                } else {
                    Toast.makeText(this@Login, "all fields are required", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                getCurrentLocation()
                if(state) {
                    if (valid_data()) {
                        login()
                    } else {
                        Toast.makeText(this@Login, "all fields are required", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)

    }
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(){
        if(checkPermissions()){
            if(isLocationEnabled()){
                if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){task->
                   location=task.result
                    if(location==null){

                    }
                    else{
                        DataConfig.location=location
                        Toast.makeText(this,"Location is Set",Toast.LENGTH_LONG).show()
                        state=true


                    }
                }

            }
            else{
                    Toast.makeText(this,"Turn On Location",Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)

            }
        }
        else{
            requestPermission()

        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=100
    }
    fun checkPermissions():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ){
            return true
        }
        return false

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }
            else{
                
            }
        }
    }

    fun login() {
        loadingDialog!!.startAlertDialog()
        val url = DataConfig.login_Api
        val request: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
                loadingDialog!!.dismissDialog()
                try {
                    val respObj = JSONObject(response)
                    if (respObj.getString("result") == "success") {
                       //DataConfig.token = respObj.getString("token")
                        val User = respObj.getJSONObject("user")
                        DataConfig.user_id = User.getInt("id")
                        DataConfig.user_email = User.getString("email")
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Login, respObj.getString("result"), Toast.LENGTH_SHORT)
                            .show()
                    }
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
                    params["email"] = email!!
                    params["password"] = password!!

                    // at last we are
                    // returning our params.
                    return params
                }
            }
        myQueue!!.add(request)
    }

    fun valid_data(): Boolean {
        email = binding.email2.text.toString()
        password = binding.password2.text.toString()
        return if (email != "" && password != "") {
            true
        } else {
            false
        }
    }
}