package com.example.chathub.model.module

import com.example.chathub.model.AccountService
import com.example.chathub.model.LogService
import com.example.chathub.model.service.AccountServiceImpl
import com.example.chathub.model.service.LogServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl) : AccountService
    @Binds abstract fun provideLogService(impl: LogServiceImpl) : LogService
//    @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService
//    @Binds abstract fun provideConfigurationService(impl: ConfigurationServiceImpl): ConfigurationService
}