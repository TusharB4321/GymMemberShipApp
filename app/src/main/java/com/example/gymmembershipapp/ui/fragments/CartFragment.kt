package com.example.gymmembershipapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gymmembershipapp.R
import com.example.gymmembershipapp.adapter.MembershipAdapter
import com.example.gymmembershipapp.data.MembershipModel
import com.example.gymmembershipapp.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var list:ArrayList<MembershipModel>
    private val membershipAdapter by lazy { MembershipAdapter(onProductClick = ::onProductClick,requireContext(),list=list) }

    private fun onProductClick(membershipModel: MembershipModel) {
        val bundle = Bundle().apply {
            putString("cost", membershipModel.cost)
            putString("duration", membershipModel.duration)
            putString("planName", membershipModel.planName)
            putString("benefits", membershipModel.benefits)
            putBoolean("fromCart",true)
        }
        findNavController().navigate(R.id.action_cartFragment_to_memberShipDetailFragment,bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCartBinding.inflate(layoutInflater)
        list=ArrayList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCartList.adapter=membershipAdapter
        binding.rvCartList.layoutManager=GridLayoutManager(requireContext(),2)
        binding.rvCartList.setHasFixedSize(true)

        fetchCartData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchCartData() {
        val db=FirebaseFirestore.getInstance()
        val userId=FirebaseAuth.getInstance().currentUser?.uid

        if (userId!=null){
            db.collection("Cart Details")
                .whereEqualTo("userId",userId)
                .get()
                .addOnSuccessListener { res->
                    list.clear()

                    for (doc in res){

                        val cart=doc.toObject(MembershipModel::class.java)
                        list.add(cart)

                    }
                    membershipAdapter.notifyDataSetChanged()
                }.addOnFailureListener { e->
                    Toast.makeText(requireContext(), "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }


}