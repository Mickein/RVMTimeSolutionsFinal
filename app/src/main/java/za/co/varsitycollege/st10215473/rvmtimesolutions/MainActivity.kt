package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootView = findViewById(R.id.tblTimesheet) // Root view for layout listener
        bottomNavBar = findViewById(R.id.bottom_nav)


        setupWindowInsets()
        setupBottomNavigation()
        replaceFragment(DashboardFragment())

        observeKeyboardVisibility()
    }


    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBottomNavigation() {
        bottomNavBar.setItemIconTintList(null)
        bottomNavBar.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.bottom_dashboard -> replaceFragment(DashboardFragment())
                R.id.bottom_calendar -> replaceFragment(CalendarFragment())
                R.id.bottom_add -> replaceFragment(AddFragment())
                R.id.bottom_timesheet -> replaceFragment(TimesheetFragment())
                R.id.bottom_profile -> replaceFragment(ProfileFragment())
                else -> false
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    private fun observeKeyboardVisibility() {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            bottomNavBar.visibility = if (keypadHeight > screenHeight * 0.15) View.GONE else View.VISIBLE
        }
    }
}
