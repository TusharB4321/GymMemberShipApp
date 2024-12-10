package com.example.gymmembershipapp.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gymmembershipapp.R
import com.example.gymmembershipapp.adapter.MembershipAdapter
import com.example.gymmembershipapp.data.MembershipModel
import com.example.gymmembershipapp.databinding.FragmentMemberShipDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class MemberShipDetailFragment : Fragment() {

    private lateinit var binding:FragmentMemberShipDetailBinding

    // These variables will hold the values passed from the HomeFragment
    private var cost: String? = null
    private var duration: String? = null
    private var planName: String? = null
    private var benefits: String? = null
    private var fromCart: Boolean = false // Flag to track source fragment


    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMemberShipDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()

        arguments?.let {
            cost = it.getString("cost")
            duration = it.getString("duration")
            planName = it.getString("planName")
            benefits = it.getString("benefits")
            fromCart = it.getBoolean("fromCart", false) // Default is false if not provided
        }

        if (fromCart){
           binding.addToCart.text="Proceed to Payment"
            proceedPayment()
            displayMembershipDetails1()
        }else{
            binding.addToCart.text="Add To Cart"
            saveCartDetails()
            displayMembershipDetails()
        }

    }

    private fun proceedPayment() {
        binding.addToCart.setOnClickListener {
            Toast.makeText(requireContext(), "Successfully Payment Proceed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCartDetails() {

        binding.addToCart.setOnClickListener {
            val cost=binding.tvCost.text.toString()
            val duration=binding.tvDuration.text.toString()
            val plan=binding.tvPlanName.text.toString()
            val benefits=binding.tvBenefits.text.toString()

            val cartModel=MembershipModel(cost=cost, duration = duration, planName = plan, benefits = benefits)

            val db = FirebaseFirestore.getInstance()

            val progressDialog=ProgressDialog(requireContext())
            progressDialog.setMessage("Adding to Cart....")
            progressDialog.setCancelable(false)
            progressDialog.show()

            db.collection("Cart Details")
                .add(cartModel)
                .addOnSuccessListener {ref->
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Successfully Added to Cart", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {e->
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }


    private fun displayMembershipDetails() {
        // Set the values received in the bundle to the UI components
        binding.tvPlanName.text ="Type: $planName"
        binding.tvCost.text ="Membership $cost"
        binding.tvDuration.text ="Time Duration:$duration"
        binding.tvBenefits.text = benefits



    }
    private fun displayMembershipDetails1() {
        // Set the values received in the bundle to the UI components
        binding.tvPlanName.text =planName
        binding.tvCost.text =cost
        binding.tvDuration.text =duration
        binding.tvBenefits.text = benefits



    }


}

