package com.example.product

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.product.databinding.FragmentMarketBinding
import com.example.product.databinding.FragmentProductBinding
import java.text.DecimalFormat

class MarketFragment : Fragment() {
    var binding: FragmentMarketBinding? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        binding = FragmentMarketBinding.inflate(inflater,container,false);
        val view = binding?.root;
        Glide.with(binding?.marketimage!!.context).load(
            "https://yassirnasri.000webhostapp.com/markets/" +bundle?.getString("image")
        ).into(
            binding?.marketimage!!
        )
        binding?.distance?.text="Distance "+ DecimalFormat("#.##").format(bundle?.getFloat("distance")).toString()+" KM"
        binding?.marketname?.text =  bundle?.getString("name")
        binding?.marketdescription?.text =  bundle?.getString("description")
        binding?.gotomap!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(getActivity(), MapsActivity::class.java)
            intent.putExtra("name",  bundle?.getDouble("name"))
            intent.putExtra("latitude",  bundle?.getDouble("latitude"))
            intent.putExtra("longitude",  bundle?.getDouble("longitude"))
            startActivity(intent)
        })

        return view


    }
}