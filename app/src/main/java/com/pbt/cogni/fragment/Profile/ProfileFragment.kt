package com.pbt.cogni.fragment.Profile

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pbt.cogni.R
import com.pbt.cogni.model.UserDetailsData
import com.pbt.cogni.util.MyPreferencesHelper


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        setHasOptionsMenu(true)

        setProfileData(view)


        return view
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.ic_menu_logout).isVisible = true

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.main_menu, menu)
//        menu.findItem(R.id.ic_menu_logout).setVisible(true)
//
//        super.onCreateOptionsMenu(menu, inflater)
//    }

    private fun setProfileData(view: View) {
        val userdataDetails: UserDetailsData? =MyPreferencesHelper.getUser(requireContext())

        val companyname:TextView=view.findViewById(R.id.eCompanyName)
        val username:TextView=view.findViewById(R.id.txtUserName)
        val email:TextView=view.findViewById(R.id.eEmaile)
        val mobile:TextView=view.findViewById(R.id.eMobile)
        val landline:TextView=view.findViewById(R.id.ELandLine)
        val role:TextView=view.findViewById(R.id.eRole)

        username.setText(userdataDetails?.FirstName + "   " + userdataDetails?.LastName)
        companyname.setText(userdataDetails?.companyname)

        email.text=userdataDetails?.UserName
        role.text=userdataDetails?.Rolename
        mobile.text=userdataDetails?.Mobile
        landline.text=userdataDetails?.LastName
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}