package app.swapper.com.swapper.di

import app.swapper.com.swapper.di.module.BaseModule
import app.swapper.com.swapper.ui.activity.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [(BaseModule::class)])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [(BaseModule::class)])
    internal abstract fun bindCreateNewActivity(): CreateNewItemActivity
//
    @ContributesAndroidInjector(modules = [(BaseModule::class)])
    internal abstract fun bindDetailActivity(): DetailItemActivity

    @ContributesAndroidInjector(modules = [(BaseModule::class)])
    internal abstract fun bindHistoryActivity(): HistoryActivity
//
    @ContributesAndroidInjector(modules = [(BaseModule::class)])
    internal abstract fun bindLoginActivity(): LoginActivity
//
    @ContributesAndroidInjector(modules = [(BaseModule::class)])
    internal abstract fun bindUserItemsActivity(): UserItemsActivity
}