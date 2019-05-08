package app.icecreamhot.kaidelivery_employee.ui.order

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.data.mLatitude
import app.icecreamhot.kaidelivery_employee.data.mLongitude
import app.icecreamhot.kaidelivery_employee.firebasemodel.LatLngFB
import app.icecreamhot.kaidelivery_employee.firebasemodel.OrderFB
import app.icecreamhot.kaidelivery_employee.model.Order
import app.icecreamhot.kaidelivery_employee.network.OrderAPI
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_list.*
import com.google.firebase.database.DataSnapshot

//class OrderListActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
//    GoogleApiClient.OnConnectionFailedListener {
//
//    private val orderAPI by lazy {
//        OrderAPI.create()
//    }
//
//    private var disposable: Disposable? = null
//
//    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
//
//    private var googleApiClient: GoogleApiClient? = null
//    private val REQUEST_CODE = 1000
//
//    private lateinit var mLocationRequest:LocationRequest
//
//    private lateinit var ref: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_order_list)
//
//        googleApiClient = GoogleApiClient.Builder(this)
//            .addConnectionCallbacks(this)
//            .addOnConnectionFailedListener(this)
//            .addApi(LocationServices.API)
//            .build()
//
//        loadOrderFromDatabase()
//
//        ref = FirebaseDatabase.getInstance().getReference("Orders")
//        ref.addValueEventListener(object: ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                Log.d("err", p0.message)
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                loadOrderFromDatabase()
//            }
//
//        })
//    }
//
//    private fun loadOrderFromDatabase() {
//        disposable = orderAPI.getWaitingOrder()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
//            .subscribe(
//                {
//                    result -> onLoadOrderFinish(result.orderList)
//                },
//                {
//                    err -> Log.d("err", err.message)
//                }
//            )
//    }
//
//    private fun onLoadOrderFinish(order: ArrayList<Order>?) {
//        Log.d("data", order.toString())
//        val orderAdapter = OrderListAdapter(order!!)
//
//        orderList.apply {
//            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//            adapter = orderAdapter
//        }
//
//        orderAdapter.onItemClick = { order ->
//            disposable = orderAPI.updateEmployeeDelivery(order.order_id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//                .doOnTerminate { loadingOrder.visibility = View.GONE }
//                .subscribe(
//                    {
//                            updateToFirebase(order)
//                    },
//                    {
//                            err -> Log.d("err", err.message)
//                    }
//                )
//        }
//    }
//
//    private fun updateToFirebase(order: Order) {
//        loadOrderFromDatabase()
//
//        val refDelete = FirebaseDatabase.getInstance().getReference("Orders").child(order.order_name)
//        refDelete.removeValue()
//
//        val refDelivery = FirebaseDatabase.getInstance().getReference("Delivery")
//
//        val orderList = LatLngFB(mLatitude, mLongitude)
//
//        refDelivery.child(order.order_name).setValue(orderList).addOnSuccessListener {
//            Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if(googleApiClient != null) {
//            googleApiClient!!.connect()
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        if(googleApiClient != null) {
//            googleApiClient!!.disconnect()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        disposable?.dispose()
//    }
//
//    override fun onConnected(p0: Bundle?) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//        startLocationUpdate()
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//
//        fusedLocationProviderClient.lastLocation
//            .addOnSuccessListener {
//                location: Location ->
//
//                mLatitude = location.latitude
//                mLongitude = location.longitude
//            }
//    }
//
//    override fun onConnectionSuspended(p0: Int) {
//        googleApiClient!!.connect()
//    }
//
//    override fun onConnectionFailed(p0: ConnectionResult) {
//        googleApiClient!!.disconnect()
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when(requestCode) {
//            REQUEST_CODE -> {
//                if(grantResults.isNotEmpty()) {
//                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun startLocationUpdate() {
//        mLocationRequest = LocationRequest.create()
//            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//            .setInterval(5000)
//            .setFastestInterval(5000)
//    }
//}
