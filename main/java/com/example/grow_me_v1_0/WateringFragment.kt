package com.example.grow_me_v1_0

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.grow_me_v1_0.databinding.FragmentWateringBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class WateringFragment : Fragment() {
    private lateinit var binding : FragmentWateringBinding
    private lateinit var getAllPixelsButton: Button
    private lateinit var setColorButton: Button
    private lateinit var setCoordsButton : Button
    private lateinit var setAllPixelsButton : Button
    private lateinit var setSinglePixelButton : Button
    private lateinit var pixelsStateArray : MutableList<JSONArray>
    private lateinit var phoneMatrixSquaresArray : MutableList<TextView>
    private var singularJson = JSONArray()
    private var colorInput = " "
    private var coordsInput = " "

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_watering, container, false)

        phoneMatrixSquaresArray= mutableListOf(
            binding.textview11, binding.textview12, binding.textview13, binding.textview14, binding.textview15, binding.textview16, binding.textview17, binding.textview18,
            binding.textview21, binding.textview22, binding.textview23, binding.textview24, binding.textview25, binding.textview26, binding.textview27, binding.textview28,
            binding.textview31, binding.textview32, binding.textview33, binding.textview34, binding.textview35, binding.textview36, binding.textview37, binding.textview38,
            binding.textview41, binding.textview42, binding.textview43, binding.textview44, binding.textview45, binding.textview46, binding.textview47, binding.textview48,
            binding.textview51, binding.textview52, binding.textview53, binding.textview54, binding.textview55, binding.textview56, binding.textview57, binding.textview58,
            binding.textview61, binding.textview62, binding.textview63, binding.textview64, binding.textview65, binding.textview66, binding.textview67, binding.textview68,
            binding.textview71, binding.textview72, binding.textview73, binding.textview74, binding.textview75, binding.textview76, binding.textview77, binding.textview78,
            binding.textview81, binding.textview82, binding.textview83, binding.textview84, binding.textview85, binding.textview86, binding.textview87, binding.textview88
        )

        setColorButton = binding.setColor
        setColorButton.setOnClickListener {
            openColorPickerDialog()
        }

        setCoordsButton = binding.setCoords
        setCoordsButton.setOnClickListener {
            openCoordsDialog()
        }

        val handler = Handler()
        getAllPixelsButton = binding.getAllPixels
        getAllPixelsButton.setOnClickListener{
            getValFromURL()
            handler.postDelayed({
                actuatePhoneMatrixSquaresArray()
            }, 200)

        }

        setSinglePixelButton = binding.setSingle
        setSinglePixelButton.setOnClickListener {
            setSinglePixel()
        }

        setAllPixelsButton = binding.button
        setAllPixelsButton.setOnClickListener {
            sendValToURL()
        }

        return binding.root
    }

    private fun zeroStringChecker(string : String) : String{
        var reString = string
        reString = if(reString == "0"){
            "00"
        } else{
            string
        }
        return reString
    }

    private fun actuatePhoneMatrixSquaresArray(){
        for(i in 0..63){
            phoneMatrixSquaresArray[i].setBackgroundColor(Color.parseColor(
                "#" + zeroStringChecker(Integer.toHexString(pixelsStateArray[i].get(0).toString().toInt())) +
                        zeroStringChecker(Integer.toHexString(pixelsStateArray[i].get(1).toString().toInt())) +
                        zeroStringChecker(Integer.toHexString(pixelsStateArray[i].get(2).toString().toInt()))
            ))
        }
    }

    private fun getValFromURL(){
        val IP = Common.DEFAULT_IP_ADDRESS
        val port = Common.DEFAULT_PORT
        val url = "http://$IP:$port/getPixels"
        val queue = Volley.newRequestQueue(activity)
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                //Toast.makeText(activity, "Connected", Toast.LENGTH_LONG).show()
                responseHandling(response!!)
            },
            Response.ErrorListener {
                //Toast.makeText(activity, "No server connection", Toast.LENGTH_LONG).show()
            }) {
        }
        queue.add(request)
    }


    private fun sendValToURL(){
        val IP = Common.DEFAULT_IP_ADDRESS
        val port = Common.DEFAULT_PORT
        val url = "http://$IP:$port/setAllPixels"

        val colors = colorInput.split(":")
        singularJson.put(0, 0)
        singularJson.put(0, colors[0].toInt(16))
        singularJson.put(1, 0)
        singularJson.put(1, colors[1].toInt(16))
        singularJson.put(2, 0)
        singularJson.put(2, colors[2].toInt(16))

        val jsonBody = JSONArray()
        for(i in 0..63){
            jsonBody.put(i, singularJson)
        }

        val queue = Volley.newRequestQueue(activity)

        val request = object : JsonArrayRequest(
            Method.POST, url, jsonBody,
            Response.Listener {
                //Toast.makeText(activity, "Connected", Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener {
                //Toast.makeText(activity, "No server connection", Toast.LENGTH_LONG).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue.add(request)
    }

    private fun setSinglePixel(){
        val IP = Common.DEFAULT_IP_ADDRESS
        val port = Common.DEFAULT_PORT
        val url = "http://$IP:$port/setSinglePixel"
        val jsonBody = JSONObject()

        val colors = colorInput.split(":")
        jsonBody.put("R", 0)
        jsonBody.put("R", colors[0].toInt(16))
        jsonBody.put("G", 0)
        jsonBody.put("G", colors[1].toInt(16))
        jsonBody.put("B", 0)
        jsonBody.put("B", colors[2].toInt(16))

        val coords = coordsInput.split(",")
        jsonBody.put("x", 0)
        jsonBody.put("x", coords[0].toInt())
        jsonBody.put("y", 0)
        jsonBody.put("y", coords[1].toInt())
        Toast.makeText(activity, jsonBody.toString(), Toast.LENGTH_LONG).show()

        val queue = Volley.newRequestQueue(activity)

        val request = object : JsonObjectRequest(
            Method.POST, url, jsonBody,
            Response.Listener {
                //Toast.makeText(activity, "Connected", Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener {
                //Toast.makeText(activity, "No server connection", Toast.LENGTH_LONG).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue.add(request)
    }

    private fun responseHandling(response: String) : MutableList<JSONArray> {
        val data = getRawDataFromResponse(response)
        pixelsStateArray = data
        return data
    }

    private fun getRawDataFromResponse(response: String?): MutableList<JSONArray> {
        val jArray: JSONArray
        val pixels = mutableListOf<JSONArray>()

        jArray = try {
            JSONArray(response)
        } catch (e: JSONException) {
            e.printStackTrace()
            return pixels
        }

        try {
            for(i in 0..63){
                pixels.add(jArray.getJSONArray(i))
            }
            //data = jArray.getJSONArray(9).toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return pixels
    }

    private fun openColorPickerDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Enter color in hex code values e.g. FF:FF:FF")

        // Set up the input
        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { _, _ ->
            colorInput = input.text.toString()
            Toast.makeText(activity, "Zapisano kolor: $colorInput", Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun openCoordsDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Enter x and y values, in range [0-7] e.g. 4,5")

        // Set up the input
        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { _, _ ->
            coordsInput = input.text.toString()
            Toast.makeText(activity, "Saved coordinates: $coordsInput", Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}