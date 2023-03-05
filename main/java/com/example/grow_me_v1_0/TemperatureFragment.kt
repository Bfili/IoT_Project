package com.example.grow_me_v1_0

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.grow_me_v1_0.adder.AdderFragmentModel
import com.example.grow_me_v1_0.databinding.FragmentTemperatureBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*
import android.os.Handler
import android.widget.Button
import android.widget.CheckBox
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class TemperatureFragment : Fragment() {
    private lateinit var binding : FragmentTemperatureBinding
    private lateinit var viewModel : AdderFragmentModel

    private lateinit var dataGraph: GraphView
    private lateinit var series: LineGraphSeries<DataPoint>
    private lateinit var series2: LineGraphSeries<DataPoint>

    private lateinit var tempCheckbox : CheckBox
    private lateinit var presCheckbox : CheckBox
    private lateinit var humCheckbox : CheckBox
    private lateinit var orientCheckbox : CheckBox
    private lateinit var rollCheckbox : CheckBox
    private lateinit var pitchCheckbox : CheckBox
    private lateinit var yawCheckbox : CheckBox

    private var xAxisValue = 0
    private val handler: Handler = Handler()
    private val sampleTime: Int = 100
    private var requestTimerTask: TimerTask? = null
    private var requestTimer: Timer? = null
    private var requestTimerFirstRequestAfterStop = false

    private var plotParam1 = " "
    private var plotParam2 = " "

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentTemperatureBinding>(inflater,
            R.layout.fragment_temperature, container, false)

        tempCheckbox = binding.checkBox1
        tempCheckbox.setOnCheckedChangeListener { _, _ ->
            if(plotParam1 == " "){
                plotParam1 = "temperature"
            }
            else{
                plotParam2 = "temperature"
            }
        }

        presCheckbox = binding.checkBox2
        presCheckbox.setOnCheckedChangeListener { _, _ ->
            if(plotParam1 == " "){
                plotParam1 = "pressure"
            }
            else{
                plotParam2 = "pressure"
            }
        }

        humCheckbox = binding.checkBox3
        humCheckbox.setOnCheckedChangeListener { _, _ ->
            if(plotParam1 == " "){
                plotParam1 = "humidity"
            }
            else{
                plotParam2 = "humidity"
            }
        }

        orientCheckbox = binding.checkBox4
        rollCheckbox = binding.checkBox12
        pitchCheckbox = binding.checkBox13
        yawCheckbox = binding.checkBox14
        var check = 0
        orientCheckbox.setOnCheckedChangeListener { _, _ ->
            if (check == 0) {
                rollCheckbox.visibility = View.VISIBLE
                pitchCheckbox.visibility = View.VISIBLE
                yawCheckbox.visibility = View.VISIBLE
                check = 1
            } else {
                rollCheckbox.visibility = View.INVISIBLE
                pitchCheckbox.visibility = View.INVISIBLE
                yawCheckbox.visibility = View.INVISIBLE
                check = 0
            }

        }

        rollCheckbox.setOnCheckedChangeListener { _, _ ->
            if(plotParam1 == " "){
                plotParam1 = "roll"
            }
            else{
                plotParam2 = "roll"
            }
        }

        pitchCheckbox.setOnCheckedChangeListener { _, _ ->
            if(plotParam1 == " "){
                plotParam1 = "pitch"
            }
            else{
                plotParam2 = "pitch"
            }
        }

        yawCheckbox.setOnCheckedChangeListener { _, _ ->
            if(plotParam1 == " "){
                plotParam1 = "yaw"
            }
            else{
                plotParam2 = "yaw"
            }
        }

        dataGraph = binding.dataGraph

        dataGraph.viewport.isXAxisBoundsManual = true
        dataGraph.viewport.setMinX(0.0)
        dataGraph.viewport.setMaxX(50.0)

        dataGraph.gridLabelRenderer.textSize = 30F
        dataGraph.gridLabelRenderer.horizontalAxisTitle = "X [0.1s]"
        dataGraph.gridLabelRenderer.verticalAxisTitle = "Y_red [-]"
        dataGraph.gridLabelRenderer.verticalAxisTitleColor = Color.RED
        dataGraph.gridLabelRenderer.verticalAxisTitleTextSize = 35F

        dataGraph.secondScale.setMinY(0.0)
        dataGraph.secondScale.setMaxY(360.0)
        dataGraph.secondScale.verticalAxisTitle = "Y_blue [-]"
        dataGraph.secondScale.verticalAxisTitleColor = Color.BLUE
        dataGraph.secondScale.verticalAxisTitleTextSize = 35F


        series = LineGraphSeries(arrayOf<DataPoint>())
        series.color = Color.RED
        series2 = LineGraphSeries(arrayOf<DataPoint>())
        series2.color = Color.BLUE

        dataGraph.addSeries(series)
        dataGraph.secondScale.addSeries(series2)

        val startButton : Button = binding.startButton
        startButton.setOnClickListener{
            startRequestTimer()
            initializeRequestTimerTask()
        }

        val stopButton : Button = binding.stopButton
        stopButton.setOnClickListener{
            //Toast.makeText(activity, series.getValues(0.0, series.highestValueX)., Toast.LENGTH_LONG).show()
            stopRequestTimerTask()
        }

        return binding.root
    }

    private fun getValFromURL(){
        val IP = Common.DEFAULT_IP_ADDRESS
        val port = Common.DEFAULT_PORT
        val url = "http://$IP:$port/getAdvancedData"
        val queue = Volley.newRequestQueue(activity)
        val params = HashMap<String, String>()
        val request = object : StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                responseHandling(response!!)
            },
            Response.ErrorListener { error ->
                //Toast.makeText(activity, "No server connection", Toast.LENGTH_LONG).show()
            }) {
        }
        queue.add(request)
    }

    private fun responseHandling(response: String) : Pair<Double, Double> {
        // get data from JSON -> add datapoint of value y to graph
        val (rawData1, rawData2) = getRawDataFromResponse(response)
        //Toast.makeText(this, rawData.toString(), Toast.LENGTH_LONG).show()
        series.appendData(DataPoint(xAxisValue.toDouble(), rawData1), true, 51)
        series2.appendData(DataPoint(xAxisValue.toDouble(), rawData2), true, 51)
        xAxisValue += 1
        return Pair(rawData1, rawData2)
    }

    private fun getRawDataFromResponse(response: String?): Pair<Double, Double> {
        val jObject: JSONObject
        var xValue1 = Double.NaN
        var xValue2 = Double.NaN
        var key1 = " "
        var key2 = " "

        when (plotParam1) {
            "temperature" -> key1 = "temperatura"
            "pressure" -> key1 = "pressure"
            "humidity" -> key1 = "humidity"
            "roll" -> key1 = "roll"
            "pitch" -> key1 = "pitch"
            "yaw" -> key1 = "yaw"
        }
        when (plotParam2) {
            "temperature" -> key2 = "temperatura"
            "pressure" -> key2 = "pressure"
            "humidity" -> key2 = "humidity"
            "roll" -> key2 = "roll"
            "pitch" -> key2 = "pitch"
            "yaw" -> key2 = "yaw"
        }



        // Create generic JSON object form string
        jObject = try {
            JSONObject(response)
        } catch (e: JSONException) {
            e.printStackTrace()
            return Pair(xValue1, xValue2)
        }

        // Read chart data form JSON object
        try {
            if(plotParam1 == "roll" || plotParam1 == "pitch" || plotParam1 == "yaw"){
                xValue1 = jObject.getJSONObject("orient").getDouble(key1)
            }
            else{
                xValue1 = jObject.getDouble(key1)
            }

            if(plotParam2 == "roll" || plotParam2 == "pitch" || plotParam2 == "yaw"){
                xValue2 = jObject.getJSONObject("orient").getDouble(key2)
            }
            else{
                xValue2 = jObject.getDouble(key2)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return Pair(xValue1, xValue2)
    }


    private fun startRequestTimer() {
        if (requestTimer == null) {
            // set a new Timer
            requestTimer = Timer()

            // initialize the TimerTask's job
            initializeRequestTimerTask()
            requestTimer!!.schedule(requestTimerTask, 0, sampleTime.toLong())

            // clear error message
            Toast.makeText(activity, "Starting Timer", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopRequestTimerTask() {
        // stop the timer, if it's not already null
        if (requestTimer != null) {
            requestTimer!!.cancel()
            requestTimer = null
            requestTimerFirstRequestAfterStop = true
            Toast.makeText(activity, "Stopping Timer", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeRequestTimerTask() {
        requestTimerTask = object : TimerTask() {
            override fun run() {
                handler.post { getValFromURL() }
            }
        }
    }
}