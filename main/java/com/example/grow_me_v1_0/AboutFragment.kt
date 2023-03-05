package com.example.grow_me_v1_0

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.grow_me_v1_0.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentAboutBinding>(inflater, R.layout.fragment_about, container, false)

        val buttonConfirm = binding.buttonConfirm
        val editIP = binding.editIP
        val editPort = binding.editPort
        buttonConfirm.setOnClickListener {
            if(editIP.text.isNotEmpty()){
                Common.DEFAULT_IP_ADDRESS = editIP.text.toString()
            }
            else{
                Toast.makeText(activity, "Nie podano adresu IP", Toast.LENGTH_LONG).show()
            }

            if(editPort.text.isNotEmpty()){
                Common.DEFAULT_PORT = editPort.text.toString()
            }
            else{
                Toast.makeText(activity, "Nie podano portu", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }
}