package com.example.grow_me_v1_0

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.grow_me_v1_0.databinding.MainMenuFragmentBinding

class Menu : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<MainMenuFragmentBinding>(inflater, R.layout.main_menu_fragment,container,false)
        binding.plantButtonMenu.setOnClickListener { view : View -> view.findNavController().navigate(R.id.action_menu_to_adderFragment) }
        binding.temperatureButtonMenu.setOnClickListener { view : View -> view.findNavController().navigate(R.id.action_menu_to_temperatureFragment2)}
        binding.wateringButtonMenu.setOnClickListener { view : View -> view.findNavController().navigate(R.id.action_menu_to_wateringFragment)}
        return binding.root
    }

}