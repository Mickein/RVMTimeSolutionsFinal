package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavBar: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        bottomNavBar = findViewById(R.id.bottom_nav)
        bottomNavBar.setItemIconTintList(null)
        bottomNavBar.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId)
            {
                R.id.bottom_dashboard -> {
                        replaceFragment(DashboardFragment())
                        true
                }
                R.id.bottom_calendar -> {
                    replaceFragment(CalendarFragment())
                    true
                }
                R.id.bottom_add -> {
                    replaceFragment(AddFragment())
                    true
                }
                R.id.bottom_timesheet -> {
                    replaceFragment(TimesheetFragment())
                    true
                }
                R.id.bottom_profile-> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }

        }
        replaceFragment(DashboardFragment())
    }

    private fun replaceFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}