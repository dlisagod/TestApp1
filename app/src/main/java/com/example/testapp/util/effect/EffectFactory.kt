package com.example.testapp.util.effect

import com.example.testapp.util.Constant

class EffectFactory {
    companion object {
        fun createEffect(): IEffect {
            return when (Constant.EFFECT_VERSION) {
                Constant.AppEffectVersion.First -> {
                    First.getInstance()
                }

                Constant.AppEffectVersion.Second -> {
                    Second.getInstance()
                }
            }
        }
    }
}