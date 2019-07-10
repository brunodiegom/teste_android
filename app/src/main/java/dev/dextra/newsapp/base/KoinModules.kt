package dev.dextra.newsapp.base

import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.repository.EndpointService
import dev.dextra.newsapp.feature.news.NewsViewModel
import dev.dextra.newsapp.feature.sources.SourcesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val serviceModules = module {
    single { EndpointService() }
}

val repositoryModules = module {
    single { NewsRepository(get()) }
}

val viewModelModules = module {
    viewModel { SourcesViewModel(newsRepository = get()) }
    viewModel { NewsViewModel(newsRepository = get()) }
}

val appComponent: List<Module> = listOf(viewModelModules, serviceModules, repositoryModules)
