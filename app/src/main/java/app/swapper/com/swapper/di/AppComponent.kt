package app.swapper.com.swapper.di

import app.swapper.com.swapper.SwapperApp
import app.swapper.com.swapper.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityBuilder::class])
interface AppComponent: AndroidInjector<SwapperApp> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<SwapperApp>()
}