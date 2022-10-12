package net.bruty.CodeLabs.graphql

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

object Cities: IntIdTable() {
	val name = varchar("name", 50)
}

class City(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<City>(Cities)

	var name by Cities.name
}

@SpringBootApplication
class CodeLabsGraphQLApplication
fun main(args: Array<String>) {
	runApplication<CodeLabsGraphQLApplication>(*args)

	DbUtils.connect()
	DbUtils.createTables()
}