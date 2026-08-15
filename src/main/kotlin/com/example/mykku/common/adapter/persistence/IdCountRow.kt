package com.example.mykku.common.adapter.persistence

interface IdCountRow {
    fun getEntityId(): Long
    fun getCountValue(): Long
}

fun List<IdCountRow>.toCountMap(): Map<Long, Int> {
    return associate { it.getEntityId() to it.getCountValue().toInt() }
}
