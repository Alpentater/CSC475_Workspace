package com.example.cta_7

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Tells Hilt that whenever UnitConverter is requested, DefaultUnitConverter should be supplied.
@Module
@InstallIn(SingletonComponent::class) abstract class ConversionModule {
    @Binds
    @Singleton
    abstract fun bindUnitConverter(implementation: DefaultUnitConverter): UnitConverter
}