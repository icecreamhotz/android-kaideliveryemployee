package app.icecreamhot.kaidelivery_employee.ui.order.Map

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.data.mLatitude
import app.icecreamhot.kaidelivery_employee.data.mLongitude
import app.icecreamhot.kaidelivery_employee.firebasemodel.LatLngFB
import app.icecreamhot.kaidelivery_employee.firebasemodel.OrderFB
import app.icecreamhot.kaidelivery_employee.model.GoogleMapDTO
import app.icecreamhot.kaidelivery_employee.model.Order
import app.icecreamhot.kaidelivery_employee.model.PolyLine
import app.icecreamhot.kaidelivery_employee.network.OrderAPI
import app.icecreamhot.kaidelivery_employee.ui.order.Alert.CheckBillDialog
import app.icecreamhot.kaidelivery_employee.ui.order.Alert.FoodDetailDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_maps_fragment.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception

class MapsFragment : Fragment(),
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener,
    OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener {

    private lateinit var mMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mLocationManager: LocationManager? = null
    private var disposable: Disposable? = null

    var googleApiClient: GoogleApiClient? = null
    val REQUEST_CODE = 1000

    lateinit var mLocationRequest: LocationRequest
    private lateinit var ref: DatabaseReference
    private var order_name:String? = null
    private var order_status: Int? = null

    private lateinit var restaurant: LatLng
    private lateinit var endpoint: LatLng
    private lateinit var markerEmployee: Marker
    private lateinit var markerEndpoint: Marker
    private lateinit var markerRestaurant: Marker
    private var polyline: Polyline? = null

    lateinit var btnOrderDetail: Button
    lateinit var mOrder: ArrayList<app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.Order>

    private val orderAPI by lazy {
        OrderAPI.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_maps_fragment, container, false)
        btnOrderDetail = view.findViewById(R.id.btnOrderDetail)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        googleApiClient = GoogleApiClient.Builder(context!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }

        mLocationManager = activity?.applicationContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        btnOrderDetail.setOnClickListener {
            val dialogDetailFragment = FoodDetailDialog.newInstance(mOrder)
            dialogDetailFragment.show(fragmentManager, "OrderDetailFragment")
        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(activity!!.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            }
        }
        else {
            mMap.isMyLocationEnabled = true
        }
    }

    override fun onCameraMove() {
        mMap.clear()
    }

    override fun onCameraIdle() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.option_maps_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return (when(item?.itemId) {
            R.id.orderColected -> {
                onClickOrderCollected()
                true
            }
            R.id.onTheWay -> {
                onClickOnTheWay()
                true
            }
            R.id.checkBill ->
            {
                onClickCheckbill()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

    private fun onClickOrderCollected() {
        order_name?.let {
            order_status = 2
            ref = FirebaseDatabase.getInstance().getReference("Delivery")

            val latLng = OrderFB(mLatitude, mLongitude, 123, 2)
            ref.child(order_name!!).setValue(latLng).addOnSuccessListener {
                Toast.makeText(activity?.applicationContext, "order waiting", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClickOnTheWay() {
        order_name?.let {
            order_status = 3
            ref = FirebaseDatabase.getInstance().getReference("Delivery")

            val latLng = OrderFB(mLatitude, mLongitude, 123, 3)
            val now = LatLng(mLatitude!!, mLongitude!!)
            ref.child(order_name!!).setValue(latLng).addOnSuccessListener {
                markerRestaurant.remove()
                markerEmployee.position = now
                val url = getURL(now, endpoint)
                GetDirection(url).execute()
                Toast.makeText(activity?.applicationContext, "on the way", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClickCheckbill() {
        val dialogCheckBillDialog = CheckBillDialog.newInstance(mOrder)
        dialogCheckBillDialog.show(fragmentManager, "dialogCheckBillDialog")
        Toast.makeText(activity?.applicationContext, "check bill", Toast.LENGTH_SHORT).show()
    }

    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        startLocationUpdates()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                    location: Location ->
                mLatitude = location.latitude
                mLongitude = location.longitude
                loadDeliveryNow()
            }
    }

    override fun onLocationChanged(p0: Location?) {
        mLatitude = p0?.latitude
        mLongitude = p0?.longitude
        order_name?.let {
            ref = FirebaseDatabase.getInstance().getReference("Delivery")

            val latlng = OrderFB(mLatitude, mLongitude, 123, order_status!!)
            val now = LatLng(mLatitude!!, mLongitude!!)
            ref.child(order_name!!).setValue(latlng).addOnSuccessListener {
                var url = ""
                if(order_status == 1 || order_status == 2) {
                    url = getURL(now, restaurant)
                } else {
                    url = getURL(now, endpoint)
                }
                markerEmployee.position = now
                GetDirection(url).execute()
            }
        }
    }

    private fun startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5000)
            .setFastestInterval(5000)

        if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
            mLocationRequest, this)
    }

    override fun onConnectionSuspended(p0: Int) {
        googleApiClient!!.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        googleApiClient!!.disconnect()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_CODE -> {
                if(grantResults.isNotEmpty()) {
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadDeliveryNow() {
        disposable = orderAPI.getDeliveryNow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
            .subscribe(
                {
                        result ->
                        getOrderStatus(result.orderList)
                        loadOrderDetail(result.orderList?.get(0)?.order_id)
                },
                {
                        err -> Log.d("err", err.message)
                }
            )
    }

    private fun loadOrderDetail(order_id: Int?) {
        disposable = orderAPI.getOrderAndOrderDetail(order_id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
            .subscribe(
                {
                        result -> mOrder = result.data
                },
                {
                        err -> Log.d("err", err.message)
                }
            )
    }

    private fun setMarkerRestaurant(orderList: ArrayList<Order>?) {
        restaurant = LatLng(orderList?.get(0)?.restaurant!!.res_lat, orderList.get(0).restaurant!!.res_lng)
        endpoint = LatLng(orderList.get(0).endpoint_lat, orderList.get(0).endpoint_lng)

        val now = LatLng(mLatitude!!, mLongitude!!)

        val markerOptionEmployee = MarkerOptions()
        markerOptionEmployee.position(now)
        markerEmployee = mMap.addMarker(markerOptionEmployee)
        markerEmployee.title = "You"
        markerEmployee.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

        val markerOptionRestaurant = MarkerOptions()
        markerOptionRestaurant.position(restaurant)
        markerRestaurant = mMap.addMarker(markerOptionRestaurant)
        markerRestaurant.title = orderList.get(0).restaurant!!.res_name
        markerRestaurant.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        val markerOptionEndpoint = MarkerOptions()
        markerOptionEndpoint.position(endpoint)
        markerEndpoint = mMap.addMarker(markerOptionEndpoint)
        markerEndpoint.title = orderList.get(0).endpoint_name
        markerEndpoint.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setMinZoomPreference(11f)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(now))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17f))

        var url = ""

        if(order_status == 1 || order_status == 2) {
            url = getURL(now, restaurant)
        } else {
            url = getURL(now, endpoint)
            markerRestaurant.remove()
        }

        GetDirection(url).execute()
    }

    private fun getOrderStatus(orderList: ArrayList<Order>?) {
        order_name = orderList!!.get(0).order_name

        order_name?.let {
            ref = FirebaseDatabase.getInstance().getReference("Delivery")
            ref.child(it).child("status").addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    order_status = p0.getValue(Int::class.java)
                    setMarkerRestaurant(orderList)
                }
            })
        }
    }


    private fun getURL(from : LatLng, to : LatLng) : String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${from.latitude},${from.longitude}&destination=${to.latitude},${to.longitude}&sensor=false&mode=driving&key=AIzaSyDCkgDceoiSbeWa29pNeJxmsNipUF7P3uw"
    }

    private inner class GetDirection(val url : String) : AsyncTask<Void,Void,List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.d("GoogleMap" , " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data,GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            if(polyline != null) {
                polyline?.remove()
            }
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            polyline = mMap.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

}
