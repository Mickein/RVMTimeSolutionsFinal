package za.co.varsitycollege.st10215473.rvmtimesolutions


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class BeginPage : AppCompatActivity() {
    lateinit var openLog: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openLoginPage(view: View?)
    {
        openLog = findViewById(R.id.txtBegin)
        openLog.setOnClickListener{
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

    }
}