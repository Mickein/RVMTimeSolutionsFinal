package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var nameText: TextView
    private lateinit var surnameText: TextView
    private lateinit var emailText: TextView
    private lateinit var passwordText: TextView
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        nameText = view.findViewById(R.id.txtName2)
        surnameText = view.findViewById(R.id.edtSurname)
        emailText = view.findViewById(R.id.edtEmail)
        passwordText = view.findViewById(R.id.txtPassword)

        auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        currentUser?.let {
            val uid = currentUser.uid
            firebaseRef = FirebaseDatabase.getInstance().reference.child("Profile").child(uid)
            fetchData()
        }

        // Logout button
        val btnLogout: Button = view.findViewById(R.id.btnLogOut)
        btnLogout.setOnClickListener {
            // Sign out the user
            auth.signOut()

            // Redirect to the login page
            val intent = Intent(requireContext(), LoginPage::class.java)
            startActivity(intent)
            requireActivity().finish() // Optional: Close the current activity
        }

        return view
    }

    private fun fetchData() {
        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val surname = snapshot.child("surname").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)

                    nameText.text = name
                    surnameText.text = surname
                    emailText.text = email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }
}
 