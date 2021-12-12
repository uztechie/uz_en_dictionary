package uz.techie.uzendictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.techie.uzendictionary.dialog.CustomProgressbar
import uz.techie.uzendictionary.models.Word
import uz.techie.uzendictionary.utils.Utils
import uz.techie.uzendictionaryadmin.data.DictionaryViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val viewModel: DictionaryViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var customProgressbar: CustomProgressbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customProgressbar = CustomProgressbar(this)

        loadData()

        lifecycle.coroutineScope.launch {
            viewModel.searchWordUz("%%").collect {
                if (it.isNotEmpty()){
                    customProgressbar.dismiss()
                }
            }
        }



        supportActionBar?.title = ""
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.findNavController()



        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottomNavigationView.setupWithNavController(navController)



    }


    private fun loadData() {
        customProgressbar.show()
        val list = mutableListOf<Word>()
        db.collection("words").get().addOnSuccessListener { snapshot->
            customProgressbar.dismiss()
            snapshot.forEach { childSnapshot->
                val word = childSnapshot.toObject(Word::class.java)
                list.add(word)
            }
            viewModel.insertAndDeleteWords(list)
        }.addOnFailureListener {
            customProgressbar.dismiss()
            Utils.showMessage(this, it.toString())
        }

    }

}