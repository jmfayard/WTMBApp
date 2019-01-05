package com.wtmberlin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wtmberlin.data.Repository
import com.wtmberlin.data.Result
import com.wtmberlin.data.Venue
import com.wtmberlin.util.exhaustive
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class CollaborationsViewModel(private val repository: Repository) : ViewModel() {
    val adapterItems = MutableLiveData<List<CollaborationsAdapterItem>>()
    private val subscriptions = CompositeDisposable()

    init {
        subscriptions.add(
            repository.venues()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDataLoaded)
        )
    }


    private fun onDataLoaded(result: Result<List<Venue>>) {
        when (result) {
            is Result.Success<List<Venue>> -> processVenues(result.data)
            is Result.Error<*> -> Timber.w(result.exception)
        }.exhaustive
    }

    private fun processVenues(list: List<Venue>) {
        adapterItems.value = list.map {
            CollaborationsAdapterItem(it.id, it.name)
        }
    }

    override fun onCleared() {
        super.onCleared()

        subscriptions.clear()
    }
}