package com.picker.app.data.repository

import com.picker.app.data.db.PickerDao
import com.picker.app.domain.model.Picker
import javax.inject.Inject

class PickerRepository @Inject constructor(private val dao: PickerDao) {

    data class StoredPicker(val id: String, val login: String, val fullName: String, val pinHash: String)

    suspend fun findByLogin(login: String): StoredPicker? =
        dao.findByLogin(login)?.let { StoredPicker(it.id, it.login, it.fullName, it.pinHash) }
}
