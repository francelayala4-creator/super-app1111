package com.superapp.data.repository

import com.superapp.data.api.SuperApi
import com.superapp.data.api.dto.CreateListDto
import com.superapp.data.api.dto.ListItemDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepository @Inject constructor(private val api: SuperApi) {
    suspend fun myLists() = api.myLists()
    suspend fun create(name: String, items: List<ListItemDto>) = api.createList(CreateListDto(name = name, items = items))
    suspend fun get(id: String) = api.getList(id)
    suspend fun addItem(id: String, item: ListItemDto) = api.addItem(id, item)
    suspend fun deleteItem(id: String, itemId: String) = api.deleteItem(id, itemId)
}
