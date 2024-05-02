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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Profile

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
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var confirmPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)
        openLoginPage()

        firstName = findViewById(R.id.edtRegisterName)
        surname = findViewById(R.id.edtRegisterSurname)
        emailEdit = findViewById(R.id.edtRegisterEmail)
        passwordEdit = findViewById(R.id.edtRegisterPassword)
        confirmPassword = findViewById(R.id.edtRegisterConfirmPassword)
        registerButton = findViewById(R.id.txtRegister)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Profile")

        //Firebase authentication
        authReg = Firebase.auth
        registerButton.setOnClickListener()
        {
            val name = firstName.text.toString()
            val surname = surname.text.toString()
            val email = emailEdit.text.toString()
            val password = passwordEdit.text.toString()
            val confirm = confirmPassword.text.toString()
            if(password != confirm){
                passwordEdit.setText("")
                confirmPassword.setText("")
                Toast.makeText(this, "Password does not match!", Toast.LENGTH_SHORT).show()
            }
            else{
                RegisterUser(email, password, name, surname)
            }
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

    private fun RegisterUser(email: String, password: String, name: String, surname: String) {
        authReg.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Registration Successful", Toast.LENGTH_LONG).show()

                    val user = authReg.currentUser
                    val uid = user?.uid
                    if(user != null){

                        val userProfile = Profile(uid, name, surname, email, "", "", "", "")
                        addUserToFirebase(userProfile, user)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                }
            }
    }

    private fun addUserToFirebase(userProfile: Profile, user: FirebaseUser) {
        val uid = user.uid

        firebaseRef.child(uid).setValue(userProfile)
            .addOnSuccessListener {
                val intent = Intent(this@RegisterPage, LoginPage::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(baseContext, "Failed to add profile to database", Toast.LENGTH_SHORT).show()
            }
    }


}