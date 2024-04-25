package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterPage : AppCompatActivity() {
    //variable for going back to Login page if user has an existing account
    lateinit var openLog: TextView
    //variable for storing the users registration details
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var firstName:EditText
    private lateinit var surname: EditText
    private lateinit var registerButton: TextView
    private lateinit var authReg: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)
        openLoginPage()

        firstName = findViewById(R.id.edtRegisterName)
        surname = findViewById(R.id.edtRegisterSurname)
        emailEdit = findViewById(R.id.edtRegisterEmail)
        passwordEdit = findViewById(R.id.edtRegisterPassword)
        registerButton = findViewById(R.id.txtRegister)

        //Firebase authentication
        authReg = Firebase.auth
        registerButton.setOnClickListener()
        {
            var email = emailEdit.text.toString()
            var password = passwordEdit.text.toString()
            RegisterUser(email, password)
        }
    }
    fun openLoginPage()
    {
        openLog = findViewById(R.id.txtGoToLoginPage)
        openLog.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        })

    }

    private fun RegisterUser(email: String, password: String) {
        authReg.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
// Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Registration Successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@RegisterPage, DashboardFragment::class.java )
                    startActivity(intent)
                    finish()
                } else {
// If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                }
            }
    }

}