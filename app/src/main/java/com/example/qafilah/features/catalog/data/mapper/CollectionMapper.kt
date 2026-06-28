package com.example.qafilah.features.catalog.data.mapper

import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.graphql.storefront.GetCollectionsQuery

fun GetCollectionsQuery.Node.toDomain(): StoreCollection {
    return StoreCollection(
        id = this.id,
        title = this.title,
        handle = this.handle,
        description = this.description,
        imageUrl = this.image?.url?.toString()
    )
}