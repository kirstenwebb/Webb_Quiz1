package com.example.webb_quiz1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var requestQueue: RequestQueue
    private val users = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        recyclerView = findViewById(R.id.recycler_users)
        val btnFetch: Button = findViewById(R.id.btn_fetch)

        adapter = UserAdapter(users) { user ->
            Toast.makeText(this, "You selected ${user.name}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnFetch.setOnClickListener {
            fetchUsers()
        }
    }

    private fun fetchUsers() {
        val url = "https://jsonplaceholder.typicode.com/users"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val parsedUsers = mutableListOf<User>()
                    for (i in 0 until response.length()) {
                        val obj = response.getJSONObject(i)
                        val name = obj.optString("name", "N/A")
                        val email = obj.optString("email", "N/A")
                        val phone = obj.optString("phone", "N/A")
                        parsedUsers.add(User(name, email, phone))
                    }

                    adapter.updateItems(parsedUsers)
                    Toast.makeText(this, "Loaded ${parsedUsers.size} users", Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    Log.e("MainActivity", "JSON parse error", e)
                    Toast.makeText(this, "Failed to parse data.", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("MainActivity", "Volley error: ${error.message}", error)
                Toast.makeText(this, "Failed to fetch data.", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    override fun onStop() {
        super.onStop()
        requestQueue.cancelAll { true }
    }
}
