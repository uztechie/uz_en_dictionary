package uz.techie.uzendictionary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.techie.uzendictionary.dialog.CustomProgressbar
import uz.techie.uzendictionary.dialog.InternetDialog
import uz.techie.uzendictionary.models.User
import uz.techie.uzendictionary.models.Version
import uz.techie.uzendictionary.models.Word
import uz.techie.uzendictionary.utils.Utils
import uz.techie.uzendictionaryadmin.data.DictionaryViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val UPDATE_CODE = 123
    private val TAG = "MainActivity"

    private val appUpdateManager:AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private lateinit var  installstateListener:InstallStateUpdatedListener

    private val db = Firebase.firestore
    lateinit var database:DatabaseReference
    private var listener:ValueEventListener? = null
    private var ref:DatabaseReference? = null

    private val viewModel: DictionaryViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var customProgressbar: CustomProgressbar

    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customProgressbar = CustomProgressbar(this)




//        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()

        auth.signInAnonymously()
            .addOnSuccessListener {
                database = Firebase.database.reference
                checkVersionAndLoad()
                Log.d("TAG", "onCreate: createUserWithEmailAndPassword success")
            }
            .addOnFailureListener {
                database = Firebase.database.reference
                checkVersionAndLoad()
                Log.e("TAG", "onCreate: createUserWithEmailAndPassword fail "+it)
            }
        val user = auth.currentUser
        Log.d("TAG", "onCreate: FirebaseAuth ${user?.uid}")




//        loadData()
//        loadUserData()



        lifecycle.coroutineScope.launch {
            viewModel.searchWordUz("%%").collect {
                if (it.isNotEmpty()){
                    customProgressbar.dismiss()
                }
                else if (it.isEmpty() && !isNetworkAvailable()){
                    val internetDialog = InternetDialog(this@MainActivity)
                    internetDialog.show()
                }
            }
        }



        supportActionBar?.title = ""
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.findNavController()


        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottomNavigationView.setupWithNavController(navController)




    }

    private fun checkAppUpdate(){

        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                try {
                    appUpdateManager.startUpdateFlowForResult(it, AppUpdateType.FLEXIBLE, this, UPDATE_CODE)

                }catch (t:Throwable){
                    Log.e("TAG", "checkAppUpdate: error ",t)
                }
            }
        }.addOnFailureListener {
            Log.e("TAG", "checkAppUpdate: error ",it)
        }


        installstateListener = object :InstallStateUpdatedListener{
            override fun onStateUpdate(state: InstallState) {
                if (state.installStatus() == InstallStatus.DOWNLOADED){
                    Log.d("TAG", "onStateUpdate: DOWNLOADED")
                    val snackbar = Snackbar.make(findViewById(android.R.id.content), "Update downloaded", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Install"){updateApp()}

//                snackbar.setTextColor(Color.BLACK)
//                snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.show()
                }
                else if (state.installStatus() == InstallStatus.INSTALLED){
                    appUpdateManager.unregisterListener(installstateListener)
                    Log.d("TAG", "onStateUpdate: Installed")
                }
                else{
                    Log.i("TAG", "InstallStateUpdatedListener: state: " + state.installStatus())
                }
            }

        }

        appUpdateManager.registerListener (installstateListener)





    }

    private fun updateApp() {
        appUpdateManager.completeUpdate().addOnSuccessListener {
            Log.d(TAG, "updateApp:  success")
        }
            .addOnFailureListener {
                Log.e(TAG, "updateApp: failed ", it)
            }
    }

    private fun loadData() {
        customProgressbar.show()
        val list = mutableListOf<Word>()
        listener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                customProgressbar.dismiss()
                snapshot.children.forEach { childSnapshot->
                    val word = childSnapshot.getValue(Word::class.java)
                    if (word != null) {
                        list.add(word)
                    }
                }
                Log.d("TAG", "onDataChange: list size "+list.size)
                viewModel.insertAndDeleteWords(list)
                ref?.removeEventListener(this)

            }

            override fun onCancelled(error: DatabaseError) {
                customProgressbar.dismiss()
                Utils.showMessage(this@MainActivity, error.message)
                ref?.removeEventListener(this)
            }

        }
        ref = database.child("words")
        ref?.addValueEventListener(listener!!)

    }

    private fun loadUserData1(){
        val ref = Firebase.database.reference.child("users").child("user1")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TAG", "onDataChange: userdata ")
                if (snapshot.exists()){
                    val user = User(1)
                    user.full_name = snapshot.child("name").value.toString()
                    user.email = snapshot.child("email").value.toString()
                    user.phone = snapshot.child("phone").value.toString()
                    viewModel.insertUser(user)

                    ref.removeEventListener(this)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled:userdata ", error.toException())
                ref.removeEventListener(this)
            }

        })

    }

    private fun loadUserData2(){
        val ref = Firebase.database.reference.child("users").child("developer")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TAG", "onDataChange: userdata ")
                if (snapshot.exists()){
                    val user = User(2)
                    user.full_name = snapshot.child("name").value.toString()
                    user.email = snapshot.child("email").value.toString()
                    user.phone = snapshot.child("phone").value.toString()
                    viewModel.insertUser(user)

                    ref.removeEventListener(this)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled:userdata ", error.toException())
                ref.removeEventListener(this)
            }

        })

    }


    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }


    fun checkVersionAndLoad(){
        val ref = Firebase.database.reference.child("version").get()
            .addOnSuccessListener { snapshot->
                if (snapshot.exists()){
                    val versionLong:Long = snapshot.child("version").value as Long
                    Log.d("TAG", "checkVersionAndLoad: version "+versionLong)


                    if (viewModel.getVersion().isNotEmpty()){
                        val localVersion = viewModel.getVersion()[0]
                        Log.d("TAG", "checkVersionAndLoad: versionlocal "+localVersion)
                        if (versionLong != localVersion.version){
                            loadData()
                            loadUserData1()
                            loadUserData2()
                            val version = Version(1, versionLong)
                            viewModel.insertVersion(version)
                        }
                    }
                    else{
                        loadData()
                        loadUserData1()
                        loadUserData2()
                        val version = Version(1, versionLong)
                        viewModel.insertVersion(version)
                        Log.d("TAG", "checkVersionAndLoad: versionlocal empty")
                    }






                }
            }
            .addOnFailureListener {
                Utils.showMessage(this, it.message!!)
            }
    }

    override fun onStart() {
        super.onStart()


    }

    override fun onResume() {
        super.onResume()
        checkAppUpdate()

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "aaaa onStop: ")
        appUpdateManager.unregisterListener(installstateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "aaaa onDestroy: ")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_CODE){
            if (resultCode != RESULT_OK){
                Log.d("TAG", "onActivityResult: "+data.toString())
            }
        }
    }




}