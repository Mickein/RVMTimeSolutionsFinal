package za.co.varsitycollege.st10215473.rvmtimesolutions


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//Testing video call
class BeginPage : AppCompatActivity() {
    lateinit var openLog: TextView
    lateinit var imgclick: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.begin_page)

        imgclick = findViewById(R.id.BeginImage)
        imgclick.setOnClickListener {
            Toast.makeText(baseContext, "COMING SOON", Toast.LENGTH_SHORT,).show()
        }

        openLoginPage()

    }

    fun openLoginPage()
    {
        openLog = findViewById(R.id.txtBegin)
        openLog.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        })

    }
}