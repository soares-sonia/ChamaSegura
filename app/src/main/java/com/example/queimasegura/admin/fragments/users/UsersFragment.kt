package com.example.queimasegura.admin.fragments.users

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.queimasegura.R
import com.example.queimasegura.admin.fragments.users.adapter.UserAdapter
import com.example.queimasegura.admin.fragments.users.model.User
import com.example.queimasegura.retrofit.repository.AdminRepository
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.entities.Auth

class UsersFragment : Fragment() {
    private lateinit var viewModel: UsersViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var userList: MutableList<User>
    private lateinit var authUser: Auth

    private var isPermissionMode = false
    private var isDeleteMode = false
    private var isBanMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModels()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)

        initVariables(view)

        initEvents(view)

        initObservers(view)

        return view
    }

    private fun initViewModels() {
        val repository = Repository()
        val adminRepository = AdminRepository()
        val viewModelFactory = UsersViewModelFactory(requireActivity().application, repository, adminRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[UsersViewModel::class.java]
    }

    private fun initVariables(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewUserTable)
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun initEvents(view: View) {
        view.findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            showAddRuleFragment()
        }

        val searchView = view.findViewById<SearchView>(R.id.searchViewUsers)

        setSearchViewTextColor(searchView, R.color.black)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filterBySearchQuery(newText ?: "")
                return true
            }
        })

        val filter: ImageButton = view.findViewById(R.id.imageButtonFilterUsers)
        filter.setOnClickListener {
            handleFilterMenu(filter, R.menu.filter_users_admin)
        }

        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
        val buttonBan: Button = view.findViewById(R.id.buttonBanUnban)
        val buttonEditPerms: Button = view.findViewById(R.id.buttonEditUsers)
        buttonDelete.setOnClickListener {
            isDeleteMode = !isDeleteMode
            if(isDeleteMode) {
                buttonDelete.setText(R.string.cancel_btn)
                buttonBan.setText(R.string.ban_btn)
                isBanMode = false
                buttonEditPerms.setText(R.string.users_edit_btn)
                isPermissionMode = false
                Toast.makeText(requireContext(), R.string.admin_button_delete_message, Toast.LENGTH_SHORT).show()
            } else {
                buttonDelete.setText(R.string.delete_btn)
            }
        }

        buttonEditPerms.setOnClickListener {
            isPermissionMode = !isPermissionMode
            if(isPermissionMode) {
                buttonEditPerms.setText(R.string.cancel_btn)
                buttonDelete.setText(R.string.cancel_btn)
                isBanMode = false
                buttonDelete.setText(R.string.delete_btn)
                isDeleteMode = false
                Toast.makeText(requireContext(), R.string.admin_button_edit_message, Toast.LENGTH_SHORT).show()
            } else {
                buttonEditPerms.setText(R.string.users_edit_btn)
            }
        }

        buttonBan.setOnClickListener {
            isBanMode = !isBanMode
            if(isBanMode) {
                buttonBan.setText(R.string.cancel_btn)
                buttonDelete.setText(R.string.delete_btn)
                isDeleteMode = false
                buttonEditPerms.setText(R.string.users_edit_btn)
                isPermissionMode = false
                Toast.makeText(requireContext(), R.string.admin_button_ban_message, Toast.LENGTH_SHORT).show()
            } else {
                buttonBan.setText(R.string.ban_btn)
            }
        }
    }

    private fun initObservers(view: View) {
        viewModel.authData.observe(viewLifecycleOwner) { auth ->
            auth?.let {
                authUser = it
                viewModel.fetchUsersData(it)
            }
        }

        viewModel.usersResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                if(it.isSuccessful) {
                    it.body()?.result?.let { result ->
                        val users = result.map { user ->
                            User(user.userId, user.fullName, user.email, user.type, user.active, user.deleted)
                        }
                        userList = users.toMutableList()
                        setupRecyclerView(view)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(view: View) {
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
        val buttonBan: Button = view.findViewById(R.id.buttonBanUnban)
        val buttonEditPerms: Button = view.findViewById(R.id.buttonEditUsers)

        adapter = UserAdapter(requireContext(), userList) { position ->
            if (isPermissionMode) {
                showPermissionDialog(position)
                isPermissionMode = false
                buttonEditPerms.setText(R.string.users_edit_btn)
            } else if (isDeleteMode) {
                val state = adapter.deleteUser(position)
                if(state) {
                    viewModel.deleteUser(authUser, userList[position].id)
                } else {
                    viewModel.restoreUer(authUser, userList[position].id)
                }
                isDeleteMode = false
                buttonDelete.setText(R.string.delete_btn)
            } else if (isBanMode) {
                val state = adapter.banUser(position)
                if(state) {
                    viewModel.unbanUser(authUser, userList[position].id)
                } else {
                    viewModel.banUser(authUser, userList[position].id)
                }
                isBanMode = false
                buttonBan.setText(R.string.ban_btn)
            }
        }
        recyclerView.adapter = adapter
    }

    private fun showPermissionDialog(position: Int) {
        val user = userList[position]

        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_edit_perms, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle(R.string.admin_title_edit_perms)

        val dialog = dialogBuilder.create()
        dialog.show()

        val radioGroup: RadioGroup = dialogView.findViewById(R.id.radioGroup)
        val radioButtonAdmin: RadioButton = dialogView.findViewById(R.id.radioButtonAdmin)
        val radioButtonManager: RadioButton = dialogView.findViewById(R.id.radioButtonManager)
        val radioButtonUser: RadioButton = dialogView.findViewById(R.id.radioButtonUser)

        when (user.type) {
            2 -> radioButtonAdmin.isChecked = true
            1 -> radioButtonManager.isChecked = true
            0 -> radioButtonUser.isChecked = true
        }

        val confirmButton: Button = dialogView.findViewById(R.id.confirmButton)
        confirmButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedRadioButton: RadioButton = dialogView.findViewById(selectedRadioButtonId)
            val selectedPermission = selectedRadioButton.text.toString()

            val userType = when (selectedPermission) {
                getString(R.string.admin) -> 2
                getString(R.string.manager) -> 1
                getString(R.string.user) -> 0
                else -> 0
            }

            Log.d("USER TYPE", userType.toString())
            Log.d("USER TYPE", selectedPermission)
            viewModel.editUserPerms(authUser, userList[position].id, userType)
            adapter.updateUserPermission(position, userType)
            dialog.dismiss()
        }
    }

    private fun showAddRuleFragment() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_add_user, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Add Rule")

        val dialog = dialogBuilder.create()
        dialog.show()

        dialogView.findViewById<ImageButton>(R.id.imageButtonDropDownType).setOnClickListener {
            handleDropDownMenu(it, R.menu.temp_dropdown_type, dialogView.findViewById(R.id.textViewType))
        }

        dialogView.findViewById<Button>(R.id.buttonConfirm).setOnClickListener {
            val username: String = dialogView.findViewById<EditText>(R.id.editTextUsername).text.toString()
            val email: String = dialogView.findViewById<EditText>(R.id.editTextEmail).text.toString()
            val type: String = dialogView.findViewById<TextView>(R.id.textViewType).text.toString()

            val isValid = handleConfirmation(username, email, type)

            if (isValid) {
                TODO()
            }
        }
    }

    private fun handleDropDownMenu(view: View, menuId: Int, textView: TextView) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(menuId, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            textView.text = item.title
            true
        }

        popupMenu.show()
    }

    private fun handleFilterMenu(view: View, menuId: Int) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(menuId, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.active -> adapter.filterByStatus(getString(R.string.active_filter))
                R.id.banned -> adapter.filterByStatus(getString(R.string.banned_filter))
                R.id.deleted -> adapter.filterByStatus(getString(R.string.del_filter))
                else -> adapter.filterByStatus("")
            }
            true
        }

        popupMenu.show()
    }

    private fun handleConfirmation(username: String, email: String, type: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || type.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields1", Toast.LENGTH_SHORT).show()
            return false
        }

        val emailExists = userList.any { user -> user.email == email }

        if (emailExists) {
            Toast.makeText(requireContext(), "Email already exists!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun setSearchViewTextColor(searchView: SearchView, color: Int) {
        val searchTextField = SearchView::class.java.getDeclaredField("mSearchSrcTextView")
        searchTextField.isAccessible = true
        val searchText = searchTextField.get(searchView) as EditText
        searchText.setTextColor(searchText.context.getColor(color))
    }
}