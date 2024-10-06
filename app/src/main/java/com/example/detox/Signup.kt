package com.example.detox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val usernameEditText = findViewById<EditText>(R.id.editTextText)
        val emailEditText = findViewById<EditText>(R.id.editTextText2)
        val passwordEditText = findViewById<EditText>(R.id.editTextText3)
        val signupButton = findViewById<Button>(R.id.button)

        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.length >= 8) {
                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                // You can navigate to the login screen here
            } else {
                Toast.makeText(this, "Signup failed. Please check inputs", Toast.LENGTH_SHORT).show()
            }
        }
    }
}