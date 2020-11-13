package io.github.mojira.risa.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import io.github.mojira.risa.domain.RedditPost
import io.github.mojira.risa.domain.Snapshot
import io.github.mojira.risa.infrastructure.config.Risa
import java.io.File

fun previousSnapshotsPosts(mapper: ObjectMapper): Map<Snapshot, RedditPost> = with(File("posts.json")) {
    if(exists()) {
        mapper.readValue(this)
    } else {
        emptyMap()
    }
}

fun saveSnapshotPosts(mapper: ObjectMapper, map: Map<Snapshot, RedditPost>) =
    mapper.writeValue(File("posts.json"), map)

fun <K, V> Map<K, V>.add(pair: Pair<K, V>): Map<K, V> = toMutableMap().add(pair)

fun readConfig(): Config {
    return Config { addSpec(Risa) }
        .from.yaml.watchFile("risa.yml")
        .from.yaml.watchFile("secret.yml")
        .from.env()
        .from.systemProperties()
}

