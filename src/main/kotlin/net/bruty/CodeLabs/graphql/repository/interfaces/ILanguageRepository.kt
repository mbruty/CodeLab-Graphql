package net.bruty.CodeLabs.graphql.repository.interfaces

interface ILanguageRepository {
    fun getQueueNameByLanguage(language: String): String
}