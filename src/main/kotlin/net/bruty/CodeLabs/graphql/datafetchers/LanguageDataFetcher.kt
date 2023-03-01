package net.bruty.CodeLabs.graphql.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import net.bruty.CodeLabs.graphql.model.LanguageEntity
import org.jetbrains.exposed.sql.transactions.transaction

@DgsComponent
class LanguageDataFetcher {
    @DgsQuery
    fun availableLanguages(): List<String> {
        return transaction {
            LanguageEntity
                .all()
                .map { it.language }
                .sorted()
        }
    }
}