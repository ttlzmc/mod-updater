package org.ttlzmc.core.api

import com.google.gson.JsonObject
import java.net.URI

class ModVersion(apiResponse: JsonObject) {
    val base62version = apiResponse.get("id").asString
    val versionNumber = apiResponse.get("version_number").asString

    val projectId = apiResponse.get("project_id").asString

    val dowloadLink = URI.create(apiResponse.get("files")
        .asJsonArray.get(0).asJsonObject.get("url").asString).toURL()
}