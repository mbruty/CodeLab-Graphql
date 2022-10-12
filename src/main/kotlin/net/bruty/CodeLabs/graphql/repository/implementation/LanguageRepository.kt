package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.model.LanguageEntity
import net.bruty.CodeLabs.graphql.model.LanguageTable
import net.bruty.CodeLabs.graphql.repository.interfaces.ILanguageRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component

@Component
class LanguageRepository: ILanguageRepository {
    override fun getQueueNameByLanguage(language: String): String {
        return transaction {
            val x = LanguageEntity.find { LanguageTable.language eq language }
                .firstOrNull() ?: throw NotFoundException();
            x.queueIdentifier
        }
    }
}