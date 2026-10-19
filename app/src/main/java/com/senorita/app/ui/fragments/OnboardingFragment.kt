package com.senorita.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.senorita.app.R

class OnboardingFragment(
    private val onOnboardingComplete: () -> Unit = {}
) : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: MaterialButton
    private lateinit var tvSkip: TextView
    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View

    private val pages = listOf(
        OnboardingPageModel(
            title = "Pesan favoritmu dengan mudah.",
            description = "Pilih hidangan, atur pesanan, dan nikmati waktu bersama keluarga—semuanya dari satu aplikasi.",
            imageResEmoji = "🍱",
            buttonText = "Lanjut"
        ),
        OnboardingPageModel(
            title = "Nikmati promo spesial setiap hari.",
            description = "Dapatkan harga istimewa, menu bundling, dan kejutan eksklusif yang hanya tersedia di aplikasi SENORITA.",
            imageResEmoji = "🎁",
            buttonText = "Lanjut"
        ),
        OnboardingPageModel(
            title = "Setiap pesanan, dapatkan lebih.",
            description = "Kumpulkan SENORITA Points dari setiap transaksi lalu tukarkan dengan menu dan hadiah favoritmu.",
            imageResEmoji = "👑",
            buttonText = "Mulai"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPagerOnboarding)
        btnNext = view.findViewById(R.id.btnNext)
        tvSkip = view.findViewById(R.id.tvSkip)
        dot1 = view.findViewById(R.id.dot1)
        dot2 = view.findViewById(R.id.dot2)
        dot3 = view.findViewById(R.id.dot3)

        viewPager.adapter = OnboardingAdapter(pages)

        tvSkip.setOnClickListener {
            onOnboardingComplete()
        }

        btnNext.setOnClickListener {
            if (viewPager.currentItem < pages.size - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                onOnboardingComplete()
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDotsAndButton(position)
            }
        })
    }

    private fun updateDotsAndButton(position: Int) {
        btnNext.text = if (position == pages.size - 1) "Mulai" else "Lanjut"

        dot1.setBackgroundResource(if (position == 0) R.drawable.dot_active else R.drawable.dot_inactive)
        dot2.setBackgroundResource(if (position == 1) R.drawable.dot_active else R.drawable.dot_inactive)
        dot3.setBackgroundResource(if (position == 2) R.drawable.dot_active else R.drawable.dot_inactive)

        val params1 = dot1.layoutParams
        params1.width = if (position == 0) 48 else 16
        dot1.layoutParams = params1

        val params2 = dot2.layoutParams
        params2.width = if (position == 1) 48 else 16
        dot2.layoutParams = params2

        val params3 = dot3.layoutParams
        params3.width = if (position == 2) 48 else 16
        dot3.layoutParams = params3
    }
}
