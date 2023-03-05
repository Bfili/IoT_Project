package com.example.grow_me_v1_0

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.grow_me_v1_0.databinding.FragmentTitleBinding

class TitleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentTitleBinding>(inflater, R.layout.fragment_title,container,false)
        binding.startButton.setOnClickListener{view : View -> view.findNavController().navigate(R.id.action_titleFragment_to_menu)}
        return binding.root
    }
}