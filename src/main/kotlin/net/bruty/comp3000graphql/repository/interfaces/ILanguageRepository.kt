package net.bruty.comp3000graphql.repository.interfaces

interface ILanguageRepository {
    fun getQueueNameByLanguage(language: String): String
}