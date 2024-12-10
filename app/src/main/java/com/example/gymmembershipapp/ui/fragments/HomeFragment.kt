package com.example.gymmembershipapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gymmembershipapp.R
import com.example.gymmembershipapp.adapter.MembershipAdapter
import com.example.gymmembershipapp.data.MembershipModel
import com.example.gymmembershipapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var list:ArrayList<MembershipModel>
    private val membershipAdapter by lazy { MembershipAdapter(onProductClick = ::onProductClick,requireContext(),list=list) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMemberShipList()
    }

    private fun setMemberShipList() {
        list=ArrayList()
        listMembership(list)

        binding.rvMembershipList.adapter=membershipAdapter
        binding.rvMembershipList.layoutManager=GridLayoutManager(requireContext(),2)
        binding.rvMembershipList.setHasFixedSize(true)
    }

    private fun listMembership(list: ArrayList<MembershipModel>){
        list.add(
            MembershipModel("Basic","3 Month","Cost: Rs.2000",
                context?.getString(R.string.benefit1)
            ))

        list.add(
            MembershipModel("Basic","3 Month","Cost: Rs.2000",
                context?.getString(R.string.benefit2)
            ))

        list.add(
            MembershipModel("Basic","12 Month","Cost: Rs.4000",
                context?.getString(R.string.benefit3)
            ))

        list.add(
            MembershipModel("Basic","6 Month","Cost: Rs.3000",
                context?.getString(R.string.benefit4)
            ))

        list.add(
            MembershipModel("Basic","3 Month","Cost: Rs.2000",
                context?.getString(R.string.benefit1)
            ))

        list.add(
            MembershipModel("Basic","3 Month","Cost: Rs.2000",
                context?.getString(R.string.benefit3)
            ))
    }

    private fun onProductClick(membershipModel: MembershipModel) {
        val bundle = Bundle().apply {
            putString("cost", membershipModel.cost)
            putString("duration", membershipModel.duration)
            putString("planName", membershipModel.planName)
            putString("benefits", membershipModel.benefits)
            putBoolean("fromCart",false)
        }
        findNavController().navigate(R.id.action_homeFragment_to_memberShipDetailFragment,bundle)
    }



}