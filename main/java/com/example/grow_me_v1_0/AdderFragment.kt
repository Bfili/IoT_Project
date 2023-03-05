package com.example.grow_me_v1_0

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.grow_me_v1_0.adder.AdderFragmentModel
import com.example.grow_me_v1_0.databinding.FragmentAdderBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AdderFragment : Fragment() {
    private lateinit var binding: FragmentAdderBinding
    private lateinit var viewModel : AdderFragmentModel
    private var measTable : Array<JSONObject>? = null
    private var measId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_adder, container, false)

        viewModel = ViewModelProvider(this).get(AdderFragmentModel::class.java)

        val buttonStart = binding.buttonStart

        val tableLayout = binding.measTabLayout

        getValFromURL()

        val handler = Handler()
        buttonStart.setOnClickListener{
            getValFromURL()
            handler.postDelayed({
                val tableRow = TableRow(activity)
                val tableParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                tableRow.layoutParams = tableParams

                val textView1 = TextView(activity)
                textView1.text = measId.toString()
                measId++
                val idParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
                textView1.layoutParams = idParams

                val textView2 = TextView(activity)
                textView2.text = measTable?.get(0)?.getDouble("wartosc")
                    ?.let { it1 -> roundToTwoDec(it1).toString() } +
                        measTable?.get(0)?.getString("jednostka").toString()
                val tempParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    3.0f
                )
                textView2.layoutParams = tempParams

                val textView3 = TextView(activity)
                textView3.text = measTable?.get(1)?.getDouble("wartosc")
                    ?.let { it1 -> roundToTwoDec(it1).toString() } +
                        measTable?.get(1)?.getString("jednostka").toString()
                textView3.layoutParams = tempParams

                val textView4 = TextView(activity)
                textView4.text = measTable?.get(2)?.getDouble("wartosc")
                    ?.let { it1 -> roundToTwoDec(it1).toString() } +
                        measTable?.get(2)?.getString("jednostka").toString()
                textView4.layoutParams = tempParams

                val textView5 = TextView(activity)
                textView5.text = "R:${measTable?.get(3)?.getDouble("wartosc")
                                    ?.let { it1 -> roundToTwoDec(it1).toString() } +
                                    measTable?.get(3)?.getString("jednostka").toString()}\n" +
                                    "P:${measTable?.get(4)?.getDouble("wartosc")
                                    ?.let { it1 -> roundToTwoDec(it1).toString() } + 
                                    measTable?.get(4)?.getString("jednostka").toString()}\n" +
                                    "Y:${measTable?.get(5)?.getDouble("wartosc")
                                    ?.let { it1 -> roundToTwoDec(it1).toString() } + 
                                    measTable?.get(5)?.getString("jednostka").toString()}"
                textView5.layoutParams = tempParams


                tableRow.addView(textView1)
                tableRow.addView(textView2)
                tableRow.addView(textView3)
                tableRow.addView(textView4)
                tableRow.addView(textView5)
                tableLayout.addView(tableRow)
            }, 100)
        }


        return binding.root
    }

    private fun getValFromURL(){
        val IP = Common.DEFAULT_IP_ADDRESS
        val port = Common.DEFAULT_PORT
        val url = "http://$IP:$port/getAdvancedData2"
        val queue = Volley.newRequestQueue(activity)
        val request = object : StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                measTable = responseHandling(response!!)
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity, "No server connection", Toast.LENGTH_LONG).show()
            }) {
        }
        queue.add(request)
    }

    private fun responseHandling(response: String) : Array<JSONObject> {
        // get data from JSON -> add datapoint of value y to graph
        val data = getRawDataFromResponse(response)
        //Toast.makeText(activity, data[0].toString(), Toast.LENGTH_LONG).show()
        return data
    }

    private fun getRawDataFromResponse(response: String?): Array<JSONObject> {
        //val jObject: JSONObject
        val jArray: JSONArray
        var supportList = arrayOf<JSONObject>()


        // Create generic JSON object form string
        jArray = try {
            JSONArray(response)
        } catch (e: JSONException) {
            e.printStackTrace()
            return supportList
        }

        val temperatureA = jArray.getJSONObject(0)
        val pressureA = jArray.getJSONObject(1)
        val humidityA = jArray.getJSONObject(2)
        val rollA = jArray.getJSONObject(3)
        val pitchA = jArray.getJSONObject(4)
        val yawA = jArray.getJSONObject(5)

        // Read chart data form JSON object
        try {
            supportList += temperatureA
            supportList += pressureA
            supportList += humidityA
            supportList += rollA
            supportList += pitchA
            supportList += yawA
//            supportList += roundToTwoDec(jObject.getDouble("temperatura"))
//            supportList += roundToTwoDec(jObject.getDouble("pressure"))
//            supportList += roundToTwoDec(jObject.getDouble("humidity"))
//            supportList += roundToTwoDec(jObject.getJSONObject("orient").getDouble("roll"))
//            supportList += roundToTwoDec(jObject.getJSONObject("orient").getDouble("pitch"))
//            supportList += roundToTwoDec(jObject.getJSONObject("orient").getDouble("yaw"))

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //Toast.makeText(activity, supportList[0].toString(), Toast.LENGTH_LONG).show()
        return supportList
    }

    private fun roundToTwoDec(value: Double): Double {
        return String.format("%.2f", value).toDouble()
    }
}


//class AdderFragment : Fragment() {
//    private lateinit var binding: FragmentAdderBinding
//    private lateinit var viewModel : AdderFragmentModel
//
//    private val handler: Handler = Handler()
//    private val sampleTime: Int = 500
//    private var requestTimerTask: TimerTask? = null
//    private var requestTimer: Timer? = null
//    private var requestTimerFirstRequestAfterStop = false
//
//    private var changingID = 1
////    private val listOfMeasurements = arrayOf(
////        listOf<Any>(R.id.tempVal, R.id.tempUnit),
////        listOf<Any>(R.id.humVal, R.id.humUnit),
////        listOf<Any>(R.id.preVal, R.id.preUnit),
////        listOf<Any>(R.id.rollVal, R.id.rollUnit),
////        listOf<Any>(R.id.pitchVal, R.id.pitchUnit),
////        listOf<Any>(R.id.yawVal, R.id.yawUnit)
////    )
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding = DataBindingUtil.inflate<FragmentAdderBinding>(inflater,
//            R.layout.fragment_adder, container, false)
//
//        viewModel = ViewModelProvider(this).get(AdderFragmentModel::class.java)
//
//        val buttonStart = binding.buttonStart
//        val buttonStop = binding.buttonStop
//
////        binding.tempUnit.text = "C"
////        binding.preUnit.text = "hPa"
////        binding.humUnit.text = "%"
////        binding.rollUnit.text = "deg"
////        binding.pitchUnit.text = "deg"
////        binding.yawUnit.text = "deg"
//
//        buttonStart.setOnClickListener{
//            startRequestTimer()
//            initializeRequestTimerTask()
//        }
//
//        buttonStop.setOnClickListener {
//            stopRequestTimerTask()
//        }
//
//
//        return binding.root
//    }
//
//    private fun getValFromURL(): Boolean {
//        val IP = Common.DEFAULT_IP_ADDRESS
//        val port = Common.DEFAULT_PORT
//        val url = "http://$IP:$port/getAdvancedData"
//        val queue = Volley.newRequestQueue(activity)
//        val params = HashMap<String, String>()
//        val request = object : StringRequest(Request.Method.GET, url,
//            Response.Listener<String> { response ->
//                responseHandling(response!!)
//            },
//            Response.ErrorListener { error ->
//                Toast.makeText(activity, "No server connection", Toast.LENGTH_LONG).show()
//            }) {
//        }
//        queue.add(request)
//        return true
//    }
//
//    private fun responseHandling(response: String) : Array<Double> {
//        // get data from JSON -> add datapoint of value y to graph
//        val rawData= getRawDataFromResponse(response)
////        view?.findViewById<TextView>(R.id.tempVal)?.text = rawData[0].toString()
////        view?.findViewById<TextView>(R.id.humVal)?.text = rawData[1].toString()
////        view?.findViewById<TextView>(R.id.preVal)?.text = rawData[2].toString()
////        view?.findViewById<TextView>(R.id.rollVal)?.text = rawData[3].toString()
////        view?.findViewById<TextView>(R.id.pitchVal)?.text = rawData[4].toString()
////        view?.findViewById<TextView>(R.id.yawVal)?.text = rawData[5].toString()
//        return rawData
//    }
//
//    private fun getRawDataFromResponse(response: String?): Array<Double> {
//        val jObject: JSONObject
//        var supportList = arrayOf<Double>()
//
//        // Create generic JSON object form string
//        jObject = try {
//            JSONObject(response)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//            return supportList
//        }
//
//        // Read chart data form JSON object
//        try {
//            supportList += roundToTwoDec(jObject.getDouble("temperatura"))
//            supportList += roundToTwoDec(jObject.getDouble("pressure"))
//            supportList += roundToTwoDec(jObject.getDouble("humidity"))
//            supportList += roundToTwoDec(jObject.getJSONObject("orient").getDouble("roll"))
//            supportList += roundToTwoDec(jObject.getJSONObject("orient").getDouble("pitch"))
//            supportList += roundToTwoDec(jObject.getJSONObject("orient").getDouble("yaw"))
//
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        return supportList
//    }
//
//    private fun roundToTwoDec(value:Double): Double {
//        val number2dig:Double = String.format("%.2f",value).toDouble()
//        return number2dig
//    }
//
//    private fun startRequestTimer() {
//        if (requestTimer == null) {
//            // set a new Timer
//            requestTimer = Timer()
//
//            // initialize the TimerTask's job
//            initializeRequestTimerTask()
//            requestTimer!!.schedule(requestTimerTask, 0, sampleTime.toLong())
//
//            // clear error message
//            //Toast.makeText(this, "Starting Timer", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private fun stopRequestTimerTask() {
//        // stop the timer, if it's not already null
//        if (requestTimer != null) {
//            requestTimer!!.cancel()
//            requestTimer = null
//            requestTimerFirstRequestAfterStop = true
//            //Toast.makeText(this, "Stopping Timer", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private fun initializeRequestTimerTask() {
//        requestTimerTask = object : TimerTask() {
//            override fun run() {
//                handler.post {
//                    var error = getValFromURL()
//                }
//            }
//        }
//    }
//}