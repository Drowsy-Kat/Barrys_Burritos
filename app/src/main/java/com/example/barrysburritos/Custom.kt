package com.example.barrysburritos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider



class Custom : Fragment() {

    private lateinit var customCartViewModel: CustomCartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customCartViewModel = ViewModelProvider(requireActivity())[CustomCartViewModel::class.java]



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_custom, container, false)

        val addToCartButton: Button = view.findViewById(R.id.addToCartCustom)

        customCartViewModel.cartItems.observe(viewLifecycleOwner) { cartItemsList ->
            val cartSize = cartItemsList.size.toString()
            Toast.makeText(requireContext(), cartSize, Toast.LENGTH_SHORT).show()
        }





        addToCartButton.setOnClickListener {
            if (validateSelections(view)) {
                val proteinChoice: String = getSelectedRadioButtonText(view.findViewById(R.id.radioGroupProtein))
                val riceChoice: String = getSelectedRadioButtonText(view.findViewById(R.id.radioGroupRice))
                val beansChoice: String = getSelectedRadioButtonText(view.findViewById(R.id.radioGroupBeans))

                val guacamole: Boolean = view.findViewById<CheckBox>(R.id.guacamoleCheckBox).isChecked
                val sourCream: Boolean = view.findViewById<CheckBox>(R.id.sourCreamCheckBox).isChecked
                val cheese: Boolean = view.findViewById<CheckBox>(R.id.cheeseCheckBox).isChecked
                val salsa: Boolean = view.findViewById<CheckBox>(R.id.salsaCheckBox).isChecked

                val size: String = getSelectedRadioButtonText(view.findViewById(R.id.radioGroupSize))

                val quantity: Int = view.findViewById<EditText>(R.id.customQuantity).text.toString().toInt()

                val cartItem = CustomCartItem(
                    proteinChoice, riceChoice, beansChoice,
                    guacamole, sourCream, cheese, salsa,
                    size, quantity
                )
                customCartViewModel.addToCart(cartItem)



                Toast.makeText(requireContext(), cartItem.toString(), Toast.LENGTH_LONG).show()
                val fragmentTransaction = requireFragmentManager().beginTransaction()
                fragmentTransaction.replace(R.id.main_frame, Custom())
                fragmentTransaction.addToBackStack(null) // Optional: Add to back stack if you want to allow back navigation
                fragmentTransaction.commit()






            }
        }

        return view
    }

    private fun validateSelections(view: View): Boolean {
        val radioGroups = arrayOf(
            R.id.radioGroupProtein,
            R.id.radioGroupRice,
            R.id.radioGroupBeans,
            R.id.radioGroupSize
        )

        for (groupId in radioGroups) {
            val radioGroup = view.findViewById<RadioGroup>(groupId)
            if (radioGroup.checkedRadioButtonId == -1) {
                showToast("Please select an option in each category.")
                return false
            }
        }

        return true
    }

    private fun getSelectedRadioButtonText(radioGroup: RadioGroup): String {
        val selectedRadioButtonId: Int = radioGroup.checkedRadioButtonId
        val selectedRadioButton: RadioButton = view?.findViewById(selectedRadioButtonId)
            ?: throw IllegalStateException("RadioButton not found")
        return selectedRadioButton.text.toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }



}
