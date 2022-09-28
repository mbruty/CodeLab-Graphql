package net.bruty.comp3000graphql.repository.interfaces

interface IIntIDRepository<Entity, Model>  {
    fun findById(id: Int): Entity?
    fun findByIdOrThrow(id: Int): Entity
    fun findAll(): List<Entity>
    fun create(obj: Model): Entity
    fun update(obj: Model): Entity
}