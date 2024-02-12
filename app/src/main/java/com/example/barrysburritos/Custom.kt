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
import android.widget.TextView
import android.widget.Toast

import androidx.lifecycle.ViewModelProvider



class Custom : Fragment() {

    private lateinit var customCartViewModel: CustomCartViewModel
    private var basePrice :Double = 7.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customCartViewModel = ViewModelProvider(requireActivity())[CustomCartViewModel::class.java]



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_custom, container, false)



        val checkBoxes = listOf<CheckBox>(
            view.findViewById(R.id.guacamoleCheckBox),
            view.findViewById(R.id.sourCreamCheckBox),
            view.findViewById(R.id.cheeseCheckBox),
            view.findViewById(R.id.salsaCheckBox)
        )
        val proteinRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroupProtein)
        val sizeRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroupSize)



        checkBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                updatePrice(proteinRadioGroup, checkBoxes, sizeRadioGroup)
            }
        }

        proteinRadioGroup.setOnCheckedChangeListener { _, _ ->
            updatePrice(proteinRadioGroup, checkBoxes, sizeRadioGroup)
        }

        sizeRadioGroup.setOnCheckedChangeListener { _, _ ->
            updatePrice(proteinRadioGroup, checkBoxes, sizeRadioGroup)
        }








        val addToCartButton: Button = view.findViewById(R.id.addToCartCustom)




        addToCartButton.setOnClickListener {
            if (validateSelections(view)) {

                val burritoName = view.findViewById<EditText>(R.id.burritoNameTextBox).text.toString()

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
                    burritoName,proteinChoice, riceChoice, beansChoice,
                    guacamole, sourCream, cheese, salsa,
                    size, quantity, basePrice

                )
                customCartViewModel.addToCart(cartItem)




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

        val burritoNameTextBox = view.findViewById<EditText>(R.id.burritoNameTextBox)
        if (burritoNameTextBox.text.isEmpty()) {
            showToast("Please name your burrito.")
            return false
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


    private fun updatePrice(proteinRadioGroup: RadioGroup, checkBoxes: List<CheckBox>, sizeRadioGroup: RadioGroup) {
        var newBasePrice = if (proteinRadioGroup.checkedRadioButtonId == R.id.plandBasedRadio) {
            8.0
        } else {
            7.0
        }

        checkBoxes.forEach { checkBox ->
            if (checkBox.isChecked) {
                newBasePrice += 0.5
            }
        }

        basePrice = newBasePrice


        if (sizeRadioGroup.checkedRadioButtonId == R.id.radiolarge) {
            basePrice += 1.0
        }

        val priceTextView = view?.findViewById<TextView>(R.id.priceTextBox)
        val quantity = view?.findViewById<EditText>(R.id.customQuantity)?.text.toString().toInt()
        priceTextView?.text = "£${basePrice*quantity}"

    }



}
