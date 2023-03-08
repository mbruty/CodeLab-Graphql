package net.bruty.CodeLabs.graphql.repository.interfaces

interface IUUIDRepository <Entity, Model>  {
    fun findById(id: String): Entity?
    fun findByIdOrThrow(id: String): Entity
    fun findAll(): List<Entity>
    fun create(obj: Model): Entity
    fun update(obj: Model): Entity
}