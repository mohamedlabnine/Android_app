package com.example.product

import androidx.recyclerview.widget.RecyclerView
import com.example.product.ProductAdapter.viewholder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.product.databinding.SingleItemBinding
import java.util.ArrayList

 class ProductAdapter(var context: Context) : RecyclerView.Adapter<viewholder>() {
     lateinit var binding :SingleItemBinding
    var data = ArrayList<Product>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        var inflater=LayoutInflater.from(parent.context)
        binding=SingleItemBinding.inflate(inflater,parent,false)
        return viewholder(binding.root)

    }

    override fun onBindViewHolder(holder: viewholder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(data[position],position)


    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setdata(array: ArrayList<Product>) {
        data = array
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(x:Product,position:Int){
            binding.apply {
                Glide.with(image.context)
                    .load("https://yassirnasri.000webhostapp.com/products/" + data[position].image)
                    .into(image)
               name.text=x.name
                price.text=x.price
                cardview.setOnClickListener {
                    val intent = Intent(context, Product_page::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("id", data[position].id)
                    intent.putExtra("name", data[position].name)
                    intent.putExtra("price", data[position].price)
                    intent.putExtra("use_case", data[position].use_case)
                    intent.putExtra("component", data[position].component)
                    intent.putExtra("caution", data[position].caution)
                    intent.putExtra("description", data[position].description)
                    intent.putExtra("id_market", data[position].id_market)
                    intent.putExtra("image", data[position].image)
                    context.startActivity(intent)
                }
            }
        }
    }
}