package com.example.currencyconverter

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.currencyconverter.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val exchangeRates = mapOf(
        "EUR" to 1.0,
        "USD" to 1.08,
        "GBP" to 0.85,
        "TND" to 3.40,
        "JPY" to 165.0,
        "CAD" to 1.48
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSpinners()
        setupKeypad()
        setupActions()
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.devises,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spDeviseSource.adapter = adapter
        binding.spDeviseCible.adapter = adapter

        if (adapter.count > 1) {
            binding.spDeviseSource.setSelection(0)
            binding.spDeviseCible.setSelection(1)
        }
    }

    private fun setupKeypad() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        for (btn in numberButtons) {
            btn.setOnClickListener {
                appendDigit(btn.text.toString())
            }
        }

        binding.btnPoint.setOnClickListener {
            appendDecimalPoint()
        }

        binding.btnClear.setOnClickListener {
            clearAmount()
        }
    }

    private fun appendDigit(digit: String) {
        val current = binding.tvMontant.text.toString()
        if (current == "0") {
            binding.tvMontant.text = digit
        } else {
            binding.tvMontant.text = current + digit
        }
    }

    private fun appendDecimalPoint() {
        val current = binding.tvMontant.text.toString()
        if (!current.contains(".")) {
            binding.tvMontant.text = current + "."
        }
    }

    private fun clearAmount() {
        binding.tvMontant.text = "0"
        binding.tvResultat.text = ""
    }

    private fun setupActions() {
        binding.btnConvertir.setOnClickListener {
            convertCurrency()
        }

        binding.btnInverser.setOnClickListener {
            swapCurrencies()
        }
    }

    private fun swapCurrencies() {
        val sourceIndex = binding.spDeviseSource.selectedItemPosition
        val targetIndex = binding.spDeviseCible.selectedItemPosition
        binding.spDeviseSource.setSelection(targetIndex)
        binding.spDeviseCible.setSelection(sourceIndex)

        convertCurrency()
    }

    private fun convertCurrency() {
        val amountStr = binding.tvMontant.text.toString()
        val amount = amountStr.toDoubleOrNull()

        if (amount == null) {
            Toast.makeText(this, R.string.erreur_montant, Toast.LENGTH_SHORT).show()
            return
        }

        val sourceCurrency = binding.spDeviseSource.selectedItem?.toString() ?: "EUR"
        val targetCurrency = binding.spDeviseCible.selectedItem?.toString() ?: "USD"

        val sourceRate = exchangeRates[sourceCurrency] ?: 1.0
        val targetRate = exchangeRates[targetCurrency] ?: 1.0

        val amountInEur = amount / sourceRate
        val result = amountInEur * targetRate

        binding.tvResultat.text = String.format(Locale.US, "%.2f", result)
    }
}
