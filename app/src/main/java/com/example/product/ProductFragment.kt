package com.example.product

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.product.databinding.FragmentProductBinding


class ProductFragment : Fragment() {
    var binding:FragmentProductBinding? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        binding = FragmentProductBinding.inflate(inflater,container,false);
        val view = binding?.root;
        Glide.with(binding?.productimage!!.context).load(
            "https://yassirnasri.000webhostapp.com/products/" + bundle?.getString("image")
        ).into(
            binding?.productimage!!
        )
        binding?.productname?.text = bundle?.getString("name")
        binding?.productprice?.text = bundle?.getString("price")
        binding?.productdescription?.text = bundle?.getString("description")
        binding?.productusecase?.text = bundle?.getString("use_case")
        binding?.productcomponent?.text = bundle?.getString("component")
        binding?.productcaution?.text = bundle?.getString("caution")
        binding?.rate?.setOnClickListener {
            Toast.makeText(
                activity,
                binding?.ratingbar?.rating.toString() + " star",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding?.buy!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(getActivity(), Order_Product::class.java)
            intent.putExtra("name", bundle?.getString("name"))
            intent.putExtra("price", bundle?.getString("price"))
            intent.putExtra("description", bundle?.getString("description"))
            intent.putExtra("id_market", bundle?.getInt("id_market"))
            intent.putExtra("image", bundle?.getString("image"))
            intent.putExtra("id", bundle?.getInt("id"))
            startActivity(intent)
        })
        return view
    }




}