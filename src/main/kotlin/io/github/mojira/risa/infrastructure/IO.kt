package io.github.mojira.risa.infrastructure

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.infrastructure.config.Risa
import java.io.File
import java.time.Instant

fun readSnapshotPosts(mapper: ObjectMapper): Map<Snapshot, RedditPost> = with(File("posts.json")) {
    if (exists()) {
        mapper.readValue(this)
    } else {
        emptyMap()
    }
}

fun saveSnapshotPosts(mapper: ObjectMapper, map: Map<Snapshot, RedditPost>) =
    mapper.writeValue(File("posts.json"), map)

fun <K, V> Map<K, V>.add(key: K, value: V): Map<K, V> = toMutableMap().apply { put(key, value) }

fun readConfig(): Config {
    return Config { addSpec(Risa) }
        .from.yaml.watchFile("risa.yml")
        .from.yaml.watchFile("secret.yml")
        .from.env()
        .from.systemProperties()
}

class SnapshotKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(
        key: String,
        ctxt: DeserializationContext
    ) = with(key.split("#")) {
        Snapshot(this[0], Instant.ofEpochMilli(this[1].toLong()))
    }
}

class SnapshotKeySerializer : JsonSerializer<Snapshot>() {
    override fun serialize(value: Snapshot, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeFieldName(value.name + "#" + value.releasedDate.toEpochMilli())
    }
}

class SnapshotModule : SimpleModule() {
    init {
        addKeyDeserializer(
            Snapshot::class.java,
            SnapshotKeyDeserializer()
        )
        addKeySerializer(
            Snapshot::class.java,
            SnapshotKeySerializer()
        )
    }
}
