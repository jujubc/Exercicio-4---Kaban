package com.juliana.task.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.*
import com.google.firebase.database.database
import com.juliana.task.R
import com.juliana.task.data.model.Status
import com.juliana.task.data.model.Task
import com.juliana.task.databinding.FragmentDoingBinding
import com.juliana.task.ui.adapter.TaskAdapter

class DoingFragment : Fragment() {

    private var _binding: FragmentDoingBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter

    private lateinit var reference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = Firebase.database.reference
        auth = Firebase.auth

        initListeners()
        initRecyclerViewTask()
        getTask()
    }

    private fun initListeners() {
        binding.floatingActionButton2.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_formTaskFragment)
        }
    }

    private fun initRecyclerViewTask(taskList: MutableList<Task> = mutableListOf()) {

        taskAdapter = TaskAdapter(taskList)

        binding.recyclerViewTask.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerViewTask.setHasFixedSize(true)
        binding.recyclerViewTask.adapter = taskAdapter
    }

    private fun getTask() {

        reference
            .child("task")
            .child(auth.currentUser?.uid ?: "")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val taskList = mutableListOf<Task>()

                    for (ds in snapshot.children) {

                        val task = ds.getValue(Task::class.java)

                        if (task != null && task.status == Status.DOING) {
                            taskList.add(task)
                        }
                    }

                    binding.progressBar.isVisible = false

                    listEmpty(taskList)

                    taskList.reverse()

                    taskAdapter.submit(taskList)
                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(
                        requireContext(),
                        R.string.error_generic,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun listEmpty(taskList: List<Task>) {

        binding.textInfo.text =
            if (taskList.isEmpty()) {
                getString(R.string.text_list_task_empty)
            } else {
                ""
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}