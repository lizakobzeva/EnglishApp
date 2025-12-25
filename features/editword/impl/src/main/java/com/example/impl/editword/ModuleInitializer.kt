package com.example.impl.editword

import android.content.Context
import com.example.api.EditWordApi
import com.example.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

internal class ModuleInitializer: AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(
            module {
                single<EditWordApi> { EditWordApiImpl() }
            }
        )
    }
}

