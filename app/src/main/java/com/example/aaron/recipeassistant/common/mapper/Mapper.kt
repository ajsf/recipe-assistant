package com.example.aaron.recipeassistant.common.mapper

interface Mapper<D, M> {
    fun toModel(domain: D): M
    fun toDomain(model: M): D {
        throw UnsupportedOperationException()
    }
}