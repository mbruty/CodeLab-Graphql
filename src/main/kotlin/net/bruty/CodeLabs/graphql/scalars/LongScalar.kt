package net.bruty.CodeLabs.graphql.scalars

import com.netflix.graphql.dgs.DgsScalar
import graphql.language.IntValue
import graphql.schema.Coercing

// Adapted from the DGS docs
@DgsScalar(name="Long")
class LongScalar: Coercing<Long, String> {
    /**
     * Called to convert a Java object result of a DataFetcher to a valid runtime value for the scalar type.
     *
     *
     * Note : Throw [graphql.schema.CoercingSerializeException] if there is fundamental
     * problem during serialisation, don't return null to indicate failure.
     *
     *
     * Note : You should not allow [java.lang.RuntimeException]s to come out of your serialize method, but rather
     * catch them and fire them as [graphql.schema.CoercingSerializeException] instead as per the method contract.
     *
     * @param dataFetcherResult is never null
     *
     * @return a serialized value which may be null.
     *
     * @throws graphql.schema.CoercingSerializeException if value input can't be serialized
     */
    override fun serialize(dataFetcherResult: Any): String {
        val data = dataFetcherResult as Long
        return data.toString()
    }

    /**
     * Called to resolve an input from a query variable into a Java object acceptable for the scalar type.
     *
     *
     * Note : You should not allow [java.lang.RuntimeException]s to come out of your parseValue method, but rather
     * catch them and fire them as [graphql.schema.CoercingParseValueException] instead as per the method contract.
     *
     * @param input is never null
     *
     * @return a parsed value which is never null
     *
     * @throws graphql.schema.CoercingParseValueException if value input can't be parsed
     */
    override fun parseValue(input: Any): Long {
        val inputStr = input.toString()
        return inputStr.toLong()
    }

    /**
     * Called during query validation to convert a query input AST node into a Java object acceptable for the scalar type.  The input
     * object will be an instance of [graphql.language.Value].
     *
     *
     * Note : You should not allow [java.lang.RuntimeException]s to come out of your parseLiteral method, but rather
     * catch them and fire them as [graphql.schema.CoercingParseLiteralException] instead as per the method contract.
     *
     * @param input is never null
     *
     * @return a parsed value which is never null
     *
     * @throws graphql.schema.CoercingParseLiteralException if input literal can't be parsed
     */
    override fun parseLiteral(input: Any): Long {
        return (input as IntValue).value.toLong()
    }

}