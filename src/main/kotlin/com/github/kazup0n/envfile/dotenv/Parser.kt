package com.github.kazup0n.envfile.dotenv

import com.copperleaf.kudzu.node.choice.ChoiceNode
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.node.noop.NoopNode
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.CharNotInParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.IdentifierTokenParser
import com.copperleaf.kudzu.parser.text.OptionalWhitespaceParser
import com.intellij.openapi.diagnostic.logger
import java.io.File

@OptIn(ExperimentalStdlibApi::class)
object Parser {
    private val log = logger<Parser>()

    private val lines: ManyParser<ChoiceNode>

    init {
        val newLine = MaybeParser(ManyParser(CharInParser('\n')))
        val ws = OptionalWhitespaceParser()
        val comment = FlatMappedParser(
            SequenceParser(
                CharInParser('#'),
                ManyParser(CharNotInParser('\n')),
                newLine
            )
        ) { NoopNode(it.context) }

        val key = IdentifierTokenParser()
        val delim = CharInParser('=')
        val value = ManyParser(CharNotInParser('\n'))

        val kv = MappedParser(
            SequenceParser(
                key,
                ws,
                delim,
                ws,
                value,
                newLine
            )
        ) {
            val (k, _, _, _, v) = it.children
            k.text to v.text
        }
        val entry = ExactChoiceParser(comment, kv)
        lines = ManyParser(entry)
    }

    fun parse(envFile: File): Map<String, String> {
        val ctx = ParserContext.fromString(envFile.readText(Charsets.UTF_8))
        return parse(ctx)
    }

    internal fun parse(ctx: ParserContext): Map<String, String> {
        val parsed = lines.parse(ctx)
        val entries = parsed.first.nodeList
            .filter { it.node.astNodeName == "ValueNode" }
            .map {
                (it.children[0] as ValueNode<Pair<String, String>>).value
            }
        val result = mutableMapOf<String, String>()
        entries.forEach {
            val prev = result.put(it.first, it.second)
            if (prev != null) {
                log.warn("Key(${it.first}) is duplicated; previous value(${it.second}) was discarded")
            }
        }
        return result
    }
}
