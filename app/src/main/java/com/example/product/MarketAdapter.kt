package com.example.product

import androidx.recyclerview.widget.RecyclerView
import com.example.product.MarketAdapter.Marketviewholder
import com.example.product.Market
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.product.R
import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.example.product.Market_page
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.product.databinding.SingleItemBinding
import com.example.product.databinding.SinglemarketdesignBinding
import java.util.ArrayList

class MarketAdapter(var context: Context) : RecyclerView.Adapter<Marketviewholder>() {
    lateinit var binding : SinglemarketdesignBinding
    var data = ArrayList<Market>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Marketviewholder {
        var inflater=LayoutInflater.from(parent.context)
        binding=SinglemarketdesignBinding.inflate(inflater,parent,false)
        return Marketviewholder(binding.root)

    }

    override fun onBindViewHolder(
        holder: Marketviewholder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.bind(data[position],position)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setdata(array: ArrayList<Market>) {
        data = array
    }

    inner class Marketviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(x:Market,position:Int) {
            binding.apply {
                Glide.with(marketlistimage.context)
                    .load("https://yassirnasri.000webhostapp.com/markets/" + data[position].image)
                    .into(marketlistimage)
                marketlistname.text=x.name
                marketlistcardview.setOnClickListener {
                    val intent = Intent(context, Market_page::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("name", data[position].name)
                    intent.putExtra("location", data[position].location)
                    intent.putExtra("latitude", data[position].latitude)
                    intent.putExtra("longitude", data[position].longitude)
                    intent.putExtra("description", data[position].description)
                    intent.putExtra("id", data[position].id)
                    intent.putExtra("image", data[position].image)
                    context.startActivity(intent)
                }


            }
        }
    }
}