package app.icecreamhot.kaidelivery_employee.ui.order

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery_employee.R
import app.icecreamhot.kaidelivery_employee.data.mLatitude
import app.icecreamhot.kaidelivery_employee.data.mLongitude
import app.icecreamhot.kaidelivery_employee.firebasemodel.EmployeeStatus
import app.icecreamhot.kaidelivery_employee.firebasemodel.OrderFB
import app.icecreamhot.kaidelivery_employee.model.Order
import app.icecreamhot.kaidelivery_employee.network.EmployeeAPI
import app.icecreamhot.kaidelivery_employee.network.OrderAPI
import app.icecreamhot.kaidelivery_employee.ui.order.Alert.Dialog
import app.icecreamhot.kaidelivery_employee.ui.order.Map.MapsFragment
import app.icecreamhot.kaidelivery_employee.utils.MY_PREFS
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_list.*
import java.util.*

class OrderListFragment: Fragment(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private val orderAPI by lazy {
        OrderAPI.create()
    }
    private val employeeAPI by lazy {
        EmployeeAPI.create()
    }

    private var disposable: Disposable? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var googleApiClient: GoogleApiClient? = null
    private val REQUEST_CODE = 1000

    private lateinit var mLocationRequest: LocationRequest

    private lateinit var ref: DatabaseReference

    private lateinit var switchStatus: Switch
    private lateinit var recyclerView: RecyclerView
    private lateinit var textStatus: TextView
    private lateinit var loadingOrder: ProgressBar

    private var pref: SharedPreferences? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        pref = context?.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_order_list, container, false)
        switchStatus = view.findViewById(R.id.statusOnline)
        recyclerView = view.findViewById(R.id.orderList)
        textStatus = view.findViewById(R.id.statusText)
        loadingOrder = view.findViewById(R.id.loadingOrder)

        googleApiClient = GoogleApiClient.Builder(activity!!.applicationContext)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        if(googleApiClient != null) {
            googleApiClient!!.connect()
        }

        val connectivityManager=activity?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        if(networkInfo!=null && networkInfo.isConnected) {
            val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
            connectedRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    if (connected) {
                        Log.d("connected", "connected")
                    } else {
                        Log.d("connected", "not connected")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("connected", "Listener was cancelled")
                }
            })

        } else {
            Log.d("connected", "truegorhere")
        }
        checkOrderNowIsExist()

        return view
    }

    private fun checkOrderNowIsExist() {
        val token = pref?.getString("token", null)
        token?.let {
            disposable = orderAPI.getDeliveryNow(it)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
                .doOnTerminate { loadingOrder.visibility = View.GONE }
                .subscribe(
                    {
                            result ->
                        if(result.orderList?.get(0)?.order_name != null) {
                            val mapsFragment = MapsFragment()
                            val fm = fragmentManager
                            fm?.beginTransaction()
                                ?.replace(R.id.contentContainer, mapsFragment)
                                ?.commitAllowingStateLoss()
                        } else {
                            loadOrderFromDatabase()
                            loadOrderRealtime()
                            loadStatusRealtime()
                            changeStatusOnlineOffline()
                        }
                    },
                    {
                            err -> err.printStackTrace()
                    }
                )
        }
    }

    private fun loadStatusRealtime() {
        ref = FirebaseDatabase.getInstance().getReference().child("Employees")
        ref.child("123").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("error", p0.toString())
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.value != null) {
                    statusOnline.isChecked = true
                    textStatus.text = activity?.getString(R.string.online)
                    textStatus.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorSuccess))
                } else {
                    statusOnline.isChecked = false
                    textStatus.text = activity?.getString(R.string.offline)
                    textStatus.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent))
                }
            }

        })
    }

    private fun changeStatusOnlineOffline() {
        val token = pref?.getString("token", null)
        switchStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            ref = FirebaseDatabase.getInstance().getReference("Employees")
            if(isChecked) {
                disposable = employeeAPI.updateEmployeeStatus(1, token!!)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
                    .doOnTerminate { loadingOrder.visibility = View.GONE }
                    .subscribe(
                        {
                                result -> if(result.status) {
                                val workingStatus = EmployeeStatus("online", 0)
                                ref.child("123").setValue(workingStatus).addOnCompleteListener {
                                    textStatus.text = activity?.getString(R.string.online)
                                    textStatus.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorSuccess))
                                }
                            }
                        },
                        {
                                err -> Log.d("errorja", err.message)
                        }
                    )
            } else {
                disposable = employeeAPI.updateEmployeeStatus(0, token!!)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
                    .doOnTerminate { loadingOrder.visibility = View.GONE }
                    .subscribe(
                        {
                                result -> if(result.status) {
                                ref.child("123").removeValue().addOnCompleteListener {
                                    textStatus.text = activity?.getString(R.string.offline)
                                    textStatus.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent))
                                }
                        }
                        },
                        {
                                err -> Log.d("errorja", err.message)
                        }
                    )
            }
        }
    }

    private fun loadOrderRealtime() {
        ref = FirebaseDatabase.getInstance().getReference("Orders")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("err", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                loadOrderFromDatabase()
            }

        })
    }

    private fun loadOrderFromDatabase() {
        disposable = orderAPI.getWaitingOrder()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
            .doOnTerminate { loadingOrder.visibility = View.GONE }
            .subscribe(
                {
                        result -> onLoadOrderFinish(result.orderList)
                },
                {
                        err -> Log.d("err", err.message)
                }
            )
    }

    private fun onLoadOrderFinish(order: ArrayList<Order>?) {
        Log.d("data", order.toString())
        val orderAdapter = OrderListAdapter(order!!)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = orderAdapter
        }

        orderAdapter.onItemClick = { order ->
            val token = pref?.getString("token", null)
            token?.let {
                disposable = orderAPI.updateEmployeeDelivery(order.order_id, it)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
                    .doOnTerminate { loadingOrder.visibility = View.GONE }
                    .subscribe(
                        {
                            updateToFirebase(order)
                        },
                        {
                                err -> Log.d("err", err.message)
                        }
                    )
            }
        }

        orderAdapter.onDeclineClick = { order ->
            val transaction = fragmentManager
            transaction?.beginTransaction()
                ?.replace(R.id.contentContainer, CancelOrderFragment.newInstance(order.order_id, order.order_name))
                ?.addToBackStack(null)
                ?.commit()
        }

        orderAdapter.onPreviousQueueClick = { orderId ->
            disposable = orderAPI.updatePreviousQueue(orderId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
                .doOnTerminate { loadingOrder.visibility = View.GONE }
                .subscribe(
                    {
                            result ->
                                if(result.status) {
                                    loadOrderFromDatabase()
                                    Toast.makeText(context, "Set Queue Success", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Set Queue Fail", Toast.LENGTH_LONG).show()
                                }
                    },
                    {
                            err -> Log.d("err", err.message)
                    }
                )
        }

        orderAdapter.onNextQueueClick = { orderId, orderQueue ->
            disposable = orderAPI.updateNextQueue(orderId, orderQueue!!)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
                .doOnTerminate { loadingOrder.visibility = View.GONE }
                .subscribe(
                    {
                            result ->
                        if(result.status) {
                            loadOrderFromDatabase()
                            Toast.makeText(context, "Set Queue Success", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Set Queue Fail", Toast.LENGTH_LONG).show()
                        }
                    },
                    {
                            err -> Log.d("err", err.message)
                    }
                )
        }
    }

    private fun updateToFirebase(order: Order) {
        val token = pref?.getString("token", null)
        token?.let {
            disposable = employeeAPI.updateEmployeeStatus(1, it)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
                .doOnTerminate { loadingOrder.visibility = View.GONE }
                .subscribe(
                    {
                            result -> acceptOrder(order.order_name)
                    },
                    {
                            err -> Log.d("errorja", err.message)
                    }
                )
        }
    }



    private fun acceptOrder(order_name: String) {
        val refDelete = FirebaseDatabase.getInstance().getReference("Orders").child(order_name)
        refDelete.removeValue().addOnSuccessListener {
            val createChannelChat = FirebaseDatabase.getInstance().getReference("Chats")

            createChannelChat.setValue(order_name).addOnSuccessListener {
                val refDelivery = FirebaseDatabase.getInstance().getReference("Delivery")

                val orderList = OrderFB(mLatitude, mLongitude, 123, 1)

                refDelivery.child(order_name).setValue(orderList).addOnSuccessListener {
                    Toast.makeText(activity!!.applicationContext, "Success", Toast.LENGTH_SHORT).show()
                    val updateWorkingStatus = FirebaseDatabase.getInstance().getReference("Employees")

                    updateWorkingStatus.child("123").child("status").setValue(1).addOnSuccessListener {
                        val mapsFragment = MapsFragment()
                        val fm = fragmentManager
                        fm?.beginTransaction()
                            ?.replace(R.id.contentContainer, mapsFragment)
                            ?.commit()
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        if(googleApiClient != null) {
            googleApiClient!!.disconnect()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        startLocationUpdate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                    location: Location ->

                mLatitude = location.latitude
                mLongitude = location.longitude
            }
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
                        Toast.makeText(activity!!.applicationContext, "permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity!!.applicationContext, "permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun startLocationUpdate() {
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5000)
            .setFastestInterval(5000)
    }
}