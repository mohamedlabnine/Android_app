package com.example.product
import android.location.Location
object DataConfig {
    const val  getMarkets_Api = "https://mohamedandroid.000webhostapp.com/api/getMarkets"
    const val getMarket_Api = "https://mohamedandroid.000webhostapp.com/api/getMarket"
    const val getMarketProducts_Api = "https://mohamedandroid.000webhostapp.com/api/getMarketProducts"
    const val logout_Api = "https://mohamedandroid.000webhostapp.com/api/logout"
    const val getProducts_Api = "https://mohamedandroid.000webhostapp.com/api/getProducts"
    const val login_Api = "https://mohamedandroid.000webhostapp.com/api/login"
    const val register_Api = "https://mohamedandroid.000webhostapp.com/api/register"
    const val validateOrder_Api = "https://yassirnasri.000webhostapp.com/mail.php"
    var  location: Location?=null
    var token = ""
    var user_id = 0
    var user_email = ""
    var ProductsList:ArrayList<Product>?=null
}