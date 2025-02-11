package org.ttlzmc.core.api

import org.json.JSONObject
import java.net.URI

class ModVersion(apiResponse: JSONObject) {
    val base62version = apiResponse.getString("id")
    val versionNumber = apiResponse.getString("version_number")

    val projectId = apiResponse.getString("project_id")

    val dowloadLink = URI.create(apiResponse.getJSONArray("files")
        .getJSONObject(0).getString("url")).toURL()
}